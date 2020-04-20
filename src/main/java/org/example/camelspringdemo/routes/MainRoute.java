package org.example.camelspringdemo.routes;

import bitronix.tm.TransactionManagerServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.example.camelspringdemo.config.ActiveMQConfig;
import org.example.camelspringdemo.model.ArticlesEntity;
import org.example.camelspringdemo.repository.ArticlesRepository;
import org.example.camelspringdemo.util.MessageCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MainRoute extends RouteBuilder {

    private String srcDirPath;
    private ArticlesRepository articlesRepository;
    private String mailHost;
    private String mailPort;
    private String mailLogin;
    private String mailPassword;
    private String onHundredMailTo;
    private Integer msgThreshold;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private MessageCounter messageCounter;
    private final ActiveMQComponent activeMQComponent;
    private final SpringTransactionPolicy mandatoryPolicy;
    private final SpringTransactionPolicy requiredPolicy;

    @Autowired
    public MainRoute(@Value("${application.srcDirPath:srcDirPath}") String srcDirPath,
                     MessageCounter messageCounter,
                     ArticlesRepository articlesRepository,
                     @Value("${application.mail.host}") String mailHost,
                     @Value("${application.mail.port}") String mailPort,
                     @Value("${application.mail.login}") String mailLogin,
                     @Value("${application.mail.password}") String mailPassword,
                     @Value("${application.mail.onHundredMailTo}") String onHundredMailTo,
                     @Value("${application.msgThreshold:100}") Integer msgThreshold,
                     @Value("${spring.datasource.driverClassName}") String dbDriver,
                     @Value("${spring.datasource.url}") String dbUrl,
                     @Value("${spring.datasource.username}") String dbUsername,
                     @Value("${spring.datasource.password}") String dbPassword,
                     @Qualifier("MANDATORY") SpringTransactionPolicy mandatory,
                     @Qualifier("REQUIRED") SpringTransactionPolicy required,
                     ActiveMQComponent activemq
                     ) {
        this.srcDirPath = srcDirPath;
        this.messageCounter = messageCounter;
        this.articlesRepository = articlesRepository;
        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.mailLogin = mailLogin;
        this.mailPassword = mailPassword;
        this.onHundredMailTo = onHundredMailTo;
        this.msgThreshold = msgThreshold;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.activeMQComponent = activemq;
        this.mandatoryPolicy = mandatory;
        this.requiredPolicy = required;
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            log.error("Could not load dbDriver for class " + dbDriver, e);
        }
    }

    @Override
    public void configure() {
        CamelContext context = new DefaultCamelContext();
//        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("vm://localhost?broker.persistent=false"));
        context.addComponent("activemq", activeMQComponent);


        from("file://" + srcDirPath + "?autoCreate=true&move=.done&readLock=markerFile")
//            .transacted()
            .log(LoggingLevel.DEBUG, "START OF ROUTE")
            .transacted("REQUIRED")
            .setHeader("FileExtension").simple("${file:name.ext.single}")
            .choice()
            // if txt file detected
            .when(header("FileExtension").isEqualTo("txt"))
                .bean(messageCounter, "incTxtCounter()")
                .to("activemq:queue:" + ActiveMQConfig.TXT_QUEUE)
            // if xml file detected
            .when(header("FileExtension").isEqualTo("xml"))
                .bean(messageCounter, "incXmlCounter()")
                .setHeader("testHeader").constant("testHeaderVal")
                .multicast().to("activemq:queue:" + ActiveMQConfig.XML_QUEUE, "direct:xslt")
                .endChoice()
            // if non-txt, non-xml file detected
            .otherwise()
                .bean(messageCounter, "incOtherCounter()")
                .log(LoggingLevel.DEBUG, ActiveMQConfig.ERROR_QUEUE + " >>> ${body}")
                .to("activemq:queue:" + ActiveMQConfig.ERROR_QUEUE)
            .end()
            .to("direct:checkMail");

        from("activemq:queue:" + ActiveMQConfig.TXT_QUEUE)
            .log(LoggingLevel.DEBUG, ActiveMQConfig.TXT_QUEUE + " >>> ${body}")
            .log(LoggingLevel.INFO, "txtFilesLog", ActiveMQConfig.TXT_QUEUE + " >>> ${body}");

        from("activemq:queue:" + ActiveMQConfig.XML_QUEUE)
            .policy(requiredPolicy)
            .unmarshal().jacksonxml(ArticlesEntity.class)
            .log(LoggingLevel.DEBUG, ActiveMQConfig.XML_QUEUE + " >>> ${body}")
            .process(this::processXml);

        from("direct:checkMail")
            .choice()
            .when(method(messageCounter, "getTotalCount()").isGreaterThanOrEqualTo(msgThreshold))
                .setHeader("to").simple(onHundredMailTo)
                .setHeader("from").simple(mailLogin)
                .setHeader("subject").simple("CamelSpringDemo: 100 msgs reached")
                .setBody().method(messageCounter, "getCounters()")
                .to("smtp://" + mailHost + ":" + mailPort + "?username=" + mailLogin + "&password=" + mailPassword + "&mail.smtp.auth=true")
                .bean(MessageCounter.class, "resetCounters()")
            .end();

        from("direct:xslt")
            .to("xslt:articles.xsl")
            .log("Result after XSLT: ${body}")
            .to("xslt:articles2csv.xsl")
            .log("Result after CSV: ${body}");

    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void processXml(Exchange exchange) {
        log.info("CurrentTransaction Gtrid: " + String.valueOf(TransactionManagerServices.getTransactionManager().getCurrentTransaction().getGtrid()));
        ArticlesEntity articlesEntity = (ArticlesEntity) exchange.getIn().getBody();
        Map<String, Object> headers = exchange.getIn().getHeaders();
        String headersString = headers.keySet().stream()
                .map(key -> key + ":" + String.valueOf(headers.get(key)))
                .collect(Collectors.joining(";", "", ""));
        articlesEntity.setHeaders(headersString);
        log.debug("Persisting entity >>> " + articlesEntity.toString());
        articlesRepository.save(articlesEntity);

        log.info("Connecting to database via JDBC");
        try (Statement stmt = DriverManager.getConnection(dbUrl, dbUsername, dbPassword).createStatement()) {
            String tmpsql = "SELECT id, headers FROM articles";
            ResultSet rs1 = stmt.executeQuery(tmpsql);
            String res = "";
            while (rs1.next()) {
                Long resId = rs1.getLong("id");
                String resHeaders = rs1.getString("headers");
                res = res.concat("{" + resId + ", " + resHeaders + "}");
            }
            log.info("Current state of articles: " + res);
        } catch (SQLException e) {
            log.error("Error while inserting headers", e);
        }

        log.info("CurrentTransaction Gtrid: " + String.valueOf(TransactionManagerServices.getTransactionManager().getCurrentTransaction().getGtrid()));
        log.debug("FINISH OF ROUTE");
    }

}

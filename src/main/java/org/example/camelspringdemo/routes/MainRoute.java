package org.example.camelspringdemo.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.example.camelspringdemo.config.ActiveMQConfig;
import org.example.camelspringdemo.model.CatalogEntity;
import org.example.camelspringdemo.repository.CatalogRepository;
import org.example.camelspringdemo.util.MessageCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MainRoute extends RouteBuilder {

    private String srcDirPath;
    private CatalogRepository catalogRepository;
    private String mailHost;
    private String mailPort;
    private String mailLogin;
    private String mailPassword;
    private String onHundredMailTo;
    private Integer msgThreshold;
    private MessageCounter messageCounter;

    @Autowired
    public MainRoute(@Value("${application.srcDirPath:srcDirPath}") String srcDirPath,
                     MessageCounter messageCounter,
                     CatalogRepository catalogRepository,
                     @Value("${application.mail.host}") String mailHost,
                     @Value("${application.mail.port}") String mailPort,
                     @Value("${application.mail.login}") String mailLogin,
                     @Value("${application.mail.password}") String mailPassword,
                     @Value("${application.mail.onHundredMailTo}") String onHundredMailTo,
                     @Value("${application.msgThreshold:100}") Integer msgThreshold) {
        this.srcDirPath = srcDirPath;
        this.messageCounter = messageCounter;
        this.catalogRepository = catalogRepository;
        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.mailLogin = mailLogin;
        this.mailPassword = mailPassword;
        this.onHundredMailTo = onHundredMailTo;
        this.msgThreshold = msgThreshold;
    }

    @Override
    public void configure() {
        CamelContext context = new DefaultCamelContext();
        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("vm://localhost?broker.persistent=false"));

        from("file://" + srcDirPath + "?autoCreate=true&move=.done&readLock=markerFile")
            .setHeader("FileExtension").simple("${file:name.ext.single}")
            .choice()
                // if txt file detected
                .when(header("FileExtension").isEqualTo("txt"))
                    .bean(messageCounter, "incTxtCounter()")
                    .to("activemq:queue:" + ActiveMQConfig.TXT_QUEUE)
                // if xml file detected
                .when(header("FileExtension").isEqualTo("xml"))
                    .bean(messageCounter, "incXmlCounter()")
                    .to("activemq:queue:" + ActiveMQConfig.XML_QUEUE)
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
            .unmarshal().jacksonxml(CatalogEntity.class)
            .log(LoggingLevel.DEBUG, ActiveMQConfig.XML_QUEUE + " >>> ${body}")
            .process((Exchange exchange) -> {
                CatalogEntity catalogEntity = (CatalogEntity) exchange.getIn().getBody();
                log.debug("Persisting entity >>> " + catalogEntity.toString());
                catalogRepository.save(catalogEntity);
            })
            ;

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

    }

}

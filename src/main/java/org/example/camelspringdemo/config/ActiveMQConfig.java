package org.example.camelspringdemo.config;

import bitronix.tm.resource.jms.PoolingConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Properties;

@Configuration
public class ActiveMQConfig {

    public static final String XML_QUEUE = "XML_QUEUE";
    public static final String TXT_QUEUE = "TXT_QUEUE";
    public static final String ERROR_QUEUE = "ERROR_QUEUE";

//    @Bean
//    public ActiveMQConnectionFactory activeMQConnectionFactory() {
//        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
//    }

//    @Bean
//    public ActiveMQXAConnectionFactory jmsConnectionFactory() {
//        return new ActiveMQXAConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false");
//    }
//
//    @Bean
//    public PooledConnectionFactory poolConnectionFactory(ActiveMQXAConnectionFactory jmsConnectionFactory) {
//        PooledConnectionFactory retVal = new PooledConnectionFactory();
//        retVal.setMaxConnections(8);
//        retVal.setConnectionFactory(jmsConnectionFactory);
//        return retVal;
//    }
//
//    @Bean
//    public JmsTransactionManager jmsTransactionManager(PooledConnectionFactory poolConnectionFactory) {
//        return new JmsTransactionManager(poolConnectionFactory);
//    }

    @Bean
    public ActiveMQComponent activemq(PoolingConnectionFactory connectionFactory, JtaTransactionManager transactionManager) {
        ActiveMQComponent retVal = ActiveMQComponent.activeMQComponent();
        retVal.setConnectionFactory(connectionFactory);
        retVal.setTransactionManager(transactionManager);
        retVal.setTransacted(true);
        return retVal;
    }

    @Bean
    public PoolingConnectionFactory poolConnectionFactory() {
        PoolingConnectionFactory retVal = new PoolingConnectionFactory();
        retVal.setUniqueName("activemqXAcf");
        retVal.setClassName("org.apache.activemq.ActiveMQXAConnectionFactory");
        retVal.setMaxPoolSize(5);
        retVal.setTwoPcOrderingPosition(1);
        Properties dp = new Properties();
        dp.setProperty("brokerURL", "vm://localhost?broker.persistent=false&broker.useJmx=false");
        retVal.setDriverProperties(dp);
        return retVal;
    }

}

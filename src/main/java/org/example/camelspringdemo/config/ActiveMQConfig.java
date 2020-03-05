package org.example.camelspringdemo.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQConfig {

    public static final String XML_QUEUE = "XML_QUEUE";
    public static final String TXT_QUEUE = "TXT_QUEUE";
    public static final String ERROR_QUEUE = "ERROR_QUEUE";

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

}

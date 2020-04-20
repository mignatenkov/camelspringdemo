package org.example.camelspringdemo.config;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class BitronixTMConfig {

//    @Bean
//    public bitronix.tm.Configuration btmConfig() {
//        bitronix.tm.Configuration btmConfig = TransactionManagerServices.getConfiguration();
//        btmConfig.setServerId("btx-server-0");
//        btmConfig.setLogPart1Filename("transaction-logs/part1.btm");
//        btmConfig.setLogPart2Filename("transaction-logs/part2.btm");
//        return btmConfig;
//    }

    @Bean
    public BitronixTransactionManager bitronixTransactionManager() {
        return TransactionManagerServices.getTransactionManager();
    }

    @Bean
    public JtaTransactionManager transactionManager(BitronixTransactionManager bitronixTransactionManager) {
        return new JtaTransactionManager(bitronixTransactionManager, bitronixTransactionManager);
    }

    @Bean(name = "MANDATORY")
    public SpringTransactionPolicy MANDATORY(JtaTransactionManager transactionManager) {
        SpringTransactionPolicy retVal = new SpringTransactionPolicy(new TransactionTemplate(transactionManager));
        retVal.setPropagationBehaviorName("MANDATORY");
        return retVal;
    }

    @Bean(name = "REQUIRED")
    public SpringTransactionPolicy REQUIRED(JtaTransactionManager transactionManager) {
        SpringTransactionPolicy retVal = new SpringTransactionPolicy(new TransactionTemplate(transactionManager));
        retVal.setPropagationBehaviorName("REQUIRED");
        return retVal;
    }

}

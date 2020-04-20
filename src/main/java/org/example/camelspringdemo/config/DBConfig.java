package org.example.camelspringdemo.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DBConfig {

/*
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
*/

    @Bean
    public PoolingDataSource dataSource() {
        PoolingDataSource ds = new PoolingDataSource();
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setUniqueName("h2ds");
        ds.setAllowLocalTransactions(true);
        ds.setMaxPoolSize(5);
        ds.setTwoPcOrderingPosition(0);
        ds.setIsolationLevel("READ_UNCOMMITTED");
        Properties dp = new Properties();
        dp.setProperty("user", "root");
        dp.setProperty("password", "root");
        dp.setProperty("url", "jdbc:h2:file:./db/camelspringdemodb");
        ds.setDriverProperties(dp);
        return ds;
    }

}

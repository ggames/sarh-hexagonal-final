package com.fich.sarh.auth.Infrastructure.adapter.configuration.datasource;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class DataSourceConfig {
    // =============== AUTH ==================================

    @Bean
    @ConfigurationProperties("spring.datasource.auth")
    public DataSourceProperties authDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "authDataSource")
    public DataSource authDataSource(
        @Qualifier("authDataSourceProperties") DataSourceProperties properties){
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

   // ================== PROD =================================
    @Bean
    @ConfigurationProperties("spring.datasource.prod")
    public DataSourceProperties prodDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "prodDataSource")
    public DataSource prodDataSource(@Qualifier("prodDataSourceProperties")
                                     DataSourceProperties properties){
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // ================= TEST ===================================
    @Bean
    @ConfigurationProperties("spring.datasource.test")
    public DataSourceProperties testDataSourceProperties(){
        return new DataSourceProperties();
    }



    @Bean(name = "testDataSource")
    public DataSource testDataSource(@Qualifier("testDataSourceProperties")
                                      DataSourceProperties properties){
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("authDataSource") DataSource authDataSource,
                                 @Qualifier("prodDataSource") DataSource prodDataSource,
                                 @Qualifier("testDataSource")DataSource testDataSource){

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> targets = new HashMap<>();
        targets.put(DatabaseType.AUTH, authDataSource);
        targets.put(DatabaseType.PROD, prodDataSource);
        targets.put(DatabaseType.TEST, testDataSource);

       // RoleBasedRoutingDataSource routing = new RoleBasedRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(authDataSource);
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }


}

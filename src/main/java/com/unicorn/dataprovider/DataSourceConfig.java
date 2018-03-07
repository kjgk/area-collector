package com.unicorn.dataprovider;


import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.primary")
    public DataSource primaryDataSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.secondary")
    public DataSource secondaryDataSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondaryJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate() {

        return new JdbcTemplate(secondaryDataSource());
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {

        return new JdbcTemplate(primaryDataSource());
    }
}

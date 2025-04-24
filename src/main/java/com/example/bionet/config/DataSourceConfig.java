package com.example.bionet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=bionet;encrypt=true;trustServerCertificate=true;integratedSecurity=true");
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return ds;
    }
}

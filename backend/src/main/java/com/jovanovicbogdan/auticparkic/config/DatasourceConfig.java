package com.jovanovicbogdan.auticparkic.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatasourceConfig {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public HikariDataSource hikariDataSource() {
    return DataSourceBuilder
        .create()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  public JdbcTemplate jdbcTemplate(final HikariDataSource hikariDataSource) {
    return new JdbcTemplate(hikariDataSource);
  }

//  @Bean
//  public DefaultLockRepository defaultLockRepository(final HikariDataSource dataSource) {
//    return new DefaultLockRepository(dataSource);
//  }
//
//  @Bean
//  public JdbcLockRegistry jdbcLockRegistry(final LockRepository lockRepository) {
//    return new JdbcLockRegistry(lockRepository);
//  }

}

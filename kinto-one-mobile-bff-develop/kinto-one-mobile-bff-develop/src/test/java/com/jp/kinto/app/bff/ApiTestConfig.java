package com.jp.kinto.app.bff;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

@Configuration
public class ApiTestConfig {
  @Bean
  @Primary
  public R2dbcTransactionManager r2dbcTransactionManager() {
    return new R2dbcTransactionManager(
        new H2ConnectionFactory(
            H2ConnectionConfiguration.builder()
                .inMemory("...")
                .option("DB_CLOSE_DELAY=-1")
                .build()));
  }
}

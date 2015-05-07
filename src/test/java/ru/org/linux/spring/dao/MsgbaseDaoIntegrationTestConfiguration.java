package ru.org.linux.spring.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 *
 */
@Configuration
@ImportResource("classpath:database.xml")
public class MsgbaseDaoIntegrationTestConfiguration {
  @Bean
  public MsgbaseDao msgbaseDao() {
    return new MsgbaseDao();
  }
}

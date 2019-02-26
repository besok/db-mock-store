package ru.besok.db.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"ru.besok.db.mock.data.common"})
@EntityScan("ru.besok.db.mock.data.common")
@Configuration
@SpringBootApplication
public class SpringBootConfig {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootConfig.class);
  }

}

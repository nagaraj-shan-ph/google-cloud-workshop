package org.imaginea.workshop.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.imaginea.workshop.database")
public class JpaConfig {

}

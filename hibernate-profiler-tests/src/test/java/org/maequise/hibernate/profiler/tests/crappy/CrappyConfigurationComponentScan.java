package org.maequise.hibernate.profiler.tests.crappy;

import org.maequise.hibernate.profiler.tests.entities.TestEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"org.maequise.hibernate.profiler.tests.crappy"})
@EnableJpaRepositories("org.maequise.hibernate.profiler.tests.crappy.repositories")
public class CrappyConfigurationComponentScan {
}

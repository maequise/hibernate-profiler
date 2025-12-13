package org.maequise.hibernate.profiler.tests;

import org.maequise.hibernate.profiler.configuration.ProfilerConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@TestConfiguration
@Import(ProfilerConfiguration.class)
@EntityScan("org.maequise.hibernate.profiler.tests.entities")
public class DatabaseConfiguration {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    public JpaProperties jpaProperties() {
        var p = new  JpaProperties();

        p.setShowSql(true);
        p.setGenerateDdl(true);

        return p;
    }

    @Bean
    public HibernateProperties hibernateProperties() {
        var p =  new HibernateProperties();

        p.setDdlAuto("create");

        return p;
    }

}

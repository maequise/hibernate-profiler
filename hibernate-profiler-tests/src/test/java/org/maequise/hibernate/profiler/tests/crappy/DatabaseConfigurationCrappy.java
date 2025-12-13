package org.maequise.hibernate.profiler.tests.crappy;

import jakarta.annotation.PostConstruct;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.maequise.hibernate.profiler.configuration.ProfilerConfiguration;
import org.maequise.hibernate.profiler.tests.crappy.entities.SubEntity;
import org.maequise.hibernate.profiler.tests.crappy.entities.TestEntity;
import org.maequise.hibernate.profiler.tests.crappy.repositories.SubEntityRepository;
import org.maequise.hibernate.profiler.tests.crappy.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Stream;

@TestConfiguration
@Import(ProfilerConfiguration.class)
@EntityScan("org.maequise.hibernate.profiler.tests.crappy.entities")
public class DatabaseConfigurationCrappy {
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

    @Component
    public static class Populator {
        @Autowired
        private TestRepository testRepository;

        @Autowired
        private SubEntityRepository subEntityRepository;

        @PostConstruct
        @Transactional
        public void init() {
            EasyRandomParameters parameters = new EasyRandomParameters()
                    .scanClasspathForConcreteTypes(true)
                    .excludeField(FieldPredicates.named("id"))
                    .excludeField(FieldPredicates.named("testEntity"))
                    .objectPoolSize(10);

            EasyRandom randomizer = new EasyRandom(parameters);

            List<SubEntity> subEntities = Stream.generate(() -> randomizer.nextObject(SubEntity.class)).limit(20).toList();

            //subEntities = subEntityRepository.saveAll(subEntities);

            List<TestEntity> entities = Stream.generate(() -> randomizer.nextObject(TestEntity.class))
                    .limit(20)
                    .toList();

            //List<SubEntity> finalSubEntities = subEntities;
            entities.forEach(e -> e.setSubEntities(subEntities));

            testRepository.saveAll(entities);
            /*var random = new Easy
            var testEntities = new ArrayList<>();

            */
        }
    }

}

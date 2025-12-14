package org.maequise.hibernate.profiler.tests.crappy.execution;

import jakarta.annotation.PostConstruct;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maequise.hibernate.profiler.core.annotations.ExpectedDeleteQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedInsertQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedSelectQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedUpdateQuery;
import org.maequise.hibernate.profiler.core.extension.HibernateProfilerExtension;
import org.maequise.hibernate.profiler.tests.crappy.CrappyConfigurationComponentScan;
import org.maequise.hibernate.profiler.tests.crappy.DatabaseConfigurationCrappy;
import org.maequise.hibernate.profiler.tests.crappy.repositories.SubEntityRepository;
import org.maequise.hibernate.profiler.tests.crappy.repositories.TestRepository;
import org.maequise.hibernate.profiler.tests.crappy.entities.SubEntity;
import org.maequise.hibernate.profiler.tests.crappy.entities.TestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(classes = {DatabaseConfigurationCrappy.class, CrappyConfigurationComponentScan.class})
@ExtendWith(HibernateProfilerExtension.class)
class TestRepositoryCrappy {
    @Autowired
    private TestRepository testRepository;

    @Test
    @DisplayName("Test fetch by id")
    @ExpectedSelectQuery(1)
    void test_select_by_id_should_be_one() {
        testRepository.findById(1L);
    }

    @Test
    @DisplayName("Test insert")
    @ExpectedSelectQuery
    @ExpectedInsertQuery(2)
    void test_insert_queries() {
        var subEntity = new SubEntity();

        subEntity.setName("sub test");
        subEntity.setLastname("set lastname");

        var entity = new TestEntity();
        entity.setTest("test entity");
        entity.getSubEntities().add(subEntity);

        testRepository.save(entity);
    }

    @Test
    @DisplayName("Test delete by id")
    @ExpectedDeleteQuery(1)
    @ExpectedSelectQuery(0)
    @ExpectedUpdateQuery(0)
    void test_delete_by_id() {
        testRepository.deleteById(1L);
    }
}

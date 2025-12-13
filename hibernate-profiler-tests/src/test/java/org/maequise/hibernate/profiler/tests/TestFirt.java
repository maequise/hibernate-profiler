package org.maequise.hibernate.profiler.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maequise.hibernate.profiler.core.annotations.ExpectedSelectQuery;
import org.maequise.hibernate.profiler.core.extension.HibernateProfilerExtension;
import org.maequise.hibernate.profiler.tests.entities.TestEntity;
import org.maequise.hibernate.profiler.tests.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = DatabaseConfiguration.class)
@ExtendWith(HibernateProfilerExtension.class)
class TestFirt {
    @Autowired
    private TestRepository testRepository;

    @Test
    @ExpectedSelectQuery(10)
    void test() {
        var t = new TestEntity();

        t.setTest("test");

        testRepository.save(t);

        System.out.println("test");
    }
}

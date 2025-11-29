package org.maequise.hibernate.profiler.tests.repositories;

import org.maequise.hibernate.profiler.tests.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
}

package org.maequise.hibernate.profiler.tests.crappy.repositories;

import org.maequise.hibernate.profiler.tests.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}

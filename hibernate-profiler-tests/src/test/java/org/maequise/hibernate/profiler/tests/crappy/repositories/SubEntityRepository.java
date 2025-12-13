package org.maequise.hibernate.profiler.tests.crappy.repositories;

import org.maequise.hibernate.profiler.tests.crappy.entities.SubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubEntityRepository extends JpaRepository<SubEntity, Long> {
}

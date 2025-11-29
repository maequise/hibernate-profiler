package org.maequise.hibernate.profiler.tests.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Relation {
    @Id
    private Long id;
}

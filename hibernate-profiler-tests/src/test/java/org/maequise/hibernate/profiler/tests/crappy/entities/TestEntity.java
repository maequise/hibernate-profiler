package org.maequise.hibernate.profiler.tests.crappy.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "test")
    private String test;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testEntity", fetch = FetchType.EAGER)
    private List<SubEntity> subEntities = new ArrayList<>();


    public void addSubEntity(SubEntity subEntity) {
        subEntity.setTestEntity(this);
        this.subEntities.add(subEntity);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public List<SubEntity> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(List<SubEntity> subEntities) {
        this.subEntities = subEntities;
    }
}

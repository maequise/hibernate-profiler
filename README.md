<div align="center">
    <div>
        <blockquote>Hibernate Profiler is a testing library for Spring Boot application to quickly evaluate and improve queries performed by Hibernate</blockquote>
    </div>
</div>

# Hibernate Profiler

This opensource project is inspired by the [QuickPerf project](https://github.com/quick-perf/quickperf) initially create to profile and test the behavior of [the Hibernate ORM](https://github.com/hibernate/hibernate-orm).

The project seems not maintained anymore, and that's a shame as the project was great!

It's based on the awesome library *datasource-proxy* of [Tadaya Tsuyukubo](https://github.com/jdbc-observations/datasource-proxy). A great thanks to him and all contributors for the awesome work done!

# Philosophy of the project

This project wants to be the easiest tool for integration testing and issue killer on JPA queries performed by Hibernate.

It's delivered with a direct configuration to integrate with SpringBoot tests. 

Sample of usage:

```java
@SpringBootTest(classes = {ProfilerConfiguration.class})
class RepoTest {
    @Autowired
    private Repo repository;
    
    @Test
    @SelectQuery(totalExpected = 1)
    void test_selectQuery() {
        repository.findAll();
    }
}
```

### ***For the moment only the Spring integration is provided***

# Requirements

- JDK 17+
- Spring (Spring framework 6+, Spring boot 3.x+)

# Maven 

TODO

# 
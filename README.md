[![][coverage img]][sonarqube url]
[![][quality gate img]][sonarqube url]
[![][security img]][sonarqube url]
[![][javadoc img]][javadoc url]

>Hibernate Profiler is a testing library for Spring Boot application to quickly evaluate and improve queries performed by Hibernate

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

It's possible that you hit the following error: 

```
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'jakarta.persistence.EntityManagerFactory' available
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveNamedBean(DefaultListableBeanFactory.java:1558)
	at org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor.findDefaultEntityManagerFactory(PersistenceAnnotationBeanPostProcessor.java:584)
	at org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor.findEntityManagerFactory(PersistenceAnnotationBeanPostProcessor.java:548)
	at org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor$PersistenceElement.resolveEntityManager(PersistenceAnnotationBeanPostProcessor.java:713)
	at org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor$PersistenceElement.getResourceToInject(PersistenceAnnotationBeanPostProcessor.java:686)
	at org.springframework.beans.factory.annotation.InjectionMetadata$InjectedElement.inject(InjectionMetadata.java:270)
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:146)
	at org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor.postProcessProperties(PersistenceAnnotationBeanPostProcessor.java:379)

```

Maybe somewhere in your codebase you have something like this: 

```java
@Repository
public class MySuperDAO {
    @PersistenceContext
    private EntityManager entityManager;
    
    /// boilerplate code of the persistence layer
}

```

It means that you need to declare an `EntityManagerFactory` in your configuration class.

### ***For the moment only the Spring integration is provided***

# Requirements

- JDK 21+
- Spring (Spring framework 6+, Spring boot 3.x+)

# Maven 

```xml
<dependency>
    <groupId>io.github.maequise</groupId>
    <artifactId>hibernate-profiler-spring</artifactId>
    <version>0.2.0</version>
</dependency>

```
#
[sonarqube url]:https://sonarcloud.io/summary/new_code?id=maequise_hibernate-profiler
[quality gate img]:https://sonarcloud.io/api/project_badges/measure?project=maequise_hibernate-profiler&metric=alert_status
[coverage img]:https://sonarcloud.io/api/project_badges/measure?project=maequise_hibernate-profiler&metric=coverage
[security img]:https://sonarcloud.io/api/project_badges/measure?project=maequise_hibernate-profiler&metric=security_rating
[javadoc img]:https://javadoc.io/badge2/io.github.maequise/hibernate-profiler-core/javadoc.svg
[javadoc url]:https://javadoc.io/doc/io.github.maequise/hibernate-profiler-core
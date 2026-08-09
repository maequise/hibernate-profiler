package org.maequise.hibernate.profiler.configuration;

import net.ttddyy.dsproxy.support.ProxyDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import javax.sql.DataSource;

class ProfilerAutoConfigurationTest {
    ApplicationContextRunner contextRunner;

    @BeforeEach
    void setup() {
        contextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ProfilerAutoConfiguration.class));
    }

    @Test
    @DisplayName("Test auto configuration with no provided datasource provided")
    void test_auto_configuration_no_datasource_provided() {
        this.contextRunner
                .run(context -> {
            Assertions.assertThat(context).doesNotHaveBean(DataSource.class);
        });
    }

    @Test
    @DisplayName("")
    void test_auto_configuration_datasource_provided() {
        this.contextRunner
                .withUserConfiguration(MockDataSourceConfiguration.class)
                .run(context -> {
                    Assertions.assertThat(context).hasBean("dataSource");
                    Assertions.assertThat(context).hasBean("dataSourceProxied");
                });
    }

    @Test
    @DisplayName("")
    void test_auto_configuration_datasource_property() {
        this.contextRunner
                .withUserConfiguration(MockDataSourceConfiguration.class)
                .withPropertyValues("datasource.name=test")
                .run(context -> {
                    Assertions.assertThat(context).hasBean("test");
                    Assertions.assertThat(context).hasBean("proxiedtest");
                    Assertions.assertThat(context).doesNotHaveBean("testBis");
                });
    }

    @Test
    @DisplayName("")
    void test_auto_configuration_datasource_property_ds_bis() {
        this.contextRunner
                .withUserConfiguration(MockDataSourceConfiguration.class)
                .withPropertyValues("datasource.name=testBis")
                .run(context -> {
                    Assertions.assertThat(context).hasBean("test");
                    Assertions.assertThat(context).hasBean("testBis");
                    Assertions.assertThat(context).hasBean("proxiedtestBis");
                });
    }

    @TestConfiguration
    static class MockDataSourceConfiguration {
        @Bean
        @Conditional(DataSourceCondition.class)
        DataSource dataSource() {
            return new ProxyDataSource();
        }

        @Bean
        @ConditionalOnProperty("datasource.name")
        DataSource test() {
            return new ProxyDataSource();
        }

        @Bean
        @ConditionalOnProperty(value = "datasource.name", havingValue = "testBis")
        DataSource testBis() {
            return new ProxyDataSource();
        }
    }

}

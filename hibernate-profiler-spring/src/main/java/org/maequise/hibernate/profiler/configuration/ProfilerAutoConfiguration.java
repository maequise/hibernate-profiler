package org.maequise.hibernate.profiler.configuration;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.maequise.hibernate.profiler.listeners.Listener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

@AutoConfiguration
@Order
@ConditionalOnBean(DataSource.class)
public class ProfilerAutoConfiguration {
    @Bean
    @Primary
    @Conditional(DataSourceCondition.class)
    public DataSource dataSourceProxied(DataSource dataSource) {
        return createProxiedDataSource(dataSource);
    }

    @Bean
    @Primary
    @ConditionalOnProperty("datasource.name")
    public DataSource dataSourceNamed(AnnotationConfigApplicationContext context,
                                      @Value("${datasource.name}") String name) {
        DataSource namedBean = context.getBean(name, DataSource.class);

        var p = createProxiedDataSource(namedBean);

        context.registerBean("proxied".concat(name),
                DataSource.class, () -> p);
        return p;
    }

    private DataSource createProxiedDataSource(DataSource ds) {
        var chainListeners = new ChainListener();

        PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
        creator.setMultiline(true);

        SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
        listener.setQueryLogEntryCreator(creator);

        chainListeners.addListener(listener);
        chainListeners.addListener(new Listener());

        return ProxyDataSourceBuilder
                .create(ds)
                .name("dataSource")
                .listener(chainListeners)
                .build();
    }

    // use hibernate to format queries
    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        private static final Formatter FORMATTER = FormatStyle.BASIC.getFormatter();

        @Override
        protected String formatQuery(String query) {
            return FORMATTER.format(query);
        }
    }
}

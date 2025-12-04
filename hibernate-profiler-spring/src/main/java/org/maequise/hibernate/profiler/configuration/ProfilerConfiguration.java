package org.maequise.hibernate.profiler.configuration;

import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.maequise.hibernate.profiler.listeners.Listener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

@Configuration
@Order
public class ProfilerConfiguration {
    @Bean
    @Primary
    public DataSource dataSourceProxied(DataSource dataSource) {
        var chainListeners = new ChainListener();

        PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
        creator.setMultiline(true);

        SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
        listener.setQueryLogEntryCreator(creator);

        chainListeners.addListener(listener);
        chainListeners.addListener(new Listener());

        return ProxyDataSourceBuilder
                .create(dataSource)
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

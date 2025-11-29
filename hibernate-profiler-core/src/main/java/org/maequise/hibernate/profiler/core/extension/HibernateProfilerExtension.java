package org.maequise.hibernate.profiler.core.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.maequise.hibernate.profiler.core.DataSourceHolder;
import org.maequise.hibernate.profiler.core.ProcessorsConfiguration;
import org.maequise.hibernate.profiler.core.annotations.SelectQuery;
import org.maequise.hibernate.profiler.core.processors.Processor;

import java.lang.annotation.Annotation;
import java.util.Map;

public class HibernateProfilerExtension implements BeforeEachCallback, AfterEachCallback {
    private static final Map<String, Processor> PROCESSORS = ProcessorsConfiguration.getProcessors();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Thread.currentThread().setName(context.getTestMethod().orElseThrow().getName());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var testNameMethod = context.getTestMethod().orElseThrow().getName();

        var connectionsNamed = DataSourceHolder.getConnectionsNamed();

        context.getTestMethod().ifPresent(m -> {
            var annots = m.getAnnotations();

            for (Annotation annotation : annots) {
                if (annotation instanceof SelectQuery sq) {
                    var queryInfoList = connectionsNamed.get(testNameMethod);
                    PROCESSORS.get("select").process(queryInfoList, sq);
                }
            }
        });
    }
}

package org.maequise.hibernate.profiler.core.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.maequise.hibernate.profiler.core.DataSourceHolder;
import org.maequise.hibernate.profiler.core.ProcessorsConfiguration;
import org.maequise.hibernate.profiler.core.annotations.InsertQuery;
import org.maequise.hibernate.profiler.core.annotations.SelectQuery;
import org.maequise.hibernate.profiler.core.annotations.UpdateQuery;
import org.maequise.hibernate.profiler.core.processors.Processor;

import java.lang.annotation.Annotation;
import java.util.Map;

/// Junit extension to use in the test classes to apply the different controls to perform during the test methods
///
/// @author maequise
/// @since 0.1.0
public class HibernateProfilerExtension implements BeforeEachCallback, AfterEachCallback {
    private static final Map<String, Processor> PROCESSORS = ProcessorsConfiguration.getProcessors();

    /// Apply a callback before each test execution to identify the test method and set the [Thread] name to this test method
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Thread.currentThread().setName(context.getTestMethod().orElseThrow().getName());
    }

    /// After the execution of the test, perform a control on the possible annotation and call the appropriate [Processor]
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var testNameMethod = context.getTestMethod().orElseThrow().getName();

        var connectionsNamed = DataSourceHolder.getConnectionsNamed();

        context.getTestMethod().ifPresent(m -> {
            var annots = m.getAnnotations();
            var queryInfoList = connectionsNamed.get(testNameMethod);

            for (Annotation annotation : annots) {
                if (annotation instanceof SelectQuery sq) {
                    PROCESSORS.get("select").process(queryInfoList, sq);
                } else if(annotation instanceof InsertQuery iq) {
                    PROCESSORS.get("insert").process(queryInfoList, iq);
                } else if(annotation instanceof UpdateQuery uq){
                    PROCESSORS.get("update").process(queryInfoList, uq);
                }
            }
        });
    }
}

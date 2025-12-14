package org.maequise.hibernate.profiler.core.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.maequise.hibernate.profiler.core.DataSourceHolder;
import org.maequise.hibernate.profiler.core.ProcessorsConfiguration;
import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.ExpectedDeleteQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedInsertQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedSelectQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedUpdateQuery;
import org.maequise.hibernate.profiler.core.processors.Processor;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
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
        String testNameMethod = context.getTestMethod().orElseThrow().getName();

        Map<String, List<QueryInformation>> connectionsNamed = DataSourceHolder.getConnectionsNamed();

        context.getTestMethod().ifPresent(m -> {
            var annots = m.getAnnotations();
            List<AssertionFailedError> errors = new ArrayList<>(4);

            List<QueryInformation> queryInfoList = connectionsNamed.get(testNameMethod);

            for (Annotation annotation : annots) {
                switch (annotation) {
                    case ExpectedSelectQuery sq -> {
                        try {
                            PROCESSORS.get("select").process(queryInfoList, sq);
                        } catch (AssertionFailedError e) {
                            errors.add(e);
                        }
                    }
                    case ExpectedInsertQuery sq -> {
                        try {
                            PROCESSORS.get("insert").process(queryInfoList, sq);
                        } catch (AssertionFailedError e) {
                            errors.add(e);
                        }
                    }
                    case ExpectedUpdateQuery sq -> {
                        try {
                            PROCESSORS.get("update").process(queryInfoList, sq);
                        } catch (AssertionFailedError e) {
                            errors.add(e);
                        }
                    }
                    case ExpectedDeleteQuery sq -> {
                        try {
                            PROCESSORS.get("delete").process(queryInfoList, sq);
                        } catch (AssertionFailedError e) {
                            errors.add(e);
                        }
                    }
                    default -> {
                    }
                }
            }

            if (!errors.isEmpty()) {
                String errorMessage = String.format("Assertion error, expected: %s but got: %s", errors,errors);
                throw new AssertionFailedError(errorMessage, List.of(), errors);
            }
        });
    }
}

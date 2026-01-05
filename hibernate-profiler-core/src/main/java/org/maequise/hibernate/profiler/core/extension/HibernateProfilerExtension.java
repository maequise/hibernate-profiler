package org.maequise.hibernate.profiler.core.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.maequise.hibernate.profiler.core.DataSourceHolder;
import org.maequise.hibernate.profiler.core.ProcessorsConfiguration;
import org.maequise.hibernate.profiler.core.QueryDataHolder;
import org.maequise.hibernate.profiler.core.annotations.*;
import org.maequise.hibernate.profiler.core.processors.Processor;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

        context.getTestMethod().ifPresent(m -> processAnnotationsOfTestMethod(testNameMethod, m));
    }

    private void processAnnotationsOfTestMethod(String testNameMethod, Method method) {
        boolean isExperimentalActivated = isExperimentalActivated(method);

        Map<String, List<QueryDataHolder>> connectionsNamed = DataSourceHolder.getConnectionsNamed();
        Annotation[] annots = method.getAnnotations();
        List<AssertionFailedError> errors = new ArrayList<>(4);

        List<QueryDataHolder> queryInfoList = connectionsNamed.get(testNameMethod);

        for (Annotation annotation : annots) {
            switch (annotation) {
                case ExpectedSelectQuery sq ->
                    processAnnotation("select", queryInfoList, sq, errors, isExperimentalActivated);
                case ExpectedInsertQuery sq ->
                        processAnnotation("insert", queryInfoList, sq, errors, isExperimentalActivated);
                case ExpectedUpdateQuery sq ->
                        processAnnotation("update", queryInfoList, sq, errors, isExperimentalActivated);
                case ExpectedDeleteQuery sq ->
                        processAnnotation("delete", queryInfoList, sq, errors, isExperimentalActivated);
                default -> {
                    //continue nothing to do
                }
            }
        }

        //experimental feature

        if (!errors.isEmpty()) {
            String errorMessage = String.format("Assertion error, expected: %s but got: %s", errors,errors);
            throw new AssertionFailedError(errorMessage, List.of(), errors);
        }
    }

    private boolean isExperimentalActivated(Method method) {
        HibernateProfilerExperimental annotation = (HibernateProfilerExperimental) Stream.of(method.getAnnotations())
                .filter(HibernateProfilerExperimental.class::isInstance)
                .findFirst().orElse(null);

        return annotation != null && annotation.value();
    }

    private void processAnnotation(String processorType, final List<QueryDataHolder> queryInfoList,
                                                Annotation annotation,
                                                List<AssertionFailedError> errors, boolean isExperimental) {
        try {
            PROCESSORS.get(processorType).process(queryInfoList, annotation);
        }catch (AssertionFailedError e){
            if(!isExperimental){
                throw e;
            }

            errors.add(e);
        }
    }
}

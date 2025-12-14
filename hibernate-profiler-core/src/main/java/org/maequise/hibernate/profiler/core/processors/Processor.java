package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/// Interface to define the contract of the processor implementation to control the behavior of the annotation used
/// Can be defined as a functional interface
///
/// @author maequise
/// @since 0.1.0
public sealed interface Processor permits SelectProcessor, InsertProcessor, UpdateProcessor, DeleteProcessor {
    /// Method to control the different queries performed with the annotation associated
    void process(List<QueryInformation> queryInformation, Annotation annotation);

    /// Default method used to share the logic of control for different annotation with the same behavior on controls
    default void controlData(int expectedTotalQuery, String queryToControl, List<String> queries) {
        controlData("", expectedTotalQuery, queryToControl, queries);
    }

    /// Default method used to share the logic of control for different annotation with the same behavior on controls
    default void controlData(String typeOfQuery, int expectedTotalQuery, String queryToControl, List<String> queries) {
        if (expectedTotalQuery != queries.size()) {
            var msg = String.format("Expected %s queries: %d but found: %d %s", typeOfQuery, expectedTotalQuery, queries.size(), Arrays.toString(queries.toArray()));
            throw new AssertionFailedError(msg);
        }

        var containsQuery = new AtomicBoolean(true);

        if (!queryToControl.isEmpty()) {
            containsQuery.set(queries.contains(queryToControl));

            if (!containsQuery.get()) {
                String errorMessage = String.format("Expected %s query: %s but not found in all queries %s", typeOfQuery, queryToControl, queries);
                throw new AssertionFailedError(errorMessage);
                //throw new AssertionFailedError("Expected query: " + queryToControl + " but not found in all queries : " + queries);
            }
        }
    }
}

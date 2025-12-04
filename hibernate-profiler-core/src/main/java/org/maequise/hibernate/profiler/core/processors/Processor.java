package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface Processor {
    void process(List<QueryInformation> queryInformation, Annotation annotation);

    default void controlData(int expectedTotalQuery, String queryToControl, List<String> queries) {
        if(expectedTotalQuery != queries.size()) {
            throw new AssertionFailedError("Expected queries: " + expectedTotalQuery + " but found: " + queries.size());
        }

        var containsQuery = new AtomicBoolean(true);

        if(!queryToControl.isEmpty()) {
            containsQuery.set(queries.contains(queryToControl));

            if(!containsQuery.get()) {
                throw new AssertionFailedError("Expected query: " + queryToControl + " but not found in all queries : " + queries);
            }
        }
    }
}

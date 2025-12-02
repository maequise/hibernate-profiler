package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.InsertQuery;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InsertProcessor implements Processor {
    @Override
    public void process(List<QueryInformation> queryInformation, Annotation annotation) {
        var annot = (InsertQuery) annotation;
        var queriesStr = new ArrayList<String>();

        var totalQueries = annot.totalExpected();
        var expectedQuery = annot.queryExpected();

        var queries = new ArrayList<>(queryInformation);

        var iterator = queries.iterator();

        while(iterator.hasNext()) {
            var query = iterator.next();

            if(query.listQueries().stream().anyMatch(s -> !s.contains("insert"))) {
                iterator.remove();
            }

            queriesStr.addAll(query.listQueries());
        }

        if(totalQueries != queries.size()) {
            throw new AssertionFailedError("Expected queries: " + totalQueries + " but found: " + queryInformation.size());
        }

        var containsQuery = new AtomicBoolean(true);

        if(!expectedQuery.isEmpty()) {
            containsQuery.set(queriesStr.contains(expectedQuery));

            if(!containsQuery.get()) {
                throw new AssertionFailedError("Expected query: " + expectedQuery + " but not found in all queries : " + queriesStr);
            }
        }
    }
}

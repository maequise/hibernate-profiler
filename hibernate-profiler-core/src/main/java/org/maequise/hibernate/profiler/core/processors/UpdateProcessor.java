package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.UpdateQuery;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateProcessor implements Processor{
    @Override
    public void process(List<QueryInformation> queryInformation, Annotation annotation) {
        var annot = (UpdateQuery) annotation;
        var totalExpected = annot.totalExpected();
        var expectedQuery = annot.queryExpected();

        var queries = new ArrayList<>(queryInformation);
        var queryStrings = new ArrayList<String>();

        var iterator = queries.iterator();

        while (iterator.hasNext()) {
            var query = iterator.next();

            if(query.listQueries().stream().anyMatch(s -> !s.contains("update"))) {
                iterator.remove();
            }

            queryStrings.addAll(query.listQueries());
        }

        if(totalExpected != queries.size()) {
            throw new AssertionFailedError("Expected queries: " + totalExpected + " but found: " + queryInformation.size());
        }

        var containsQuery = new AtomicBoolean(true);

        if(!expectedQuery.isEmpty()) {
            containsQuery.set(queryStrings.contains(expectedQuery));

            if(!containsQuery.get()) {
                throw new AssertionFailedError("Expected query: " + expectedQuery + " but not found in all queries : " + queryStrings);
            }
        }

    }
}

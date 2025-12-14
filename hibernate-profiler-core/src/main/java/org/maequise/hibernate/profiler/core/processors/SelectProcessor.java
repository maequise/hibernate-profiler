package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.ExpectedSelectQuery;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/// Implementation to control the `Select` statements
///
/// @author maequise
/// @since 0.1.0
public final class SelectProcessor implements Processor {
    @Override
    public void process(List<QueryInformation> queryInformation, Annotation anno) {
        var annot = (ExpectedSelectQuery) anno;
        var queriesStr = new ArrayList<String>();

        var totalQueries = annot.value();
        var expectedQuery = annot.queryExpected();
        var shouldExcludeSequencesQueries = annot.excludeSequenceQueries();

        var totalQueriesData = new ArrayList<>(queryInformation);

        var iterator = totalQueriesData.iterator();

        while (iterator.hasNext()) {
            var query = iterator.next();
            if(query.listQueries().stream().anyMatch(s -> (s.contains("seq") && shouldExcludeSequencesQueries)|| !s.startsWith("select"))) {
                iterator.remove();
            }else {
                queriesStr.addAll(query.listQueries());
            }
        }

        if (totalQueries != totalQueriesData.size()) {
            var msg = String.format("Expected SELECT queries: %d but found: %d %s", totalQueries, totalQueriesData.size(), Arrays.toString(queriesStr.toArray()));
            throw new AssertionFailedError(msg);
        }

        var containsQuery = new AtomicBoolean(true);

        if (expectedQuery != null && !expectedQuery.isEmpty()) {
            totalQueriesData.forEach(q -> {
                if (!q.listQueries().contains(expectedQuery)) {
                    containsQuery.set(false);
                }
            });

            if(!containsQuery.get()) {
                throw new AssertionFailedError("Expected SELECT query: " + expectedQuery +
                        " but not found in all queries : "
                        + totalQueriesData.stream().map(QueryInformation::listQueries).toList());
            }
        }
    }
}

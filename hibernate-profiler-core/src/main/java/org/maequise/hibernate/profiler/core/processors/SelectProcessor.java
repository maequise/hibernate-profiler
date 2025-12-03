package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.SelectQuery;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SelectProcessor implements Processor {
    @Override
    public void process(List<QueryInformation> queryInformation, Annotation anno) {
        var annot = (SelectQuery) anno;

        var totalQueries = annot.totalExpected();
        var expectedQuery = annot.queryExpected();
        var shouldExcludeSequencesQueries = annot.excludeSequenceQueries();

        var totalQueriesData = new ArrayList<>(queryInformation);

        var iterator = totalQueriesData.iterator();

        while (iterator.hasNext()) {
            var query = iterator.next();
            if(query.listQueries().stream().anyMatch(s -> (s.contains("seq") && shouldExcludeSequencesQueries)|| !s.startsWith("select"))) {
                iterator.remove();
            }
        }

        if (totalQueries != totalQueriesData.size()) {
            throw new AssertionFailedError("Expected queries: " + totalQueries + " but found: " + totalQueriesData.size());
        }

        var containsQuery = new AtomicBoolean(true);

        if (expectedQuery != null && !expectedQuery.isEmpty()) {
            totalQueriesData.forEach(q -> {
                if (!q.listQueries().contains(expectedQuery)) {
                    containsQuery.set(false);
                }
            });

            if(!containsQuery.get()) {
                throw new AssertionFailedError("Expected query: " + expectedQuery +
                        " but not found in all queries : "
                        + totalQueriesData.stream().map(QueryInformation::listQueries).toList());
            }
        }
    }
}

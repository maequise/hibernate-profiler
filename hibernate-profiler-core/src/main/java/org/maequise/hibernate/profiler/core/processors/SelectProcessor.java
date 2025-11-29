package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.SelectQuery;
import org.opentest4j.AssertionFailedError;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

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
            if(query.listQueries().stream().anyMatch(s -> (s.contains("seq") && shouldExcludeSequencesQueries)|| !s.contains("select"))) {
                iterator.remove();
            }
        }

        if (totalQueries != totalQueriesData.size()) {
            throw new AssertionFailedError("Expected queries: " + totalQueries + " but found: " + totalQueriesData.size());
        }

        if (expectedQuery != null && !expectedQuery.isEmpty()) {
            queryInformation.forEach(q -> {
                if (!q.listQueries().contains(expectedQuery)) {
                    throw new AssertionFailedError("Expected query: " + expectedQuery + " but found: " + q.listQueries());
                }
            });
        }
    }
}

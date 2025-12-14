package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.ExpectedDeleteQuery;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/// Implementation to control the `DELETE` statements
///
/// @author maequise
/// @since 0.2.1
public final class DeleteProcessor implements Processor {
    @Override
    public void process(List<QueryInformation> queryInformation, Annotation annotation) {
        var annot = (ExpectedDeleteQuery) annotation;
        int totalExpected = annot.value();
        String expectedQuery = annot.queryExpected();

        List<QueryInformation> queries = new ArrayList<>(queryInformation);
        List<String> queryStrings = new ArrayList<String>();

        var iterator = queries.iterator();

        while (iterator.hasNext()) {
            QueryInformation query = iterator.next();

            if(query.listQueries().stream().anyMatch(s -> !s.startsWith("delete"))) {
                iterator.remove();
            } else {
                queryStrings.addAll(query.listQueries());
            }
        }

        controlData("DELETE", totalExpected, expectedQuery, queryStrings);
    }
}

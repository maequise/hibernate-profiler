package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.ExpectedUpdateQuery;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/// Implementation to control the `Update` statements
///
/// @author maequise
/// @since 0.1.0
public final class UpdateProcessor implements Processor{
    @Override
    public void process(List<QueryInformation> queryInformation, Annotation annotation) {
        var annot = (ExpectedUpdateQuery) annotation;
        var totalExpected = annot.value();
        var expectedQuery = annot.queryExpected();

        var queries = new ArrayList<>(queryInformation);
        var queryStrings = new ArrayList<String>();

        var iterator = queries.iterator();

        while (iterator.hasNext()) {
            var query = iterator.next();

            if(query.listQueries().stream().anyMatch(s -> !s.startsWith("update"))) {
                iterator.remove();
            } else {
                queryStrings.addAll(query.listQueries());
            }
        }

        controlData("UPDATE", totalExpected, expectedQuery, queryStrings);

    }
}

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

            if(query.listQueries().stream().anyMatch(s -> !s.startsWith("insert"))) {
                iterator.remove();
            }else {
                queriesStr.addAll(query.listQueries());
            }
        }

       controlData(totalQueries, expectedQuery, queriesStr);
    }
}

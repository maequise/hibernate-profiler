package org.maequise.hibernate.profiler.core.processors;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.SelectQuery;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectProcessorTest {
    private Processor processor;

    @BeforeEach
    void setup() {
        processor = new SelectProcessor();
    }

    @Test
    @DisplayName("Test if the annotation is equals than")
    void test_annotation_total_equals_than() {
        var annotation = mock(SelectQuery.class);

        when(annotation.totalExpected()).thenReturn(2);

        assertDoesNotThrow(() -> processor.process(List.of(new QueryInformation(null,
                        null,
                        List.of(new QueryInfo("select ed from ed"),
                                new QueryInfo("select ed from ed"))),
                new QueryInformation(null,
                        null,
                        List.of(new QueryInfo("select ed from ed"),
                                new QueryInfo("select ed from ed")))), annotation));
    }

    @Test
    @DisplayName("Test if the annotation is greater than")
    void test_annotation_total_greater_than() {
        var annotation = mock(SelectQuery.class);

        when(annotation.totalExpected()).thenReturn(3);

        assertThrows(AssertionFailedError.class, () -> processor.process(List.of(new QueryInformation(null,
                null,
                List.of(new QueryInfo("select ed from ed"),
                        new QueryInfo("select sdf from ed")))), annotation));
    }

    @Test
    @DisplayName("Test expected query")
    void test_expected_query() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("select id from test_entity");

        var queryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select id from test_entity");

        assertDoesNotThrow(() -> processor.process(List.of(new QueryInformation(null,
                null,
                List.of(queryInfo))), annotation));
    }

    @Test
    @DisplayName("Test expected query in error")
    void test_expected_query_in_error() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("select id from test_entity");

        var queryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select id from test_entity where id = 1");

        assertThrows(AssertionFailedError.class, () -> processor.process(List.of(new QueryInformation(null,
                null,
                List.of(queryInfo))), annotation));
    }

    @Test
    @DisplayName("Test expected query not causing error")
    void test_expected_query_not_causing_error() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("");

        var queryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select id from test_entity where id = 1");

        assertDoesNotThrow(() -> processor.process(List.of(new QueryInformation(null,
                null,
                List.of(queryInfo))), annotation));
    }

    @Test
    @DisplayName("Exclude sequence queries")
    void test_exclude_sequence_queries() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.excludeSequenceQueries()).thenReturn(true);

        var queryInfo = mock(QueryInfo.class);
        var secondQueryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select next(id) from test_entity_seq");
        when(secondQueryInfo.getQuery()).thenReturn("select id from test_entity");

        assertDoesNotThrow(() -> processor.process(List.of(new QueryInformation(null,
                null,
                List.of(queryInfo)), createQueryInformation(null, null, secondQueryInfo)), annotation));
    }

    @Test
    @DisplayName("Exclude sequence queries")
    void test_exclude_sequence_queries_more_than_two_no_error() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(2);
        when(annotation.excludeSequenceQueries()).thenReturn(true);

        var queryInfo = mock(QueryInfo.class);
        var secondQueryInfo = mock(QueryInfo.class);
        var thirdQueryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select next(id) from test_entity_seq");
        when(secondQueryInfo.getQuery()).thenReturn("select id from test_entity");
        when(thirdQueryInfo.getQuery()).thenReturn("select id, name, lastname from test_entity");

        assertDoesNotThrow(() -> processor.process(List.of(new QueryInformation(null,
                        null,
                        List.of(queryInfo)),
                createQueryInformation(null, null, secondQueryInfo),
                createQueryInformation(null, null, thirdQueryInfo)), annotation));
    }

    @Test
    @DisplayName("Exclude sequence queries in error")
    void test_exclude_sequence_queries_more_than_two_error() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.excludeSequenceQueries()).thenReturn(true);

        var queryInfo = mock(QueryInfo.class);
        var secondQueryInfo = mock(QueryInfo.class);
        var thirdQueryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select next(id) from test_entity_seq");
        when(secondQueryInfo.getQuery()).thenReturn("select id from test_entity");
        when(thirdQueryInfo.getQuery()).thenReturn("select id, name, lastname from test_entity");

        assertThrows(AssertionFailedError.class, () -> processor.process(List.of(new QueryInformation(null,
                        null,
                        List.of(queryInfo)),
                createQueryInformation(null, null, secondQueryInfo),
                createQueryInformation(null, null, thirdQueryInfo)), annotation));
    }

    @Test
    @DisplayName("Exclude sequence queries and other queries")
    void test_exclude_sequence_queries_and_other_queries() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(2);
        when(annotation.excludeSequenceQueries()).thenReturn(true);

        var queryInfo = mock(QueryInfo.class);
        var secondQueryInfo = mock(QueryInfo.class);
        var thirdQueryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select next(id) from test_entity_seq");
        when(secondQueryInfo.getQuery()).thenReturn("select id from test_entity");
        when(thirdQueryInfo.getQuery()).thenReturn("select id, name, lastname from test_entity");

        assertDoesNotThrow(() -> processor.process(List.of(new QueryInformation(null,
                        null,
                        List.of(queryInfo)),
                createQueryInformation(null, null, secondQueryInfo),
                createQueryInformation(null, null, new QueryInfo("delete from table where id = ?1")),
                createQueryInformation(null, null, new QueryInfo("insert into table_name(id, name) values(?1,?2)")),
                createQueryInformation(null, null, new QueryInfo("insert into table_ndfge(id, name) values(?1,?2)")),
                createQueryInformation(null, null, thirdQueryInfo)), annotation));
    }

    @Test
    @DisplayName("Exclude sequence queries and other queries error")
    void test_exclude_sequence_queries_and_other_queries_error() {
        var annotation = mock(SelectQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.excludeSequenceQueries()).thenReturn(true);

        var queryInfo = mock(QueryInfo.class);
        var secondQueryInfo = mock(QueryInfo.class);
        var thirdQueryInfo = mock(QueryInfo.class);

        when(queryInfo.getQuery()).thenReturn("select next(id) from test_entity_seq");
        when(secondQueryInfo.getQuery()).thenReturn("select id from test_entity");
        when(thirdQueryInfo.getQuery()).thenReturn("select id, name, lastname from test_entity");

        assertThrows(AssertionFailedError.class, () -> processor.process(List.of(new QueryInformation(null,
                        null,
                        List.of(queryInfo)),
                createQueryInformation(null, null, secondQueryInfo),
                createQueryInformation(null, null, new QueryInfo("delete from table where id = ?1")),
                createQueryInformation(null, null, new QueryInfo("insert into table_name(id, name) values(?1,?2)")),
                createQueryInformation(null, null, new QueryInfo("insert into table_ndfge(id, name) values(?1,?2)")),
                createQueryInformation(null, null, thirdQueryInfo)), annotation));
    }

    private QueryInformation createQueryInformation(String methodName, ExecutionInfo executionInfo, QueryInfo queryInfo) {
        return new QueryInformation(methodName, executionInfo, List.of(queryInfo));
    }

}

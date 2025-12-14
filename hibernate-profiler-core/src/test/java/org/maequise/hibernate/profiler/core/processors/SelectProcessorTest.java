package org.maequise.hibernate.profiler.core.processors;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.TestUtils;
import org.maequise.hibernate.profiler.core.annotations.ExpectedSelectQuery;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelectProcessorTest {
    private Processor processor;
    private ExpectedSelectQuery annotation;

    @BeforeEach
    void setup() {
        annotation = mock(ExpectedSelectQuery.class);
        when(annotation.queryExpected()).thenReturn("");
        processor = new SelectProcessor();
    }

    @Test
    @DisplayName("Test if the annotation is equals than")
    void test_annotation_total_equals_than() {
        when(annotation.value()).thenReturn(2);

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
        when(annotation.value()).thenReturn(3);

        assertThrows(AssertionFailedError.class, () -> processor.process(List.of(new QueryInformation(null,
                null,
                List.of(new QueryInfo("select ed from ed"),
                        new QueryInfo("select sdf from ed")))), annotation));
    }

    @Test
    @DisplayName("Test expected query")
    void test_expected_query() {
        when(annotation.value()).thenReturn(1);
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
        when(annotation.value()).thenReturn(1);
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
        when(annotation.value()).thenReturn(1);
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
        when(annotation.value()).thenReturn(1);
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
        when(annotation.value()).thenReturn(2);
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
        when(annotation.value()).thenReturn(1);
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
        when(annotation.value()).thenReturn(2);
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
        when(annotation.value()).thenReturn(1);
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

    @Test
    @DisplayName("Test multiple select queries")
    void  test_multiple_select_queries() {
        when(annotation.value()).thenReturn(3);
        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select t set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("update another_table set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select third_table set id = ?1 where name = ?2")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test multiple select queries and control the assertion error output")
    void  test_multiple_select_queries_assertion_output_control() {
         var outputMsg = "Expected SELECT queries: 1 but found: 3 [select into table_name(id, value) values (?1, ?2), select t set id = ?1 where name = ?2, select third_table set id = ?1 where name = ?2]";

        when(annotation.value()).thenReturn(1);

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select t set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("update another_table set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select third_table set id = ?1 where name = ?2")));

        var error = assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        assertEquals(outputMsg, error.getMessage());

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test error message content")
    void test_error_message_content() {
        when(annotation.value()).thenReturn(1);

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select t set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("update another_table set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select third_table set id = ?1 where name = ?2")));

        var error = assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        assertTrue(error.getMessage().contains("SELECT"));

        verify(annotation).value();
    }

    private QueryInformation createQueryInformation(String methodName, ExecutionInfo executionInfo, QueryInfo queryInfo) {
        return new QueryInformation(methodName, executionInfo, List.of(queryInfo));
    }

}

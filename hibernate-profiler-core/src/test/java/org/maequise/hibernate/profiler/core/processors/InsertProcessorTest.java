package org.maequise.hibernate.profiler.core.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.maequise.hibernate.profiler.core.TestUtils;
import org.maequise.hibernate.profiler.core.annotations.InsertQuery;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InsertProcessorTest {
    private Processor processor;
    private InsertQuery annotation;

    @BeforeEach
    void setUp() {
        annotation = mock(InsertQuery.class);
        when(annotation.queryExpected()).thenReturn("");
        processor = new InsertProcessor();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test behavior of insert annotation")
    void test_behavior_of_insert_annotation(int expected) {
        when(annotation.totalExpected()).thenReturn(expected);

        var data = Stream
                .generate(() -> TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into table_name(id, value) values (?1, ?2)")))
                .limit(expected)
                .toList();


        assertDoesNotThrow(() -> processor.process(data, annotation));
        verify(annotation).totalExpected();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test behavior of insert annotation")
    void test_behavior_of_insert_annotation_with_errors(int expected) {
        when(annotation.totalExpected()).thenReturn(expected + 1);

        var data = Stream
                .generate(() -> {
                    if ((expected) % 2 == 0) {
                        return TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into table_name(id, value) values (?1, ?2)"));
                    } else {
                        return TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)"));
                    }
                })
                .limit(expected)
                .toList();


        assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        verify(annotation).totalExpected();
    }

    @Test
    @DisplayName("Test that the queries controlled are insert queries")
    void test_that_the_queries_controled_are_insert_queries() {
        when(annotation.totalExpected()).thenReturn(1);

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into table_name(id, value) values (?1, ?2)")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).totalExpected();
    }

    @Test
    @DisplayName("Test that the inserted query is equals to the provided one")
    void test_that_the_inserted_query_is_equals_to_the_provided_one() {
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("insert into t(id, val) values(?1, ?2)");

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into t(id, val) values(?1, ?2)")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test that the inserted query is not equals to the provided one")
    void test_that_the_inserted_query_is_not_equals_to_the_provided_one() {
        var annotation = mock(InsertQuery.class);
        when(annotation.totalExpected()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("insert into t234(id, val) values(?1, ?2)");

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into t(id, val) values(?1, ?2)")));

        assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test multiple insert queries")
    void  test_multiple_insert_queries() {
        when(annotation.totalExpected()).thenReturn(3);
        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into t(id, val) values(?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into another_table(id, val) values(?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into third_table(id, val) values(?1, ?2)")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test multiple insert queries and control the assertion error output")
    void  test_multiple_select_queries_assertion_output_control() {
        var outputMsg = "Expected queries: 1 but found: 3 [insert into t(id, val) values(?1, ?2), insert into another_table(id, val) values(?1, ?2), insert into third_table(id, val) values(?1, ?2)]";

        when(annotation.totalExpected()).thenReturn(1);

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into t(id, val) values(?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into another_table(id, val) values(?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into third_table(id, val) values(?1, ?2)")));

        var error = assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        assertEquals(outputMsg, error.getMessage());

        verify(annotation).queryExpected();
    }
}

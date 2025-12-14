package org.maequise.hibernate.profiler.core.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.maequise.hibernate.profiler.core.TestUtils;
import org.maequise.hibernate.profiler.core.annotations.ExpectedDeleteQuery;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteProcessorTest {
    Processor processor;
    ExpectedDeleteQuery annotation;
    
    @BeforeEach
    void setUp() {
        processor = new DeleteProcessor();
        annotation = mock(ExpectedDeleteQuery.class);
        when(annotation.queryExpected()).thenReturn("");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test behavior of delete query")
    void test_behavior_of_delete_query(int expected) {
        when(annotation.value()).thenReturn(expected);

        var data = Stream.generate(() -> TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete table set column = ?1 where id = ?2")))
                .limit(expected)
                .toList();

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).value();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test behavior of insert annotation")
    void test_behavior_of_insert_annotation_with_errors(int expected) {
        when(annotation.value()).thenReturn(expected + 1);

        var data = Stream
                .generate(() -> {
                    if ((expected) % 2 == 0) {
                        return TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete table_name set id = ?1"));
                    } else {
                        return TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)"));
                    }
                })
                .limit(expected)
                .toList();


        assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        verify(annotation).value();
    }

    @Test
    @DisplayName("Test that the queries controlled are delete queries")
    void test_that_the_queries_controlled_are_insert_queries() {
        when(annotation.value()).thenReturn(1);

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete table_name set id = ?1")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).value();
    }

    @Test
    @DisplayName("Test that the delete query is equals to the provided one")
    void test_that_the_inserted_query_is_equals_to_the_provided_one() {
        when(annotation.value()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("delete table_name set id = ?1");

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete table_name set id = ?1")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test that the delete query is not equals to the provided one")
    void test_that_the_inserted_query_is_not_equals_to_the_provided_one() {
        when(annotation.value()).thenReturn(1);
        when(annotation.queryExpected()).thenReturn("delete table_name set id = ?1, set value = ?2");

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete table_name set id = ?1")));

        assertThrows(AssertionFailedError.class, () -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test multiple delete queries")
    void  test_multiple_delete_queries() {
        when(annotation.value()).thenReturn(3);
        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete t set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete another_table set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete third_table set id = ?1 where name = ?2")));

        assertDoesNotThrow(() -> processor.process(data, annotation));

        verify(annotation).queryExpected();
    }

    @Test
    @DisplayName("Test multiple delete queries and control the assertion error output")
    void  test_multiple_select_queries_assertion_output_control() {
        var outputMsg = "Expected DELETE queries: 1 but found: 3 [delete t set id = ?1 where name = ?2, delete another_table set id = ?1 where name = ?2, delete third_table set id = ?1 where name = ?2]";

        when(annotation.value()).thenReturn(1);

        var data = List.of(TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete t set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete another_table set id = ?1 where name = ?2")),
                TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("delete third_table set id = ?1 where name = ?2")));

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

        assertTrue(error.getMessage().contains("DELETE"));

        verify(annotation).value();
    }
}
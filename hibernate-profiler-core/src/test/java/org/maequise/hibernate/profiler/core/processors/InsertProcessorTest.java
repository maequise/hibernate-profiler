package org.maequise.hibernate.profiler.core.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.maequise.hibernate.profiler.core.TestUtils;
import org.maequise.hibernate.profiler.core.annotations.InsertQuery;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InsertProcessorTest {
    private Processor processor;

    @BeforeEach
    void setUp() {
        processor = new InsertProcessor();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test behavior of insert annotation")
    void test_behavior_of_insert_annotation(int expected) {
        var annotation = mock(InsertQuery.class);

        when(annotation.totalExpected()).thenReturn(expected);

        var data = Stream
                .generate(() -> TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into table_name(id, value) values (?1, ?2)")))
                .limit(expected)
                .toList();


        assertDoesNotThrow(() -> processor.process(data, annotation));
    }

   /* @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test behavior of insert annotation")
    void test_behavior_of_insert_annotation(int expected) {
        var annotation = mock(InsertQuery.class);

        when(annotation.totalExpected()).thenReturn(expected);

        var data = Stream
                .generate(() -> {
                    if((expected) %2 == 0){
                        return TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("insert into table_name(id, value) values (?1, ?2)"))
                    }else {
                        return TestUtils.createQueryInformation(null, null, TestUtils.createQueryInfo("select into table_name(id, value) values (?1, ?2)"))
                    }
                })
                .limit(expected)
                .toList();


        assertDoesNotThrow(() -> processor.process(data, annotation));
    }
*/
}
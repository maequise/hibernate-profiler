package org.maequise.hibernate.profiler.core.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.maequise.hibernate.profiler.core.DataSourceHolder;
import org.maequise.hibernate.profiler.core.QueryInformation;
import org.maequise.hibernate.profiler.core.annotations.ExpectedDeleteQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedInsertQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedSelectQuery;
import org.maequise.hibernate.profiler.core.annotations.ExpectedUpdateQuery;
import org.opentest4j.AssertionFailedError;

import javax.swing.text.html.Option;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@ExtendWith(HibernateProfilerExtension.class)
class WorkflowExtensionTest {
    private HibernateProfilerExtension hibernateProfilerExtension;
    
    @BeforeEach
    void init() {
        hibernateProfilerExtension = new HibernateProfilerExtension();
    }
    
    @Test
    @DisplayName("Expect the workflow continues")
    void expect_the_workflow_continues() throws Exception {
        ExtensionContext extensionContext = createExtensionContextMock();
        var errors = assertThrows(AssertionFailedError.class, () -> hibernateProfilerExtension.afterEach(extensionContext));

        assertNotNull(errors);
        //assertInstanceOf(List.class, errors.getActual().getType());
        assertEquals(3, ((List)errors.getActual().getEphemeralValue()).size());
    }

    @Disabled
    @Test
    @DisplayName("Test expectation message")
    void test_expected_message() {
        ExtensionContext extensionContext = createExtensionContextMock();
        String errorMessageExpected = "Assertion error, expected: [] but got: []";

        var errors = assertThrows(AssertionFailedError.class, () -> hibernateProfilerExtension.afterEach(extensionContext));

        assertNotNull(errors);
        assertEquals(errorMessageExpected, errors.getMessage());
    }

    private ExtensionContext createExtensionContextMock() {
        //setup the test
        ExpectedSelectQuery expectedSelectQueryMock = mock(ExpectedSelectQuery.class);
        ExpectedUpdateQuery expectedUpdateQueryMock = mock(ExpectedUpdateQuery.class);
        ExpectedInsertQuery expectedInsertQueryMock = mock(ExpectedInsertQuery.class);
        ExpectedDeleteQuery expectedDeleteQueryMock = mock(ExpectedDeleteQuery.class);

        Annotation[] annotations = new Annotation[]{expectedInsertQueryMock, expectedSelectQueryMock, expectedUpdateQueryMock, expectedDeleteQueryMock};

        ExtensionContext extensionContext = mock(ExtensionContext.class);
        Method mockMethod = mock(Method.class);
        DataSourceHolder.addData("expect_the_workflow_continues", new QueryInformation(null, null, List.of()));

        //setup the mocks
        //setup the annotations
        when(expectedInsertQueryMock.value()).thenReturn(1);
        when(expectedUpdateQueryMock.value()).thenReturn(1);
        when(expectedSelectQueryMock.value()).thenReturn(1);
        when(expectedDeleteQueryMock.value()).thenReturn(1);

        when(expectedInsertQueryMock.queryExpected()).thenReturn("");
        when(expectedUpdateQueryMock.queryExpected()).thenReturn("");
        when(expectedSelectQueryMock.queryExpected()).thenReturn("");
        when(expectedDeleteQueryMock.queryExpected()).thenReturn("");

        //setup the extension context
        when(mockMethod.getName()).thenReturn("expect_the_workflow_continues");
        when(mockMethod.getAnnotations()).thenReturn(annotations);
        when(extensionContext.getTestMethod()).thenReturn(Optional.of(mockMethod));

        return extensionContext;
    }
}

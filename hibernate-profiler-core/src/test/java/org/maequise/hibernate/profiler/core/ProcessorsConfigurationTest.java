package org.maequise.hibernate.profiler.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorsConfigurationTest {
    @Test
    @DisplayName("should get a number of processors")
    void should_get_processors() {
        var processors = ProcessorsConfiguration.getProcessors();

        assertNotNull(processors);
        assertEquals(4, processors.size());
    }

    @Test
    @DisplayName("Should throw exception by instantiate by reflection")
    void should_throw_exception_by_instantiate_by_reflection() throws Exception {
        var constructor = ProcessorsConfiguration.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        var exception = assertThrows(InvocationTargetException.class, () -> constructor.newInstance());

        assertInstanceOf(IllegalStateException.class, exception.getCause());
    }
}
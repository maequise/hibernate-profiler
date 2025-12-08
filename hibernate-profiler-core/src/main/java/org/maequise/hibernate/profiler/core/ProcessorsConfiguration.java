package org.maequise.hibernate.profiler.core;

import org.maequise.hibernate.profiler.core.processors.*;

import java.util.HashMap;
import java.util.Map;

/// Class to instantiate and shared the different implementation of the [Processor] interface
public final class ProcessorsConfiguration {
    private static final Map<String, Processor> PROCESSORS = new HashMap<>();

    /// Static initialisation
    static {
        PROCESSORS.put("select", new SelectProcessor());
        PROCESSORS.put("insert", new InsertProcessor());
        PROCESSORS.put("update", new UpdateProcessor());
        PROCESSORS.put("delete", new DeleteProcessor());
    }

    private ProcessorsConfiguration() {
        throw new IllegalStateException("Utility class");
    }

    /// Retrieve he processor instances
    public static Map<String, Processor> getProcessors() {
        return PROCESSORS;
    }
}

package org.maequise.hibernate.profiler.core;

import org.maequise.hibernate.profiler.core.processors.Processor;
import org.maequise.hibernate.profiler.core.processors.SelectProcessor;

import java.util.HashMap;
import java.util.Map;

public final class ProcessorsConfiguration {
    private static final Map<String, Processor> PROCESSORS = new HashMap<>();

    static {
        PROCESSORS.put("select", new SelectProcessor());
    }

    public static Map<String, Processor> getProcessors() {
        return PROCESSORS;
    }
}

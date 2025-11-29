package org.maequise.hibernate.profiler.core.processors;

import org.maequise.hibernate.profiler.core.QueryInformation;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Processor {
    void process(List<QueryInformation> queryInformation, Annotation annotation);
}

package org.maequise.hibernate.profiler.core.annotations;

/**
 * Annotation to activate experimental features
 * By default {@code false} the experimental features are disabled
 */
public @interface HibernateProfilerExperimental {
    boolean value() default false;
}

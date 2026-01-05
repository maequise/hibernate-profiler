package org.maequise.hibernate.profiler.core;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

public final class TestUtils {
    private TestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static QueryDataHolder createQueryInformation(String threadName, ExecutionInfo executionInfo, QueryInfo... queryInfo) {
        return new QueryDataHolder(threadName, executionInfo, List.of(queryInfo));
    }

    public static QueryInfo createQueryInfo(String query) {
        return new QueryInfo(query);
    }
}

package org.maequise.hibernate.profiler.core;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/// Record class to construct data coming from the [net.ttddyy.dsproxy.support.ProxyDataSource] listener
///
/// @param threadName of the current test execution
/// @param executionInfo coming from [net.ttddyy.dsproxy.listener.QueryExecutionListener]
/// @param queryInfoList coming from [net.ttddyy.dsproxy.listener.QueryExecutionListener]
public record QueryDataHolder(String threadName, ExecutionInfo executionInfo,
                              List<QueryInfo> queryInfoList) {

    public List<String> listQueries() {
        return this.queryInfoList.stream().map(QueryInfo::getQuery).toList();
    }
}

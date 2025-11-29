package org.maequise.hibernate.profiler.core;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

public record QueryInformation(String threadName, ExecutionInfo executionInfo,
                               List<QueryInfo> queryInfoList) {

    public List<String> listQueries() {
        return this.queryInfoList.stream().map(QueryInfo::getQuery).toList();
    }
}

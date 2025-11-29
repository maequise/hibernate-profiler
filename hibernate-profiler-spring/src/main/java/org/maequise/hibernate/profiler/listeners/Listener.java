package org.maequise.hibernate.profiler.listeners;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import org.maequise.hibernate.profiler.core.DataSourceHolder;
import org.maequise.hibernate.profiler.core.QueryInformation;

import java.util.List;

public class Listener implements QueryExecutionListener {
    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        var nameTh = Thread.currentThread().getName();

        DataSourceHolder.addData(nameTh, new QueryInformation(nameTh, execInfo, queryInfoList));
    }

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {

    }
}

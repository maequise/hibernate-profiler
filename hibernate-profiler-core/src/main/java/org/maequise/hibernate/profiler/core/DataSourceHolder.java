package org.maequise.hibernate.profiler.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DataSourceHolder {
    private static final ThreadLocal<DataSourceHolder> INSTANCE = ThreadLocal.withInitial(DataSourceHolder::new);
    private static final Map<String, List<QueryInformation>> connectionsNamed = new ConcurrentHashMap<>();

    private DataSourceHolder() {
    }

    public static DataSourceHolder getInstance() {
        return INSTANCE.get();
    }

    public static void addData(String threadName, QueryInformation information) {
        connectionsNamed.computeIfAbsent(threadName, k -> new ArrayList<>()).add(information);

        connectionsNamed.computeIfPresent(threadName, (k, v) -> {
            if (!v.contains(information)) {
                v.add(information);
            }
            return v;
        });
    }

    public static Map<String, List<QueryInformation>> getConnectionsNamed() {
        return connectionsNamed;
    }

}

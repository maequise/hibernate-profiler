package org.maequise.hibernate.profiler.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Object to hold the different data coming from the [net.ttddyy.dsproxy.support.ProxyDataSource]
///
/// @author maequise
/// @since 0.1.0
public final class DataSourceHolder {
    /// Register all connection name associated to a specific thread
    private static final Map<String, List<QueryDataHolder>> connectionsNamed = new ConcurrentHashMap<>();

    private DataSourceHolder() {
        throw new IllegalStateException("Utility class");
    }

    /// Call this method if you want to add data in the current pool of shared information
    ///
    /// @param threadName to identify the data associated to a thread (set in
    /// [beforeEach extension][org.maequise.hibernate.profiler.core.extension.HibernateProfilerExtension#beforeEach(org.junit.jupiter.api.extension.ExtensionContext)])
    /// @param information contains the query information related to database operation
    public static void addData(String threadName, QueryDataHolder information) {
        connectionsNamed.computeIfAbsent(threadName, k -> new ArrayList<>()).add(information);

        connectionsNamed.computeIfPresent(threadName, (k, v) -> {
            if (!v.contains(information)) {
                v.add(information);
            }
            return v;
        });
    }

    /// Retrieve the different connections name
    /// As a connection name is associated to a Thread name
    public static Map<String, List<QueryDataHolder>> getConnectionsNamed() {
        return connectionsNamed;
    }

    /// If need to clear the data
    public static void clear() {
        connectionsNamed.clear();
    }

}

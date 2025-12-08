package org.maequise.hibernate.profiler.core;

import net.ttddyy.dsproxy.QueryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataSourceHolderTest {
    @BeforeEach
    void setUp() {
        DataSourceHolder.clear();
    }

    @Test
    @DisplayName("Test insert new data")
    void test_insert_new_data() {
        DataSourceHolder.addData("test", TestUtils.createQueryInformation("test", null, new QueryInfo("my query")));

        var connectionsKnown = DataSourceHolder.getConnectionsNamed();

        assertNotNull(connectionsKnown);
        assertNotNull(connectionsKnown.get("test"));
        assertEquals(1, connectionsKnown.size());
        assertEquals(1, connectionsKnown.get("test").size());
    }

    @Test
    @DisplayName("Test update the current entry")
    void test_update_current_entry() {
        DataSourceHolder.addData("test2", TestUtils.createQueryInformation("test2", null, new QueryInfo("test")));
        DataSourceHolder.addData("test2", TestUtils.createQueryInformation("test2", null, new QueryInfo("super test")));

        var connectionsKnown = DataSourceHolder.getConnectionsNamed();

        assertNotNull(connectionsKnown);
        assertNotNull(connectionsKnown.get("test2"));
        assertEquals(1, connectionsKnown.size());
        assertEquals(2, connectionsKnown.get("test2").size());
    }

    @Test
    @DisplayName("Test concurrency insert")
    void test_concurrency_insert() throws InterruptedException {
        try (var executor = Executors.newFixedThreadPool(5)) {
            var count = new CountDownLatch(20);

            var operations = Stream.generate(() -> (Runnable) () -> {
                        var currentThread = Thread.currentThread();
                        currentThread.setName("test");
                        DataSourceHolder.addData("test", TestUtils.createQueryInformation("test", null, new QueryInfo("test " + currentThread.threadId())));
                        count.countDown();
                    }).limit(20)
                    .toList();


            for (var ope : operations) {
                executor.execute(ope);
            }

            //trick to wait before launch the test assertions
            count.await();

            var connectionsKnown = DataSourceHolder.getConnectionsNamed();
            assertNotNull(connectionsKnown);
            assertNotNull(connectionsKnown.get("test"));
            assertEquals(1, connectionsKnown.size());
            assertEquals(20, connectionsKnown.get("test").size());
        }

    }

}
package my.plugin;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.impl.logging.LogService;
import org.neo4j.kernel.impl.spi.KernelContext;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;
import org.neo4j.logging.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterSampleExtensionFactory extends KernelExtensionFactory<RegisterSampleExtensionFactory.Dependencies> {

    @Override
    public Lifecycle newInstance(KernelContext context, Dependencies dependencies) {

        return new LifecycleAdapter() {

            Log log = dependencies.log().getUserLog(getClass());

            private MyPluginEventHandler handler;
            private ExecutorService executor;

            @Override
            public void start() {
                log.info("STARTING: My Sample Plugin");

                executor = Executors.newFixedThreadPool(8);

                handler = new MyPluginEventHandler(dependencies.getGraphDatabaseService(), executor, log);
                dependencies.getGraphDatabaseService().registerTransactionEventHandler(handler);
            }

            @Override
            public void shutdown() {
                log.info("STOPPING: My Sample Plugin");
                executor.shutdown();
                dependencies.getGraphDatabaseService().unregisterTransactionEventHandler(handler);
            }
        };
    }

    public interface Dependencies {
        GraphDatabaseService getGraphDatabaseService();
        LogService log();
    }

    public RegisterSampleExtensionFactory() {
        super("registerTransactionEventHandler");
    }
}

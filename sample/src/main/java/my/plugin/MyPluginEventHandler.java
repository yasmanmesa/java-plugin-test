package my.plugin;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.logging.Log;

import java.util.concurrent.ExecutorService;

public class MyPluginEventHandler implements TransactionEventHandler {

    private GraphDatabaseService db;
    private ExecutorService executor;
    private Log log;

    public MyPluginEventHandler(GraphDatabaseService db, ExecutorService executor, Log log) {
        this.db = db;
        this.executor = executor;
        this.log = log;
    }

    @Override
    public Object beforeCommit(TransactionData transactionData) {
        return null;
    }

    @Override
    public void afterCommit(TransactionData transactionData, Object o) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try (Transaction transaction = db.beginTx()) {

                    for (Node node : transactionData.createdNodes()) {
                        if (node.getProperty("name").equals("Yoandry")){
                            log.info("AL FIN");
                        }
                    }

                    transaction.success();

                } catch (Throwable e) {
                    log.info("An error occurred", e);
                }
            }
        });
    }

    @Override
    public void afterRollback(TransactionData transactionData, Object o) {

    }
}

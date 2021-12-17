package bmstu.ru.anonymizer;

import akka.actor.ActorRef;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class AppWatcher implements Watcher {
    private final ActorRef config;
    private ZooKeeper zk;
    private static final String ZK_SERVERS_PATH = "/servers";

    public AppWatcher(ActorRef config) {
        this.config = config;
    }

    public AppWatcher(ActorRef config, ZooKeeper zk) {
        this.config = config;
        this.zk = zk;
    }

    public void process(WatchedEvent event) {

    }
}

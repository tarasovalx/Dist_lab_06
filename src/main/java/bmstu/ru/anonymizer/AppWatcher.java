package bmstu.ru.anonymizer;

import akka.actor.ActorRef;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import java.util.ArrayList;
import java.util.List;

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
        if (event == null) {
            return;
        }
        KeeperState keeperState = event.getState();

//        String path = event.getPath();
        if (Event.KeeperState.SyncConnected == keeperState) {
            try {
                List<String> list = zk.getChildren(ZK_SERVERS_PATH, this);
                ArrayList<String> serverData = new ArrayList<>();
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }
}

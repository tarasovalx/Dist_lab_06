package bmstu.ru.anonymizer;

import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

public class AppWatcher implements Watcher {
    private static final String ZK_SERVERS_PATH = "/servers";
    private final ActorRef config;
    private ZooKeeper zk;

    public AppWatcher(ActorRef config) {
        this.config = config;
    }

    public AppWatcher(ActorRef config, ZooKeeper zk) {
        this.config = config;
        this.zk = zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        KeeperState keeperState = event.getState();

        if (Event.KeeperState.SyncConnected == keeperState) {
            try {
                List<String> list = zk.getChildren(ZK_SERVERS_PATH, this);
                ArrayList<String> serverData = new ArrayList<>();

                for (String name : list) {
                    serverData.add(new String(zk.getData(ZK_SERVERS_PATH + '/' + name, this, null)));
                }

                config.tell(new ServerList(serverData), ActorRef.noSender());
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

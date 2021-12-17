package bmstu.ru.anonymizer;

import akka.actor.AbstractActor;

import java.util.ArrayList;

public class CfgStorageActor extends AbstractActor {
    private ArrayList<String> data = new ArrayList<>();
    @Override
    public Receive createReceive() {
        return null;
    }

    private void saveServerList(ServerList list) {
        data = new ArrayList<>(list.getData());
        System.out.println(data);
    }
}

package bmstu.ru.anonymizer;

import java.util.List;

public class ServerList {

    private final List<String> data;

    public List<String> getData() {
        return data;
    }

    public ServerList(List<String> data) {
        this.data = data;
    }
}

package bmstu.ru.anonymizer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;

import java.time.Duration;

public class AppNode extends AllDirectives {

    private static final String HOSTNAME = "localhost";

    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final int ZK_TIMEOUT = 3000;
    private static final int TIMEOUT = 5000;
    private static final String ZK_PATH = "\"/servers/s\"";


    private static ActorRef config;
    private static Integer port;
    private static ActorSystem system;
    private static AppWatcher watcher;

    private Route createRoute(ActorSystem system) {
        return route(
                get()
        );
    }

    public static void main(String[] args) {

    }

    private Route get() {
        return parameter("url", url ->
                        parameter("count", count -> {
                            int counter = Integer.parseInt(count);
                            final Http http = Http.get(system);
                            if (counter == 0) {
                                return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                            }


                        })
        );
    }
}

package bmstu.ru.anonymizer;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooKeeper;


import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

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
        return route(get());
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException{
        port = Integer.parseInt(args[0]);
        system = ActorSystem.create("routes");

        config = system.actorOf(Props.create(CfgStorageActor.class));
        watcher = new AppWatcher(config);
        ZooKeeper zoo = new ZooKeeper(ZK_ADDRESS, ZK_TIMEOUT, watcher);

        watcher.setZk(zoo);
        zoo.create(ZK_PATH,
                port.toString().getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE ,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );



        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AppNode instance = new AppNode();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow;
        routeFlow = instance.createRoute(system).flow(system, materializer);

        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOSTNAME, port),
                materializer
        );

        System.out.println(String.format("Server online at http://%s:%d/\nPress RETURN to stop...",HOSTNAME, port));

        System.in.read();
        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private Route get() {
        return parameter("url", url ->
            parameter("count", count -> {
                int counter = Integer.parseInt(count);
                final Http http = Http.get(system);
                if (counter == 0) {
                    return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                }

                HttpRequest r = HttpRequest.create(
                    String.format("http://localhost:%d/?url=%s&count=%d",
                        Integer.parseInt(
                            (String) Patterns
                                    .ask(
                                            config,
                                            new ServerRequest(),
                                            Duration.ofMillis(TIMEOUT)
                                    )
                                    .toCompletableFuture()
                                    .join()),
                        url,
                        counter - 1
                    )
                );

                return completeWithFuture(http.singleRequest(r));
            })
        );
    }
}

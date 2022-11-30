package io.envoyproxy.controlplane.pemo.grpcserver;

import io.envoyproxy.controlplane.cache.v3.SimpleCache;
import io.envoyproxy.controlplane.pemo.grpcserver.callback.MyDiscoveryServerCallbacks;
import io.envoyproxy.controlplane.server.V3DiscoveryServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GrpcServer implements InitializingBean {

    @Value("12345")
    private Integer grpcServerPort;

    private static final String GROUP = "key";

    static  SimpleCache<String> cache = new SimpleCache<>(node -> GROUP);

    public static SimpleCache<String> getCache (){
        return cache;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("\033[34m ＝＝＝＝＝＝＝＝＝＝＝＝＝＝InitializingBean afterPropertiesSet＝＝＝＝＝＝");

        // create cache

        // handle server. cache and callback
        MyDiscoveryServerCallbacks mycallback = new MyDiscoveryServerCallbacks();
        V3DiscoveryServer v3DiscoveryServer = new V3DiscoveryServer(mycallback, cache);

        // embeded service to server, so that server can recognize the service and callbacks
        ServerBuilder serverBuilder =
                NettyServerBuilder.forPort(grpcServerPort)
                        .addService(v3DiscoveryServer.getAggregatedDiscoveryServiceImpl())
                        .addService(v3DiscoveryServer.getClusterDiscoveryServiceImpl())
                        .addService(v3DiscoveryServer.getEndpointDiscoveryServiceImpl())
                        .addService(v3DiscoveryServer.getListenerDiscoveryServiceImpl())
                        .addService(v3DiscoveryServer.getRouteDiscoveryServiceImpl());

        Server server = serverBuilder.build();

        server.start();

        System.out.println("Grpc server on " + server.getPort());
        System.out.println("Grpc Server services : ");
        server.getServices().forEach(System.out::println);
        System.out.println("===================================================================\033[0m");

    }
}

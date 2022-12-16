## Client docker image
[envoyproxy/envoy v1.23.1](https://github.com/ppdouble/envoymesh-practice/tree/main/pp-ldscds-grpc-javacontrolplane)

## Client source code

The sample of using generated code in envoy source:

In `envoy/source/server/server.cc`: 

```cpp
#include "envoy/admin/v3/config_dump.pb.h"
#include "envoy/config/bootstrap/v3/bootstrap.pb.h"
```

generated from

```proto
./api/envoy/config/bootstrap/v3/bootstrap.proto
./api/envoy/admin/v3/config_dump.proto
```

## java control plane:

Customized server source code

Customized cache source code

`api-0.1.35.jar`

With springboot

## README of proto file in api-0.1.35.jar

| proto file sample | generated(protoc compiled) and java compiled |
| ---- | ---- |
| `api-0.1.35.jar!/envoy/service/discovery/v3/discovery.proto` | `api-0.1.35.jar!/io.envoyproxy/envoy/service/discovery/v3/v3DiscoveryRequest.class` |
| | ... |
| `api-0.1.35.jar!/envoy/service/discovery/v3/ads.proto`  | `api-0.1.35.jar!/io.envoyproxy/envoy/service/discovery/v3/AggregatedDiscoveryServiceGrpc.class` |
| | `api-0.1.35.jar!/io.envoyproxy/envoy/service/discovery/v3/AggregatedDiscoveryService.class` |
| `api-0.1.35.jar!/envoy/service/cluster/v3/cds.proto` |`api-0.1.35.jar!/io.envoyproxy/envoy/service/cluster/v3/ClusterDiscoveryService.class` |
|    | `api-0.1.35.jar!/io.envoyproxy/envoy/service/cluster/v3/ClusterDiscoveryServiceGrpc.class` |
| | ... |



### CDS proto file in api-0.1.35.jar

In `api-0.1.35.jar!/envoy/service/cluster/v3/cds.proto`, it defines three Remote Procedure Call:

bi-direction stream

```java
  rpc StreamClusters(stream discovery.v3.DiscoveryRequest)
      returns (stream discovery.v3.DiscoveryResponse) {
  }
```

```java
 rpc DeltaClusters(stream discovery.v3.DeltaDiscoveryRequest)
      returns (stream discovery.v3.DeltaDiscoveryResponse) {
  }
```

unary

```java
  rpc FetchClusters(discovery.v3.DiscoveryRequest) returns (discovery.v3.DiscoveryResponse) {
    option (google.api.http).post = "/v3/discovery:clusters";
    option (google.api.http).body = "*";
  }
```

Same with EDS, LDS, RDS, SDS

`api-0.1.35.jar!/envoy/service/endpoint/v3/eds.proto`

`api-0.1.35.jar!/envoy/service/listner/v3/lds.proto`

`api-0.1.35.jar!/envoy/service/route/v3/rds.proto`

`api-0.1.35.jar!/envoy/service/secret/v3/sds.proto`


### Generated source Grpc file sample

In `io.envoyproxy.controlplane.serve.V3DiscoveryServer` imported from `api-0.1.35.jar!/io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc.class`

```java
import static io.envoyproxy.envoy.service.discovery.v3.AggregatedDiscoveryServiceGrpc.AggregatedDiscoveryServiceImplBase;
import static io.envoyproxy.envoy.service.endpoint.v3.EndpointDiscoveryServiceGrpc.EndpointDiscoveryServiceImplBase;
import static io.envoyproxy.envoy.service.listener.v3.ListenerDiscoveryServiceGrpc.ListenerDiscoveryServiceImplBase;
import static io.envoyproxy.envoy.service.route.v3.RouteDiscoveryServiceGrpc.RouteDiscoveryServiceImplBase;
import static io.envoyproxy.envoy.service.secret.v3.SecretDiscoveryServiceGrpc.SecretDiscoveryServiceImplBase;
```

The client using `ads{}` method is looking for `v3.AggregatedDiscoveryService/StreamAggregatedResourcest` when connecting server. If the service 
is not registered in server. It will get `Method not found: envoy.service.discovery.v3.AggregatedDiscoveryService/StreamAggregatedResources`.


## When Check ads

In [SimpleCache.java](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/cache/src/main/java/io/envoyproxy/controlplane/cache/SimpleCache.java)

> {@code SimpleCache} provides a default implementation of {@link SnapshotCache}. It maintains a single versioned
  {@link Snapshot} per node group. For the protocol to work correctly in ADS mode, EDS/RDS requests are responded to
  only when all resources in the snapshot xDS response are named as part of the request. It is expected that the CDS
  response names all EDS clusters, and the LDS response names all RDS routes in a snapshot, to ensure that Envoy makes
  the request for all EDS clusters or RDS routes eventually.

[private boolean respond(Watch watch, U snapshot, T group) {}](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/cache/src/main/java/io/envoyproxy/controlplane/cache/SimpleCache.java#L461)

```java
    if (!watch.request().getResourceNamesList().isEmpty() && watch.ads()){
        Collection<String> missingNames=watch.request().getResourceNamesList().stream()
        .filter(name->!snapshotResources.containsKey(name))
        .collect(Collectors.toList());

        if(!missingNames.isEmpty()){
        LOGGER.info(
        "not responding in ADS mode for {} from node {} at version {} for request [{}] since [{}] not in snapshot",
        watch.request().getTypeUrl(),
        group,
        snapshot.version(watch.request().getResourceType(),watch.request().getResourceNamesList()),
        String.join(", ",watch.request().getResourceNamesList()),
        String.join(", ",missingNames));

        return false;
        }
    }
```

## When call responseConsumer.accept()

The beginning. In [DiscoveryRequestStreamObserver.java](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/server/src/main/java/io/envoyproxy/controlplane/server/DiscoveryRequestStreamObserver.java#L89)    

```java
      computeWatch(requestTypeUrl, () -> discoveryServer.configWatcher.createWatch(
          ads(),
          request,
          ackedResources(requestTypeUrl),
          r -> executor.execute(() -> send(r, requestTypeUrl)),
          hasClusterChanged
      ));

```

=>
[SimpleCache.java: return createWatch(ads, request, knownResourceNames, responseConsumer, false)](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/cache/src/main/java/io/envoyproxy/controlplane/cache/SimpleCache.java#L96)

=>
[SimpleCache.java: Watch watch = new Watch(ads, request, responseConsumer);](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/cache/src/main/java/io/envoyproxy/controlplane/cache/SimpleCache.java#L127)

=>
[Watch.java: super(request, responseConsumer)](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/cache/src/main/java/io/envoyproxy/controlplane/cache/Watch.java#L20)

=>
[AbstractWatch.java: responseConsumer.accept(response)](https://github.com/envoyproxy/java-control-plane/blob/v0.1.35/cache/src/main/java/io/envoyproxy/controlplane/cache/AbstractWatch.java#L63)

And then the `r -> executor.execute(() -> send(r, requestTypeUrl)` should be executed in `accept().

Because the beginning code can be rewritten as:

```java
      Consumer<Response> responseConsumer = new Consumer<Response>() {
        @Override
        public void accept(Response response) {
          executor.execute(new Runnable() {

            @Override
            public void run() {
              send(response, requestTypeUrl);
            }
          });
        }
      };

      computeWatch(requestTypeUrl, ()->discoveryServer.configWatcher.createWatch(
              ads(),
              request,
              ackedResources(requestTypeUrl),
              responseConsumer,
              hasClusterChanged
      ));
```
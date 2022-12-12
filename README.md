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

`api-0.1.35.jar`

`cache-0.1.35.jar`

## proto file README

| proto file sample | generated(protoc compiled) and java compiled |
| ---- | ---- |
| `api-0.1.35.jar!/envoy/service/discovery/v3/discovery.proto` | `api-0.1.35.jar!/io.envoyproxy/envoy/service/discovery/v3/v3DiscoveryRequest.class` |
| | ... |
| `api-0.1.35.jar!/envoy/service/discovery/v3/ads.proto`  | `api-0.1.35.jar!/io.envoyproxy/envoy/service/discovery/v3/AggregatedDiscoveryServiceGrpc.class` |
| | `api-0.1.35.jar!/io.envoyproxy/envoy/service/discovery/v3/AggregatedDiscoveryService.class` |
| `api-0.1.35.jar!/envoy/service/cluster/v3/cds.proto` |`api-0.1.35.jar!/io.envoyproxy/envoy/service/cluster/v3/ClusterDiscoveryService.class` |
|    | `api-0.1.35.jar!/io.envoyproxy/envoy/service/cluster/v3/ClusterDiscoveryServiceGrpc.class` |
| | ... |



### CDS proto file

`api-0.1.35.jar!/envoy/service/cluster/v3/cds.proto`

Defining three Remote Procedure Call:

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

same with EDS, LDS, RDS, SDS

`api-0.1.35.jar!/envoy/service/endpoint/v3/eds.proto`

`api-0.1.35.jar!/envoy/service/listner/v3/lds.proto`

`api-0.1.35.jar!/envoy/service/route/v3/rds.proto`

`api-0.1.35.jar!/envoy/service/secret/v3/sds.proto`


### generated source Grpc file sample

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
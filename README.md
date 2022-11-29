
Customized server source code

`api-0.1.35.jar`

`cache-0.1.35.jar`

## proto file README

**CDS**

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



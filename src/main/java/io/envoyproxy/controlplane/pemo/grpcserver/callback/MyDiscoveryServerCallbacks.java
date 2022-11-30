package io.envoyproxy.controlplane.pemo.grpcserver.callback;

import io.envoyproxy.controlplane.pemo.service.AfterValidationChecking;
import io.envoyproxy.controlplane.pemo.service.impl.AfterValidationCheckingImpl;
import io.envoyproxy.controlplane.server.DiscoveryServerCallbacks;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DeltaDiscoveryResponse;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest;
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.envoyproxy.controlplane.pemo.util.UtilConstant.myGlobalPrefix;

public class MyDiscoveryServerCallbacks implements DiscoveryServerCallbacks {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyDiscoveryServerCallbacks.class);

    private final String myPrefix = myGlobalPrefix + " Grpc Server callback";

    private AfterValidationChecking handleInvalidation = new AfterValidationCheckingImpl();


    @Override
    public void onStreamClose(long streamId, String typeUrl) {
        LOGGER.info("{} onStreamClose", myPrefix);
        DiscoveryServerCallbacks.super.onStreamClose(streamId, typeUrl);
    }

    @Override
    public void onStreamCloseWithError(long streamId, String typeUrl, Throwable error) {
        LOGGER.info("{} onStreamCloseWithError", myPrefix);
        DiscoveryServerCallbacks.super.onStreamCloseWithError(streamId, typeUrl, error);
    }

    @Override
    public void onStreamOpen(long streamId, String typeUrl) {
        LOGGER.info("{} onStreamOpen", myPrefix);

        DiscoveryServerCallbacks.super.onStreamOpen(streamId, typeUrl);
    }

    @Override
    public void onV3StreamRequest(long streamId, DiscoveryRequest request) {
        LOGGER.info("{} onV3StreamRequest", myPrefix);
        LOGGER.info("{}, REQUEST version {}, Node {}, with resources {}, typeurl {}, responseNonce {}", myPrefix,
                request.getVersionInfo(),
                request.getNode().getId(),
                request.getResourceNamesList(),
                request.getTypeUrl(),
                request.getResponseNonce());


        if (! request.hasErrorDetail()) {
            handleInvalidation.receiveACK(request.getVersionInfo(), request.getNode().getId(), request.getResponseNonce());
        } else {
            handleInvalidation.receiveNACK(request.getVersionInfo(), request.getNode().getId(), request.getResponseNonce());
            LOGGER.info("{}, NACK from envoy", myPrefix);
            LOGGER.info("{}, Errordetail {}", myPrefix, request.getErrorDetail().toString());
        }
    }

    @Override
    public void onV3StreamDeltaRequest(long streamId, DeltaDiscoveryRequest request) {
        LOGGER.info("{} onV3StreamDeltaRequest", myPrefix);

    }

    @Override
    public void onV3StreamResponse(long streamId, DiscoveryRequest request, DiscoveryResponse response) {
        LOGGER.info("{} onV3StreamResponse", myPrefix);
        LOGGER.info("{}, RESPONSE version {} with resource: {}, type {}, Nonce {}, controlplane {}", myPrefix,
                response.getVersionInfo(),
                response.getResourcesList(),
                response.getTypeUrl(),
                response.getNonce(),
                response.getControlPlane());

        DiscoveryServerCallbacks.super.onV3StreamResponse(streamId, request, response);
    }

    @Override
    public void onV3StreamDeltaResponse(long streamId, DeltaDiscoveryRequest request, DeltaDiscoveryResponse response) {
        LOGGER.info("{} onV3StreamDeltaResponse", myPrefix);

        DiscoveryServerCallbacks.super.onV3StreamDeltaResponse(streamId, request, response);
    }
}

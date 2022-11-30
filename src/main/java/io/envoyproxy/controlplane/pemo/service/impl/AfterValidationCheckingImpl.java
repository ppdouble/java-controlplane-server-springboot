package io.envoyproxy.controlplane.pemo.service.impl;

import io.envoyproxy.controlplane.pemo.grpcserver.callback.MyDiscoveryServerCallbacks;
import io.envoyproxy.controlplane.pemo.service.AfterValidationChecking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static io.envoyproxy.controlplane.pemo.util.UtilConstant.myGlobalPrefix;

@Component
public class AfterValidationCheckingImpl implements AfterValidationChecking {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyDiscoveryServerCallbacks.class);

    private final String myPrefix = myGlobalPrefix + " handleInValidation ";


    @Override
    public void snapshotInConsistent(String version) {
        LOGGER.info("{} snapshotInConsistent: version {}", myPrefix, version);
    }

    @Override
    public void receiveNACK(String version, String nodeId, String nonce) {
        LOGGER.info("{} receiveACK: version {}, nodeId {}, nonce {}", myPrefix, version, nodeId, nonce);
    }

    @Override
    public void receiveACK(String version, String nodeId, String nonce) {
        LOGGER.info("{} receiveACK: version {}, nodeId {}, nonce {}", myPrefix, version, nodeId, nonce);

    }
}

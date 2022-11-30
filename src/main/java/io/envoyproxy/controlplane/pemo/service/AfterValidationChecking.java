package io.envoyproxy.controlplane.pemo.service;

public interface AfterValidationChecking {

    void snapshotInConsistent (String version);

    void receiveNACK (String version, String nodeId, String nonce);

    void receiveACK (String version, String nodeId, String nonce);

}

package org.poc.ice;

import org.poc.sdp.SdpAttribute;
import org.poc.stun.net.Transport;

public class IceCandidateInfo {
    private final String foundation;
    private final String componentId;
    private final Transport transport;
    private final long priority;
    private final String hostname;
    private final int port;
    private final String type;

    public IceCandidateInfo(SdpAttribute attribute) {
        String[] tokens = attribute.getAttributeValue().split(" ");

        assert (tokens.length == 8);

        foundation = tokens[0].split(":")[1];
        componentId = tokens[1];
        transport = tokens[2].toLowerCase().contains("udp") ? Transport.UDP : Transport.TCP;
        priority = Long.valueOf(tokens[3]);
        hostname = tokens[4];
        port = Integer.valueOf(tokens[5]);
        type = tokens[7];
    }

    public String getFoundation() {
        return foundation;
    }

    public String getComponentId() {
        return componentId;
    }

    public Transport getTransport() {
        return transport;
    }

    public long getPriority() {
        return priority;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return " foundation: " + getFoundation()
            + " componentId: " + getComponentId()
            + " transport: " + getTransport()
            + " priority: " + getPriority()
            + " hostname: " + getHostname()
            + " port: " + getPort()
            + " type: " + getType();
    }
}
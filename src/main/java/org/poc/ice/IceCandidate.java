package org.poc.ice;

import org.poc.sdp.SdpAttribute;
import org.poc.stun.net.TransportAddress;

public class IceCandidate {
    private final SdpAttribute iceUfrag;
    private final SdpAttribute icePasswd;
    private final TransportAddress address;
    private final IceCandidateInfo iceCandidateInfo;

    public IceCandidate(SdpAttribute iceUfrag, SdpAttribute icePasswd, SdpAttribute iceCandidateInfo) {
        this.iceUfrag = iceUfrag;
        this.icePasswd = icePasswd;
        this.iceCandidateInfo = new IceCandidateInfo(iceCandidateInfo);
        this.address = new TransportAddress(this.iceCandidateInfo.getHostname(),
                                            this.iceCandidateInfo.getPort(),
                                            this.iceCandidateInfo.getTransport());
    }

    public String getIceUfrag() {
        return iceUfrag.getAttributeValue().split(":")[1];
    }

    public String getIcePasswd() {
        return icePasswd.getAttributeValue().split(":")[1];
    }

    public IceCandidateInfo getIceCandidateInfo() {
        return iceCandidateInfo;
    }


    public boolean isHost() {
        return getIceCandidateInfo().getType() != null && getIceCandidateInfo().getType().contains("host");
    }

    public TransportAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Candidate: "
            + " ufrag: " + getIceUfrag()
            + " pwd: " + getIcePasswd()
            + " candidate info: " + getIceCandidateInfo();
    }
}

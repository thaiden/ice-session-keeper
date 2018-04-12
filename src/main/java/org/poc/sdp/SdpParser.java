package org.poc.sdp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.poc.ice.IceCandidate;

public class SdpParser {

    public static List<IceCandidate> getIceCandidates(MediaInfo mediaInfo) {
        return getIceCandidates(mediaInfo.getSdp());
    }

    public static List<IceCandidate> getIceCandidates(String sdp) {
        List<IceCandidate> candidates = new ArrayList<>();

        List<String> tokens = Arrays.asList(sdp.split("\\r\\n"));
        List<SdpAttribute> sdpAttributes = tokens.stream()
                                                 .map(t -> new SdpAttribute(t, "="))
                                                 .collect(Collectors.toList());

        List<SdpAttribute> iceUfrags = sdpAttributes.stream()
                                                    .filter(a -> a.getType() == SdpAttribute.SdpAttributeType.ICE_UFRAG)
                                                    .collect(Collectors.toList());

        List<SdpAttribute> icePasswords = sdpAttributes.stream()
                                                       .filter(a -> a.getType() == SdpAttribute.SdpAttributeType.ICE_PASSWD)
                                                       .collect(Collectors.toList());

        List<SdpAttribute> iceCandidates = sdpAttributes.stream()
                                                        .filter(a -> a.getType() == SdpAttribute.SdpAttributeType.ICE_CANDIDATE)
                                                        .collect(Collectors.toList());

        if (iceUfrags.size() == icePasswords.size() && icePasswords.size() == iceCandidates.size()) {
            for (int idx = 0; idx < iceUfrags.size(); idx++) {
                candidates.add(new IceCandidate(iceUfrags.get(idx), icePasswords.get(idx), iceCandidates.get(idx)));
            }
        }

        return candidates;
    }
}

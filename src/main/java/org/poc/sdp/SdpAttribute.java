package org.poc.sdp;

public class SdpAttribute {
    enum SdpAttributeType {
        ICE_TYPE,
        ICE_UFRAG,
        ICE_PASSWD,
        ICE_CANDIDATE,
        MEDIA,
    }

    private final String attributeType;
    private final String attributeValue;
    private final SdpAttributeType type;


    public SdpAttribute(String splittableValue, String delimiter) {
        String [] tokens = splittableValue.split(delimiter, 2);

        assert (tokens.length == 2);

        attributeType = tokens[0];
        attributeValue = tokens[1];

        if (attributeValue.contains("ice-lite")) {
            type = SdpAttributeType.ICE_TYPE;
        } else if (attributeValue.contains("ice-ufrag")) {
            type = SdpAttributeType.ICE_UFRAG;
        } else if (attributeValue.contains("ice-pwd")) {
            type = SdpAttributeType.ICE_PASSWD;
        } else if (attributeValue.contains("candidate")) {
            type = SdpAttributeType.ICE_CANDIDATE;
        } else {
            type = SdpAttributeType.MEDIA;
        }
    }

    public String getAttributeType() {
        return attributeType;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public SdpAttributeType getType() {
        return type;
    }
}
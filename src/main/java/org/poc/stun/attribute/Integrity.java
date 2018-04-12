package org.poc.stun.attribute;

import org.poc.stun.SharedSecret;
import org.poc.stun.util.Utility;

public class Integrity extends MessageAttribute implements ContentDependentAttribute {

    private static final int DEFAULT_INTEGRITY_ATTRIBUTE_LENGTH = 20;

    private final SharedSecret sharedSecret;

    public Integrity(SharedSecret sharedSecret) {
        super(MESSAGE_INTEGRITY);
        this.sharedSecret = sharedSecret;
    }

    @Override
    public int getDataLength() {
        return DEFAULT_INTEGRITY_ATTRIBUTE_LENGTH;
    }

    @Override
    public byte[] encode(byte[] content, int offset, int length) {
        byte value[] = new byte[HEADER_LENGTH + getDataLength()];

        generateHeader(value, getDataLength());

        byte[] key = sharedSecret.generateRemoteKey();

        //now calculate the HMAC-SHA1
        byte[] hmacSha1Content = Utility.calculateHmacSha1(content, offset, length, key);

        //username
        System.arraycopy(hmacSha1Content, 0, value, 4, getDataLength());

        setEncodedContent(value);

        return value;
    }

    @Override
    protected byte[] getBytes() {
        throw  new UnsupportedOperationException("should be use encode method from ContentDependentAttribute");
    }
}

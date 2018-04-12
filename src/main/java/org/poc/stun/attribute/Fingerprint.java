package org.poc.stun.attribute;

import java.util.zip.CRC32;

public class Fingerprint extends MessageAttribute
    implements ContentDependentAttribute {

    public static final byte[] XOR_MASK = { 0x53, 0x54, 0x55, 0x4e};

    private static final int DEFAULT_FINGERPINT_LENGTH = 4;

    public Fingerprint() {
        super(FINGERPRINT);
    }

    @Override
    public byte[] encode(byte[] content, int offset, int length) {
        byte binValue[] = new byte[HEADER_LENGTH + getDataLength()];

        generateHeader(binValue, getDataLength());

        //calculate the check sum
        byte[] xorCrc32 = calculateXorCRC32(content, offset, length);

        //copy into the attribute;
        binValue[4] = xorCrc32[0];
        binValue[5] = xorCrc32[1];
        binValue[6] = xorCrc32[2];
        binValue[7] = xorCrc32[3];

        return binValue;
    }

    public static byte[] calculateXorCRC32(byte[] message, int offset, int len)
    {
        //now check whether the CRC really is what it's supposed to be.
        //re calculate the check sum
        CRC32 checksum = new CRC32();
        checksum.update(message, offset, len);

        long crc = checksum.getValue();
        byte[] xorCRC32 = new byte[4];

        xorCRC32[0] = (byte)((byte)((crc >> 24) & 0xff) ^ XOR_MASK[0]);
        xorCRC32[1] = (byte)((byte)((crc >> 16) & 0xff) ^ XOR_MASK[1]);
        xorCRC32[2] = (byte)((byte)((crc >> 8)  & 0xff) ^ XOR_MASK[2]);
        xorCRC32[3] = (byte)((byte) (crc        & 0xff) ^ XOR_MASK[3]);

        return xorCRC32;
    }

    @Override
    protected byte[] getBytes() {
        throw new UnsupportedOperationException("should be use encode method from ContentDependentAttribute");
    }

    @Override
    public int getDataLength() {
        return DEFAULT_FINGERPINT_LENGTH;
    }
}

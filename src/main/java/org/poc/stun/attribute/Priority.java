package org.poc.stun.attribute;

public class Priority extends MessageAttribute {
    private static final int DATA_LENGTH_PRIORITY = 4;

    private final long priority;

    public Priority(long priority) {
        super(PRIORITY);
        this.priority = priority;
    }

    @Override
    public int getDataLength() {
        return DATA_LENGTH_PRIORITY;
    }

    @Override
    protected byte[] getBytes() {
        byte[] binValue = new byte[HEADER_LENGTH + getDataLength()];

        generateHeader(binValue, getDataLength());

        //Priority
        binValue[4] = (byte)((priority & 0xFF000000L) >> 24);
        binValue[5] = (byte)((priority & 0x00FF0000L) >> 16);
        binValue[6] = (byte)((priority & 0x0000FF00L) >> 8);
        binValue[7] = (byte)(priority & 0x000000FFL);

        return binValue;
    }

    public long getPriority() {
        return priority;
    }
}

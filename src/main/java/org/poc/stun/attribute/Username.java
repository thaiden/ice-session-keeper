package org.poc.stun.attribute;

import org.poc.stun.util.SaslPrep;

public class Username extends MessageAttribute{
    private final String username;

    public Username(String username) {
        super(USERNAME);
        this.username = SaslPrep.prepare(username);
    }

    public Username(String lFrag, String rFrag) {
        super(USERNAME);
        this.username = SaslPrep.prepare(lFrag + ":" + rFrag);

        encode();
    }

    @Override
    public int getDataLength() {
        return username.getBytes().length;
    }

    @Override
    protected byte[] getBytes() {
        byte binValue[] = new byte[HEADER_LENGTH + getDataLength()
            //add padding
            + (4 - getDataLength() % 4) % 4];

        generateHeader(binValue, getDataLength());

        //username
        System.arraycopy(username.getBytes(), 0, binValue, 4, getDataLength());

        return binValue;
    }

    public String getUsername() {
        return username;
    }
}

package org.poc.stun.attribute;

import org.poc.stun.util.Utility;
import org.poc.stun.Encodable;

public abstract class MessageAttribute implements Attribute, Encodable {
    private byte[] encodedContent;

    private char type;

    public MessageAttribute(char type) {
        this.type = type;
    }

    @Override
    public char getType() {return type;}

    protected abstract byte[] getBytes();

    @Override
    public byte[] encode() {
        if (encodedContent == null) {
            setEncodedContent(getBytes());
        }

        return getEncodedContent();
    }

    protected void setEncodedContent(byte[] content) {
        encodedContent = content;

    }

    @Override
    public byte[] getEncodedContent() {
        return encodedContent;
    }

    public void generateHeader(byte[] value, int length) {
        Utility.generateHeader(value, getType(), length);
    }
}


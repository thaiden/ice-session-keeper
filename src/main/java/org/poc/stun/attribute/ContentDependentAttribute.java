package org.poc.stun.attribute;

public interface ContentDependentAttribute extends Attribute {
    byte[] encode(byte[] content, int offset, int length);
}

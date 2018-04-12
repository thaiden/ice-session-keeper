package org.poc.stun;

import java.io.IOException;

public interface Encodable {
    byte[] encode() throws IOException;
}

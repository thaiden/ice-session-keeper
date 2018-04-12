package org.poc;

public interface IceSession {
    boolean start();
    void stop();

    String id();
    boolean started();
}

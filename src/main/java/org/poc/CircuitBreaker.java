package org.poc;

public interface CircuitBreaker {
    String id();

    void increment();
    void reset();
    int counter();
}

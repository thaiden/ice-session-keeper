package org.poc.logger;

public interface Logger {
    void info(String message);
    void trace(String message);
    void debug(String message);
    void warn(String message);
    void error(String message);
    void error(String message, Throwable t);
}

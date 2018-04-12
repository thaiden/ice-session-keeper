package org.poc.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DefaultLogger implements Logger {

    private static final String DATE_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String INFO = "INFO - ";
    private static final String DEBUG = "DEBUG - ";
    private static final String TRACE = "TRACE - ";
    private static final String WARN = "WARN - ";
    private static final String ERROR = "ERROR - ";


    private static TimeZone timeZone;
    private static SimpleDateFormat dateFormat;

    static {
        timeZone = TimeZone.getTimeZone("UTC");
        dateFormat = new SimpleDateFormat(DATE_ISO8601);
        dateFormat.setTimeZone(timeZone);
    }

    @Override
    public void info(String message) {
        writeMessage(INFO, message);
    }

    @Override
    public void trace(String message) {
        writeMessage(TRACE, message);
    }

    @Override
    public void debug(String message) {
        writeMessage(DEBUG, message);
    }

    @Override
    public void warn(String message) {
        writeMessage(WARN, message);
    }

    @Override
    public void error(String message) {
        writeMessage(ERROR, message);
    }

    @Override
    public void error(String message, Throwable t) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        for (StackTraceElement element : t.getStackTrace()) {
            stringBuilder.append(element.getClassName())
                         .append(" method:")
                         .append(element.getMethodName())
                         .append(" line:")
                         .append(element.getLineNumber())
                         .append("\n");
        }

        writeMessage(ERROR, message + stringBuilder.toString());
    }

    private void writeMessage(String prefix, String message) {
        System.out.println("[LOGGER - " + dateFormat.format(new Date()) + "] - " + message + "\n");
    }
}

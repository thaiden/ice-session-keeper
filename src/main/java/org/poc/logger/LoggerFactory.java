package org.poc.logger;

public class LoggerFactory {
    private static Logger logger;
    public static void initLogger() {
        setLogger(new DefaultLogger());
    }

    public static void initLogger(Logger loggable) {
        setLogger(loggable);
    }

    private synchronized static void setLogger(Logger loggable) {
        logger = loggable;
    }

    public static Logger getLogger() {
        if (logger == null) {
            initLogger();
        }
        return logger;
    }


    public static Logger getLogger(Class value) {
        return getLogger();
    }
}

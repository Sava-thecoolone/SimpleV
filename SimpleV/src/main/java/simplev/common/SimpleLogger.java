package simplev.common;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("NonConstantLogger")
public class SimpleLogger {
    private final Logger LOGGER;

    public void log(Level l, String str) {
        LOGGER.log(l, str);
    }

    public void log(Level l, String str, Throwable e) {
        LOGGER.log(l, str, e);
    }

    public SimpleLogger(String logName) {
        LOGGER = Logger.getLogger(logName);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        handler.setLevel(Level.INFO);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.INFO);
        LOGGER.setUseParentHandlers(false);
    }
}
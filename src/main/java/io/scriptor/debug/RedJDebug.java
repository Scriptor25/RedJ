package io.scriptor.debug;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public class RedJDebug {

    private RedJDebug() {
    }

    private static final Logger logger = Logger.getLogger("io.scriptor.RedJ");

    static {
        logger.setUseParentHandlers(false);
        final var handler = new ConsoleHandler();
        handler.setFormatter(new RedJDebugFormatter());
        logger.addHandler(handler);
    }

    public static void info(String format, Object... args) {
        logger.info(format.formatted(args));
    }

    public static void warning(String format, Object... args) {
        logger.warning(format.formatted(args));
    }

    public static void severe(String format, Object... args) {
        logger.severe(format.formatted(args));
    }

    public static void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        logger.throwing(sourceClass, sourceMethod, thrown);
    }
}

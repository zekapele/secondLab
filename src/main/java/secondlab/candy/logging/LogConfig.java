package secondlab.candy.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LogConfig {
    private static final Logger LOG = Logger.getLogger(LogConfig.class.getName());

    private LogConfig() {
    }

    public static void init() {
        init(LogConfig.class.getClassLoader().getResourceAsStream("logging.properties"));
    }

    static void init(InputStream input) {
        if (input == null) {
            LOG.warning("logging.properties was not found");
            return;
        }
        try (input) {
            LogManager.getLogManager().readConfiguration(input);
            LOG.info("Logging configured");
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to configure logging", exception);
        }
    }
}

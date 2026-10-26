package secondlab.candy.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public final class StdOutHandler extends StreamHandler {
    public StdOutHandler() {
        super(System.out, new SimpleFormatter());
        setLevel(Level.FINE);
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public synchronized void close() {
        flush();
    }
}

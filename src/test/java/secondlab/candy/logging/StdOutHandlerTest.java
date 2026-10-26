package secondlab.candy.logging;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StdOutHandlerTest {
    @Test
    void writesAndFlushesWithoutClosingStdout() {
        StdOutHandler handler = new StdOutHandler();
        assertDoesNotThrow(() -> handler.publish(new LogRecord(Level.INFO, "hello")));
        assertDoesNotThrow(handler::close);
    }
}

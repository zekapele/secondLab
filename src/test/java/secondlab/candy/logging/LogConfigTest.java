package secondlab.candy.logging;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LogConfigTest {
    @Test
    void loadsTheClasspathConfiguration() {
        LogConfig.init();
    }

    @Test
    void warnsWhenTheConfigurationIsMissingAndFailsWhenItCannotBeRead() {
        LogConfig.init(null);
        InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };
        assertThrows(IllegalStateException.class, () -> LogConfig.init(broken));
        LogConfig.init(new ByteArrayInputStream("handlers=\n.level=INFO\n".getBytes(StandardCharsets.UTF_8)));
    }
}

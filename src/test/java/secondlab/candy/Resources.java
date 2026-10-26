package secondlab.candy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public final class Resources {
    private Resources() {
    }

    public static byte[] bytes(String classpath) {
        try (InputStream input = stream(classpath)) {
            return input.readAllBytes();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read " + classpath, exception);
        }
    }

    public static Path path(String classpath) {
        URL url = Objects.requireNonNull(Resources.class.getClassLoader().getResource(classpath), classpath);
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Cannot resolve " + classpath, exception);
        }
    }

    private static InputStream stream(String classpath) {
        InputStream input = Resources.class.getClassLoader().getResourceAsStream(classpath);
        if (input == null) {
            throw new IllegalStateException("Missing classpath resource " + classpath);
        }
        return input;
    }
}

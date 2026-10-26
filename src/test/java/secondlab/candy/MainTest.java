package secondlab.candy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    void runsWithExplicitFiles(@TempDir Path directory) throws Exception {
        Path output = directory.resolve("nested").resolve("candies-by-type.xml");
        Main.main(new String[]{
                Resources.path("xml/candies.xml").toString(),
                Resources.path("xsd/candies.xsd").toString(),
                Resources.path("xsl/candies-by-type.xsl").toString(),
                output.toString()
        });
        String text = Files.readString(output);
        assertTrue(text.contains("<caramel"));
        assertTrue(text.contains("<chocolate"));
        assertTrue(text.contains("<iris"));
    }

    @Test
    void runsWithClasspathDefaults() throws Exception {
        Path output = Path.of("output", "candies-by-type.xml");
        Main.main(new String[0]);
        assertTrue(Files.readString(output).contains("<caramel"));
    }

    @Test
    void rejectsWrongArgumentsAndMissingFiles() {
        assertThrows(IllegalArgumentException.class, () -> Main.main(new String[]{"only-one"}));
        assertThrows(IllegalStateException.class, () -> Main.main(new String[]{
                "missing.xml", "missing.xsd", "missing.xsl", "out.xml"
        }));
    }

}

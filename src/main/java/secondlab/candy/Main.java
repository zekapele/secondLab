package secondlab.candy;

import secondlab.candy.logging.LogConfig;
import secondlab.candy.model.Candy;
import secondlab.candy.parser.ParserKind;
import secondlab.candy.service.CandyService;
import secondlab.candy.sort.CandyEnergyComparator;
import secondlab.candy.sort.CandyNameComparator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final ParserKind DEFAULT_PARSER = ParserKind.SAX;

    private Main() {
    }

    public static void main(String[] args) {
        LogConfig.init();
        try {
            run(args);
        } catch (IOException exception) {
            LOG.log(Level.SEVERE, "Failed to process the candy catalog", exception);
            throw new IllegalStateException(exception);
        } catch (RuntimeException exception) {
            LOG.log(Level.SEVERE, "Failed to process the candy catalog", exception);
            throw exception;
        }
    }

    static void run(String[] args) throws IOException {
        Launch launch = Launch.from(args);
        CandyService service = new CandyService();
        List<Candy> candies = new ArrayList<>(service.load(launch.xml, launch.xsd, launch.parser));
        LOG.info(() -> "Collection built with " + launch.parser + " (" + candies.size() + " candies)");

        candies.sort(new CandyEnergyComparator());
        LOG.info(() -> launch.parser + " by energy: " + describe(candies));

        List<Candy> byName = new ArrayList<>(candies);
        byName.sort(new CandyNameComparator());
        LOG.info(() -> launch.parser + " by name: " + describe(byName));

        service.transform(launch.xml, launch.xsl, launch.output);
        String grouped = Files.readString(launch.output);
        LOG.info(() -> "XSL result (" + launch.output.toAbsolutePath() + "):" + System.lineSeparator() + grouped);
    }

    private static String describe(List<Candy> candies) {
        return candies.stream()
                .map(candy -> candy.name() + " (" + candy.energy() + " kcal)")
                .collect(Collectors.joining(", "));
    }

    private static Path resource(String classpath) {
        URL url = Main.class.getClassLoader().getResource(classpath);
        if (url == null) {
            throw new IllegalStateException("Missing classpath resource " + classpath);
        }
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Cannot resolve " + classpath, exception);
        }
    }

    private record Launch(ParserKind parser, Path xml, Path xsd, Path xsl, Path output) {
        static Launch from(String[] args) {
            return switch (args.length) {
                case 0 -> files(DEFAULT_PARSER);
                case 1 -> files(ParserKind.fromName(args[0]));
                case 4 -> new Launch(DEFAULT_PARSER, Path.of(args[0]), Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
                case 5 -> new Launch(
                        ParserKind.fromName(args[0]),
                        Path.of(args[1]),
                        Path.of(args[2]),
                        Path.of(args[3]),
                        Path.of(args[4])
                );
                default -> throw new IllegalArgumentException(
                        "Usage: Main [SAX|DOM|STAX] [<candies.xml> <candies.xsd> <candies-by-type.xsl> <output.xml>]");
            };
        }

        private static Launch files(ParserKind parser) {
            return new Launch(
                    parser,
                    resource("xml/candies.xml"),
                    resource("xsd/candies.xsd"),
                    resource("xsl/candies-by-type.xsl"),
                    Path.of("output", "candies-by-type.xml")
            );
        }
    }
}

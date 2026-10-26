package secondlab.candy;

import secondlab.candy.logging.LogConfig;
import secondlab.candy.model.Candy;
import secondlab.candy.parser.ParserKind;
import secondlab.candy.service.CandyService;
import secondlab.candy.sort.CandyEnergyComparator;
import secondlab.candy.sort.CandyNameComparator;
import secondlab.candy.validation.XmlValidator;

import java.io.ByteArrayInputStream;
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
        if (args.length != 0 && args.length != 4) {
            throw new IllegalArgumentException(
                    "Usage: Main [<candies.xml> <candies.xsd> <candies-by-type.xsl> <output.xml>]");
        }
        Path xml = args.length == 0 ? resource("xml/candies.xml") : Path.of(args[0]);
        Path xsd = args.length == 0 ? resource("xsd/candies.xsd") : Path.of(args[1]);
        Path xsl = args.length == 0 ? resource("xsl/candies-by-type.xsl") : Path.of(args[2]);
        Path output = args.length == 0 ? Path.of("output", "candies-by-type.xml") : Path.of(args[3]);

        byte[] xmlBytes = Files.readAllBytes(xml);
        byte[] xsdBytes = Files.readAllBytes(xsd);
        new XmlValidator().validate(xmlBytes, xsdBytes, xml.toUri().toString());

        CandyEnergyComparator byEnergy = new CandyEnergyComparator();
        for (ParserKind kind : ParserKind.values()) {
            List<Candy> candies = new ArrayList<>(kind.create().parse(new ByteArrayInputStream(xmlBytes)));
            candies.sort(byEnergy);
            LOG.info(() -> kind + " by energy: " + describe(candies));
            if (kind == ParserKind.DOM) {
                List<Candy> byName = new ArrayList<>(candies);
                byName.sort(new CandyNameComparator());
                LOG.info(() -> "DOM by name: " + describe(byName));
            }
        }
        new CandyService().transform(xml, xsl, output);
        String grouped = Files.readString(output);
        LOG.info(() -> "XSL result (" + output.toAbsolutePath() + "):" + System.lineSeparator() + grouped);
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
}

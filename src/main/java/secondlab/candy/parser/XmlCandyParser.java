package secondlab.candy.parser;

import secondlab.candy.model.Candy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class XmlCandyParser implements CandyParser {
    private static final Logger LOG = Logger.getLogger(XmlCandyParser.class.getName());

    private final ParserKind kind;

    public XmlCandyParser(ParserKind kind) {
        this.kind = kind;
    }

    @Override
    public List<Candy> parse(InputStream xml) {
        CandyXmlHandler handler = new CandyXmlHandler();
        switch (kind) {
            case SAX -> SaxEventSource.read(xml, handler);
            case DOM -> DomEventSource.read(xml, handler);
            case STAX -> StaxEventSource.read(xml, handler);
        }
        List<Candy> candies = new ArrayList<>(handler.result());
        LOG.fine(() -> kind + " parsed " + candies.size() + " candies");
        return candies;
    }
}

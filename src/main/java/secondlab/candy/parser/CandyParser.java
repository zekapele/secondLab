package secondlab.candy.parser;

import secondlab.candy.model.Candy;

import java.io.InputStream;
import java.util.List;

public interface CandyParser {
    List<Candy> parse(InputStream xml);
}

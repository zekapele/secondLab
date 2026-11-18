package secondlab.candy.parser;

public enum ParserKind {
    SAX,
    DOM,
    STAX;

    public CandyParser create() {
        return new XmlCandyParser(this);
    }

    public static ParserKind fromName(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Parser name is required: SAX, DOM or STAX");
        }
        try {
            return valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown parser: " + raw + ". Use SAX, DOM or STAX", exception);
        }
    }
}

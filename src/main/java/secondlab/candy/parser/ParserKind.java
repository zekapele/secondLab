package secondlab.candy.parser;

public enum ParserKind {
    SAX {
        @Override
        public CandyParser create() {
            return new SaxCandyParser();
        }
    },
    DOM {
        @Override
        public CandyParser create() {
            return new DomCandyParser();
        }
    },
    STAX {
        @Override
        public CandyParser create() {
            return new StaxCandyParser();
        }
    };

    public abstract CandyParser create();
}

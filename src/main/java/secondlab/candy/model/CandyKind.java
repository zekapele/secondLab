package secondlab.candy.model;

public enum CandyKind {
    CARAMEL("caramel"),
    IRIS("iris"),
    CHOCOLATE("chocolate");

    private final String xmlValue;

    CandyKind(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    public String xmlValue() {
        return xmlValue;
    }

    public static CandyKind fromXml(String value) {
        for (CandyKind kind : values()) {
            if (kind.xmlValue.equals(value)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Unknown candy kind: " + value);
    }
}

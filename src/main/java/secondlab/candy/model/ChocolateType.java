package secondlab.candy.model;

public enum ChocolateType {
    MILK("milk"),
    DARK("dark"),
    WHITE("white");

    private final String xmlValue;

    ChocolateType(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    public String xmlValue() {
        return xmlValue;
    }

    public static ChocolateType fromXml(String value) {
        for (ChocolateType type : values()) {
            if (type.xmlValue.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown chocolate type: " + value);
    }
}

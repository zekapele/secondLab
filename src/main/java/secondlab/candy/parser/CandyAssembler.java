package secondlab.candy.parser;

import secondlab.candy.error.CandyXmlException;
import secondlab.candy.model.Candy;
import secondlab.candy.model.CandyKind;
import secondlab.candy.model.CandyType;
import secondlab.candy.model.ChocolateType;
import secondlab.candy.model.Ingredients;
import secondlab.candy.model.NutritionalValue;

import java.math.BigDecimal;

final class CandyAssembler {
    private String id;
    private String name;
    private Integer energy;
    private CandyKind kind;
    private Boolean filling;
    private Integer water;
    private Integer sugar;
    private Integer fructose;
    private ChocolateType chocolateType;
    private Integer vanillin;
    private BigDecimal proteins;
    private BigDecimal fats;
    private BigDecimal carbohydrates;
    private String production;

    static boolean isLeaf(String localName) {
        return switch (localName) {
            case "name", "energy", "kind", "water", "sugar", "fructose", "chocolateType",
                 "vanillin", "proteins", "fats", "carbohydrates", "production" -> true;
            default -> false;
        };
    }

    void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    void setFilling(String raw) {
        if (raw == null || raw.isBlank()) {
            filling = null;
            return;
        }
        filling = switch (raw.trim()) {
            case "true", "1" -> true;
            case "false", "0" -> false;
            default -> throw new CandyXmlException("Invalid filling: " + raw);
        };
    }

    void setField(String field, String raw) {
        String value = raw == null ? "" : raw.trim();
        try {
            switch (field) {
                case "name" -> name = value;
                case "energy" -> energy = parseInt(field, value);
                case "kind" -> kind = value.isEmpty() ? null : CandyKind.fromXml(value);
                case "water" -> water = parseInt(field, value);
                case "sugar" -> sugar = parseInt(field, value);
                case "fructose" -> fructose = parseInt(field, value);
                case "chocolateType" -> chocolateType = value.isEmpty() ? null : ChocolateType.fromXml(value);
                case "vanillin" -> vanillin = parseInt(field, value);
                case "proteins" -> proteins = parseDecimal(field, value);
                case "fats" -> fats = parseDecimal(field, value);
                case "carbohydrates" -> carbohydrates = parseDecimal(field, value);
                case "production" -> production = value;
                default -> throw new CandyXmlException("Unexpected element: " + field);
            }
        } catch (IllegalArgumentException exception) {
            throw new CandyXmlException("Invalid value of " + field + ": " + value, exception);
        }
    }

    Candy build() {
        try {
            return new Candy(
                    required("id", id),
                    required("name", name),
                    requiredInt("energy", energy),
                    new CandyType(requiredKind(), requiredFilling()),
                    new Ingredients(
                            requiredInt("water", water),
                            requiredInt("sugar", sugar),
                            requiredInt("fructose", fructose),
                            chocolateType,
                            requiredInt("vanillin", vanillin)
                    ),
                    new NutritionalValue(
                            requiredDecimal("proteins", proteins),
                            requiredDecimal("fats", fats),
                            requiredDecimal("carbohydrates", carbohydrates)
                    ),
                    required("production", production)
            );
        } catch (IllegalArgumentException exception) {
            throw new CandyXmlException(exception.getMessage(), exception);
        }
    }

    private CandyKind requiredKind() {
        if (kind == null) {
            throw new CandyXmlException("Missing kind");
        }
        return kind;
    }

    private boolean requiredFilling() {
        if (filling == null) {
            throw new CandyXmlException("Missing filling");
        }
        return filling;
    }

    private static String required(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new CandyXmlException("Missing " + field);
        }
        return value;
    }

    private static int requiredInt(String field, Integer value) {
        if (value == null) {
            throw new CandyXmlException("Missing " + field);
        }
        return value;
    }

    private static BigDecimal requiredDecimal(String field, BigDecimal value) {
        if (value == null) {
            throw new CandyXmlException("Missing " + field);
        }
        return value;
    }

    private static int parseInt(String field, String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new CandyXmlException("Invalid integer for " + field + ": " + value, exception);
        }
    }

    private static BigDecimal parseDecimal(String field, String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new CandyXmlException("Invalid decimal for " + field + ": " + value, exception);
        }
    }
}

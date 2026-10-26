package secondlab.candy.model;

import java.util.Objects;

public record Candy(
        String id,
        String name,
        int energy,
        CandyType type,
        Ingredients ingredients,
        NutritionalValue value,
        String production
) {
    public Candy {
        requireLabel("id", id);
        if (id.chars().anyMatch(Character::isWhitespace) || Character.isDigit(id.charAt(0))) {
            throw new IllegalArgumentException("id must be an XML ID");
        }
        requireLabel("name", name);
        requireLabel("production", production);
        if (energy < 1 || energy > 1000) {
            throw new IllegalArgumentException("energy must be between 1 and 1000");
        }
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(ingredients, "ingredients");
        Objects.requireNonNull(value, "value");
    }

    private static void requireLabel(String field, String value) {
        if (value == null || value.isBlank() || value.length() > 100) {
            throw new IllegalArgumentException(field + " must contain 1..100 characters");
        }
    }
}

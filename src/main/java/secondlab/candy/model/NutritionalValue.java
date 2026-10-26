package secondlab.candy.model;

import java.math.BigDecimal;
import java.util.Objects;

public record NutritionalValue(BigDecimal proteins, BigDecimal fats, BigDecimal carbohydrates) {
    private static final BigDecimal MAX = new BigDecimal("100");

    public NutritionalValue {
        checkGrams("proteins", proteins);
        checkGrams("fats", fats);
        checkGrams("carbohydrates", carbohydrates);
    }

    private static void checkGrams(String field, BigDecimal value) {
        Objects.requireNonNull(value, field);
        if (value.scale() > 2) {
            throw new IllegalArgumentException(field + " must have at most 2 fraction digits");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException(field + " must be between 0 and 100");
        }
    }
}

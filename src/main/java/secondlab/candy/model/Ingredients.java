package secondlab.candy.model;

public record Ingredients(
        int water,
        int sugar,
        int fructose,
        ChocolateType chocolateType,
        int vanillin
) {
    public Ingredients {
        checkRange("water", water, 0, 100_000);
        checkRange("sugar", sugar, 0, 100_000);
        checkRange("fructose", fructose, 0, 100_000);
        checkRange("vanillin", vanillin, 0, 1_000);
    }

    private static void checkRange(String field, int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
        }
    }
}

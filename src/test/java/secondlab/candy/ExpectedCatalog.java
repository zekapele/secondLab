package secondlab.candy;

import secondlab.candy.model.Candy;
import secondlab.candy.model.CandyKind;
import secondlab.candy.model.CandyType;
import secondlab.candy.model.ChocolateType;
import secondlab.candy.model.Ingredients;
import secondlab.candy.model.NutritionalValue;

import java.math.BigDecimal;
import java.util.List;

public final class ExpectedCatalog {
    private ExpectedCatalog() {
    }

    public static List<Candy> candies() {
        return List.of(
                candy("vk", "Вечірній Київ", 452, CandyKind.CHOCOLATE, true,
                        15000, 42000, 8000, ChocolateType.MILK, 20, "5.40", "26.20", "57.80", "Roshen"),
                candy("romashka", "Ромашка", 378, CandyKind.CARAMEL, false,
                        8000, 61000, 12000, null, 5, "0.80", "0.40", "92.50", "AVK"),
                candy("bilochka", "Білочка", 415, CandyKind.IRIS, false,
                        12000, 48000, 6000, null, 8, "3.10", "12.40", "71.20", "Roshen"),
                candy("strila", "Стріла", 498, CandyKind.CHOCOLATE, false,
                        10000, 38000, 3000, ChocolateType.DARK, 10, "6.80", "32.50", "52.10", "Konti"),
                candy("duchess", "Дюшес", 365, CandyKind.CARAMEL, false,
                        9000, 58000, 15000, null, 4, "0.50", "0.20", "94.10", "AVK"),
                candy("korivka", "Корівка", 440, CandyKind.IRIS, false,
                        14000, 45000, 7000, null, 15, "4.20", "16.80", "68.40", "Житомирські ласощі"),
                candy("ptashyne", "Пташине молоко", 470, CandyKind.CHOCOLATE, true,
                        18000, 40000, 9000, ChocolateType.WHITE, 18, "4.90", "22.30", "61.70", "AVK")
        );
    }

    private static Candy candy(
            String id,
            String name,
            int energy,
            CandyKind kind,
            boolean filling,
            int water,
            int sugar,
            int fructose,
            ChocolateType chocolateType,
            int vanillin,
            String proteins,
            String fats,
            String carbohydrates,
            String production
    ) {
        return new Candy(
                id,
                name,
                energy,
                new CandyType(kind, filling),
                new Ingredients(water, sugar, fructose, chocolateType, vanillin),
                new NutritionalValue(new BigDecimal(proteins), new BigDecimal(fats), new BigDecimal(carbohydrates)),
                production
        );
    }
}

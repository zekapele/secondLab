package secondlab.candy.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelConstraintsTest {
    @Test
    void readsEnumXmlValues() {
        assertEquals("caramel", CandyKind.CARAMEL.xmlValue());
        assertEquals("iris", CandyKind.IRIS.xmlValue());
        assertEquals("chocolate", CandyKind.CHOCOLATE.xmlValue());
        assertEquals(CandyKind.CARAMEL, CandyKind.fromXml("caramel"));
        assertEquals(CandyKind.IRIS, CandyKind.fromXml("iris"));
        assertEquals(CandyKind.CHOCOLATE, CandyKind.fromXml("chocolate"));
        assertEquals("milk", ChocolateType.MILK.xmlValue());
        assertEquals(ChocolateType.DARK, ChocolateType.fromXml("dark"));
        assertEquals(ChocolateType.WHITE, ChocolateType.fromXml("white"));
        assertThrows(IllegalArgumentException.class, () -> CandyKind.fromXml("gummy"));
        assertThrows(IllegalArgumentException.class, () -> ChocolateType.fromXml("ruby"));
    }

    @Test
    void rejectsValuesOutsideTheSchemaBounds() {
        assertThrows(IllegalArgumentException.class, () -> sample().withEnergy(0));
        assertThrows(IllegalArgumentException.class, () -> sample().withEnergy(1001));
        assertThrows(IllegalArgumentException.class, () -> new Candy(
                "has space", "Name", 10, new CandyType(CandyKind.CARAMEL, false),
                new Ingredients(0, 0, 0, null, 0),
                new NutritionalValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                "Plant"));
        assertThrows(IllegalArgumentException.class, () -> new Candy(
                "ok", "Name", 10, new CandyType(CandyKind.CARAMEL, false),
                new Ingredients(0, 0, 0, null, 0),
                new NutritionalValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                " "));
        assertThrows(IllegalArgumentException.class, () -> new Candy(
                "ok", "n".repeat(101), 10, new CandyType(CandyKind.CARAMEL, false),
                new Ingredients(0, 0, 0, null, 0),
                new NutritionalValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                "Plant"));
        assertThrows(IllegalArgumentException.class, () -> new Candy(
                "1bad", "Name", 10, new CandyType(CandyKind.CARAMEL, false),
                new Ingredients(0, 0, 0, null, 0),
                new NutritionalValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                "Plant"));
        assertThrows(IllegalArgumentException.class, () -> new Candy(
                "ok", " ", 10, new CandyType(CandyKind.CARAMEL, false),
                new Ingredients(0, 0, 0, null, 0),
                new NutritionalValue(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                "Plant"));
        assertThrows(NullPointerException.class, () -> new CandyType(null, false));
        assertThrows(IllegalArgumentException.class, () -> new Ingredients(-1, 0, 0, null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Ingredients(0, 100_001, 0, null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Ingredients(0, 0, -1, null, 0));
        assertThrows(IllegalArgumentException.class, () -> new Ingredients(0, 0, 0, null, 1001));
        assertThrows(NullPointerException.class, () -> new NutritionalValue(null, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionalValue(new BigDecimal("1.234"), BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionalValue(new BigDecimal("101"), BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionalValue(new BigDecimal("-0.01"), BigDecimal.ZERO, BigDecimal.ZERO));
    }

    private static CandyDraft sample() {
        return new CandyDraft();
    }

    private static final class CandyDraft {
        private Candy withEnergy(int energy) {
            return new Candy(
                    "ok",
                    "Name",
                    energy,
                    new CandyType(CandyKind.IRIS, false),
                    new Ingredients(0, 0, 0, null, 0),
                    new NutritionalValue(new BigDecimal("100"), new BigDecimal("0.00"), new BigDecimal("1.5")),
                    "Plant"
            );
        }
    }
}

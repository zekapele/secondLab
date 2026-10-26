package secondlab.candy.sort;

import secondlab.candy.ExpectedCatalog;
import secondlab.candy.model.Candy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CandyComparatorTest {
    @Test
    void sortsByEnergy() {
        List<Candy> candies = new ArrayList<>(ExpectedCatalog.candies());
        candies.sort(new CandyEnergyComparator());
        assertEquals(List.of("Дюшес", "Ромашка", "Білочка", "Корівка", "Вечірній Київ", "Пташине молоко", "Стріла"),
                candies.stream().map(Candy::name).toList());
    }

    @Test
    void sortsByUkrainianName() {
        List<Candy> candies = new ArrayList<>(ExpectedCatalog.candies());
        candies.sort(new CandyNameComparator());
        assertEquals(List.of("Білочка", "Вечірній Київ", "Дюшес", "Корівка", "Пташине молоко", "Ромашка", "Стріла"),
                candies.stream().map(Candy::name).toList());
    }

    @Test
    void sortsByProductionThenName() {
        CandyProductionComparator comparator = new CandyProductionComparator();
        List<Candy> candies = new ArrayList<>(ExpectedCatalog.candies());
        candies.sort(comparator);
        for (int index = 1; index < candies.size(); index++) {
            assertTrue(comparator.compare(candies.get(index - 1), candies.get(index)) <= 0);
        }
        List<String> roshen = candies.stream()
                .filter(candy -> candy.production().equals("Roshen"))
                .map(Candy::name)
                .toList();
        assertEquals(List.of("Білочка", "Вечірній Київ"), roshen);
    }

    @Test
    void treatsTheSameCandyAsEqual() {
        Candy candy = ExpectedCatalog.candies().getFirst();
        assertEquals(0, new CandyNameComparator().compare(candy, candy));
        assertEquals(0, new CandyEnergyComparator().compare(candy, candy));
        assertEquals(0, new CandyProductionComparator().compare(candy, candy));
        Candy other = ExpectedCatalog.candies().get(1);
        assertTrue(new CandyEnergyComparator().compare(candy, other) > 0);
        assertTrue(new CandyEnergyComparator().compare(other, candy) < 0);
    }
}

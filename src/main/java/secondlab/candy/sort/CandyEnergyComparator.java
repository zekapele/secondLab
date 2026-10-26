package secondlab.candy.sort;

import secondlab.candy.model.Candy;

import java.util.Comparator;

public final class CandyEnergyComparator implements Comparator<Candy> {
    @Override
    public int compare(Candy left, Candy right) {
        return Integer.compare(left.energy(), right.energy());
    }
}

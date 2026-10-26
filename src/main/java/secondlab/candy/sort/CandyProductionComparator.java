package secondlab.candy.sort;

import secondlab.candy.model.Candy;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public final class CandyProductionComparator implements Comparator<Candy> {
    private final Collator collator = Collator.getInstance(Locale.of("uk", "UA"));
    private final CandyNameComparator names = new CandyNameComparator();

    @Override
    public int compare(Candy left, Candy right) {
        int byProduction = collator.compare(left.production(), right.production());
        if (byProduction != 0) {
            return byProduction;
        }
        return names.compare(left, right);
    }
}

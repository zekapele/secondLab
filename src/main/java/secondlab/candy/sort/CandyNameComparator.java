package secondlab.candy.sort;

import secondlab.candy.model.Candy;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public final class CandyNameComparator implements Comparator<Candy> {
    private final Collator collator = Collator.getInstance(Locale.of("uk", "UA"));

    @Override
    public int compare(Candy left, Candy right) {
        return collator.compare(left.name(), right.name());
    }
}

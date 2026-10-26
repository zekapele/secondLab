package secondlab.candy.model;

import java.util.Objects;

public record CandyType(CandyKind kind, boolean filling) {
    public CandyType {
        Objects.requireNonNull(kind, "kind");
    }
}

package engine;

import range.Boundaries;

public class BoundariesDTO {
    private final String  from;
    private final String to;

    public BoundariesDTO(Boundaries boundaries) {
        this.from = boundaries.getFrom().toString();
        this.to = boundaries.getTo().toString();
    }
    public String  getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
}

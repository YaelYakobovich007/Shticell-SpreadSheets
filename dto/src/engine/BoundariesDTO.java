package engine;

import sheet.coordinate.Coordinate;

public class BoundariesDTO {
    private final Coordinate from;
    private final Coordinate to;

    public BoundariesDTO(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }
    public Coordinate getFrom() {
        return from;
    }
    public Coordinate getTo() {
        return to;
    }
}

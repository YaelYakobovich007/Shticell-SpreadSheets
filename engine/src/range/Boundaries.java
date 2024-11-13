package range;

import sheet.coordinate.Coordinate;
import java.io.Serializable;

public class Boundaries implements Serializable {
    private Coordinate to;
    private Coordinate from;

    public Boundaries(Coordinate from, Coordinate to) {
        this.from = from;
        this.to = to;
    }

    public Coordinate getTo(){
        return to;
    }

    public Coordinate getFrom(){
        return from;
    }
}

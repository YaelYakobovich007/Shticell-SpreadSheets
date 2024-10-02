package engine;

import range.Boundaries;
import range.Range;
import sheet.coordinate.Coordinate;

import java.util.HashSet;
import java.util.Set;

public class RangeDTO {

    public final Boundaries boundaries;
    private final String name;
    private final Set<Coordinate> coordinateInRange;

    public RangeDTO(Range range) {
        boundaries= range.getBoundaries();
        name = range.getName();
        coordinateInRange= range.getCoordinateInRange();

    }
    public String getName() {
        return name;
    }
    public Boundaries getBoundaries() {
        return boundaries;
    }
    public Set<Coordinate> getCoordinateInRange() {
        return coordinateInRange;
    }


}

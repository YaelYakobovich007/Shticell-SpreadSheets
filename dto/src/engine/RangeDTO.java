package engine;

import range.Range;
import java.util.HashSet;
import java.util.Set;

public class RangeDTO {
    public final BoundariesDTO boundaries;
    private final String name;
    private final Set<String> coordinateInRange;

    public RangeDTO(Range range) {
        boundaries = new BoundariesDTO(range.getBoundaries());
        name = range.getName();
        coordinateInRange = new HashSet<>();
        range.getCoordinateInRange().forEach(coordinate -> coordinateInRange.add(coordinate.toString()));
    }

    public String getName() {
        return name;
    }

    public BoundariesDTO getBoundaries() {
        return boundaries;
    }

    public Set<String> getCoordinateInRange() {
        return coordinateInRange;
    }
}

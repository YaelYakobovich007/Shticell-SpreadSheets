package range;

import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class Range implements Serializable {

    private  String name;
    private Boundaries boundaries;
    private Set<Coordinate> coordinateInRange;

    public Range(String name, Boundaries boundaries) {
        this.name = name;
        this.boundaries = boundaries;
        this.coordinateInRange = new HashSet<>();
        populateCoordinateInRange();
    }

    private void populateCoordinateInRange() {
        int fromRow = boundaries.getFrom().getRow();
        int toRow = boundaries.getTo().getRow();
        int fromCol = boundaries.getFrom().getColumn();
        int toCol = boundaries.getTo().getColumn();

        for (int row = fromRow; row <= toRow; row++) {
            for (int col = fromCol; col <= toCol; col++) {
                 coordinateInRange.add(CoordinateFactory.createCoordinate(row, col));
            }
        }
    }

    public Set<Coordinate> getCoordinateInRange() {
        return coordinateInRange;
    }

    public String getName() {
        return name;
    }
    public Boundaries getBoundaries() {
        return boundaries;
    }
}

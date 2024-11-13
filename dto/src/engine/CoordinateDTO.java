package engine;

import sheet.coordinate.Coordinate;
import java.util.Objects;

public class CoordinateDTO {
    private final int row;
    private final int column;

    public CoordinateDTO(Coordinate coordinate) {
        this.row = coordinate.getRow();
        this.column = coordinate.getColumn();
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateDTO that = (CoordinateDTO) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    public int getColumn() {
        return column;
    }


}

package ui;

public class UniqueValuesForColumnRequestDTO {
    private Character targetColumn;
    private String fromCoordinate;
    private String toCoordinate;

    public UniqueValuesForColumnRequestDTO(Character targetColumn, String fromCoordinate, String toCoordinate) {
        this.targetColumn = targetColumn;
        this.fromCoordinate = fromCoordinate;
        this.toCoordinate = toCoordinate;
    }
    public Character getTargetColumn() {
        return targetColumn;
    }

    public String getFromCoordinate() {
        return fromCoordinate;
    }

    public String getToCoordinate() {
        return toCoordinate;
    }
}

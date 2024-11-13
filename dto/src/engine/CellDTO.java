package engine;

import sheet.cell.api.Cell;
import java.util.*;

public class CellDTO {
    public final CoordinateDTO coordinate;
    private String originalValue;
    private String effectiveValue;
    private int lastModifiedVersion;
    private String updatedByUser;
    private Set<String> dependsOn;
    private Set<String> influencingOn;

    public static CellDTO createCellDTO(Cell cell){
        if (cell == null)
            return null;
        return new CellDTO(cell);
    }
    private CellDTO(Cell cell) {
        this.coordinate =new CoordinateDTO(cell.getCoordinate());
        this.originalValue= cell.getOriginalValue();
        this.updatedByUser= cell.getUpdatedByUser();
        this.effectiveValue =FormattedValuePrinter.formatValue(cell.getEffectiveValue());
        this.lastModifiedVersion= cell.getVersion();
        this.dependsOn =extractCoordinates(cell.getDependsOn());
        this.influencingOn = extractCoordinates(cell.getInfluencingOn());
    }

    public CoordinateDTO getCoordinate() {
        return coordinate;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    public Set<String> getDependentSources() {
        return dependsOn;
    }

    public Set<String> getInfluencedCells() {
        return influencingOn;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    private Set<String> extractCoordinates(List<Cell> cells) {
        Set<String> coordinates = new HashSet<>();
        for (Cell cell : cells) {
            coordinates.add(cell.getCoordinate().toString());
        }
        return coordinates;
    }



}
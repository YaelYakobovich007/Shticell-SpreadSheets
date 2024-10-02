package engine;

import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellDTO {
    public final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;
    private List<CellDTO> dependsOn;
    private List<CellDTO> influencingOn;

     public static CellDTO createCellDTO(Cell cell){
         if (cell == null)
             return null;
         return new CellDTO(cell,new HashSet<>());
     }
    private CellDTO(Cell cell, Set<Coordinate>  visitedCoordinates) {
        this.coordinate = cell.getCoordinate();
        this.originalValue= cell.getOriginalValue();
        this.effectiveValue =cell.getEffectiveValue();
        this.lastModifiedVersion= cell.getVersion();

        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
        populateDependencies(cell, visitedCoordinates);
    }

    private void populateDependencies(Cell cell, Set<Coordinate> visitedCoordinates) {
        if (visitedCoordinates.contains(this.coordinate)) {
            return;
        }

        visitedCoordinates.add(this.coordinate);

        for (Cell dependCell : cell.getDependsOn()) {
            if (!this.coordinate.equals(dependCell.getCoordinate())) {
                CellDTO dependDTO = new CellDTO(dependCell,visitedCoordinates);
                dependDTO.populateDependencies(dependCell, visitedCoordinates);
                this.dependsOn.add(dependDTO);
            }
        }

        for (Cell influenceCell : cell.getInfluencingOn()) {
            if (!this.coordinate.equals(influenceCell.getCoordinate())) {
                CellDTO influenceDTO = new CellDTO(influenceCell,visitedCoordinates);
                influenceDTO.populateDependencies(influenceCell, visitedCoordinates);
                this.influencingOn.add(influenceDTO);
            }
        }
        visitedCoordinates.remove(this.coordinate);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    public List<CellDTO> getDependentSources() {
        return dependsOn;
    }

    public List<CellDTO> getInfluencedCells() {
        return influencingOn;
    }
}

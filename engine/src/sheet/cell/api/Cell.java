package sheet.cell.api;

import javafx.scene.paint.Color;
import sheet.coordinate.Coordinate;
import java.io.Serializable;
import java.util.List;

public interface Cell extends Serializable
{
    Coordinate getCoordinate();
    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    Boolean calculateEffectiveValue();
    int getVersion();
    List<Cell> getDependsOn();
    List<Cell> getInfluencingOn();
    void addDependency(Cell cell);
    void addInfluencingOn(Cell cell);
    void updateVersion(int newVersion);
    void setCoordinate(Coordinate coordinate);

}

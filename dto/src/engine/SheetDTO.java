package engine;

import javafx.scene.paint.Color;
import sheet.api.Layout;
import sheet.api.Sheet;
import sheet.coordinate.Coordinate;
import utils.ColorUtil;
import java.util.HashMap;
import java.util.Map;

public class SheetDTO {
    private final String  name;
    private final int versionNumber;
    private final Layout layout;
    private final Map<Coordinate, CellDTO> activeCells;
    private final Map<Coordinate,String> backgroundColors;
    private final Map<Coordinate, String> textColors;

    public SheetDTO(Sheet sheet) {
        this.name = sheet.getName();
        this.versionNumber = sheet.getVersionNumber();
        this.layout= sheet.getLayout();
        this.activeCells= new HashMap<>();
        sheet.getActiveCellMap().forEach((key, value) -> activeCells.put(key,  CellDTO.createCellDTO(value)));
        this.backgroundColors = sheet.getBackgroundColors();
        this.textColors = sheet.getTextColors();
    }

    public String getSheetName() {
        return name;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public Layout getLayout() {
        return layout;
    }

    public Map<Coordinate, CellDTO> getActiveCells() {
        return activeCells;
    }

    public Map<Coordinate, Color> getBackgroundColorsAsColors() {
        Map<Coordinate, Color> colors = new HashMap<>();
        backgroundColors.forEach((coordinate, colorString) -> colors.put(coordinate, ColorUtil.hexToColor(colorString)));
        return colors;
    }

    public Map<Coordinate, Color> getTextColorsAsColors() {
        Map<Coordinate, Color> colors = new HashMap<>();
        textColors.forEach((coordinate, colorString) -> colors.put(coordinate, ColorUtil.hexToColor(colorString)));
        return colors;
    }
}

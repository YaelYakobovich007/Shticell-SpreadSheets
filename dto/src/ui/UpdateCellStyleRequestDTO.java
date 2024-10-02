package ui;
import javafx.scene.paint.Color;


public class UpdateCellStyleRequestDTO {
    String cellId;
    Color newColor;

    public UpdateCellStyleRequestDTO(String cellId, Color newColor) {
        this.cellId = cellId;
        this.newColor = newColor;
    }
    public String getCellId() {
        return cellId;
    }
    public Color getNewColor() {
        return newColor;
    }
}

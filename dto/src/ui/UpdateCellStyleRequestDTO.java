package ui;

public class UpdateCellStyleRequestDTO {
    String cellId;
    String newColor;

    public UpdateCellStyleRequestDTO(String cellId, String newColor) {
        this.cellId = cellId;
        this.newColor = newColor;
    }
    public String getCellId() {
        return cellId;
    }
    public String getNewColor() {
        return newColor;
    }
}

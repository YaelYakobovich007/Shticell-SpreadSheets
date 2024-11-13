package ui;

public class CellUpdateRequestDTO {

    private final String cellId;
    private final String newValue;

    public CellUpdateRequestDTO(String cellId, String newValue) {
        this.cellId = cellId;
        this.newValue = newValue;
    }

    public String getCellId() {
        return cellId;
    }

    public String getNewValue() {
        return newValue;
    }



}

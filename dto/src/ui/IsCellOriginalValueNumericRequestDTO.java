package ui;

public class IsCellOriginalValueNumericRequestDTO {
    private  String cellID;

    public IsCellOriginalValueNumericRequestDTO(String cellID) {
        this.cellID = cellID;
    }
    public String getCellID() {
        return cellID;
    }
}

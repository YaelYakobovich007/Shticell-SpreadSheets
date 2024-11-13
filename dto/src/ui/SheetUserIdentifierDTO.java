package ui;

public class SheetUserIdentifierDTO {
    private  final String sheetName;
    private  final int sheetVersion;
    private  String userName;

    public SheetUserIdentifierDTO(String sheetName, int sheetVersion, String userName) {
        this.sheetName = sheetName;
        this.sheetVersion = sheetVersion;
        this.userName = userName;
    }

    public SheetUserIdentifierDTO(String sheetName, int sheetVersion) {
        this.sheetName = sheetName;
        this.sheetVersion = sheetVersion;
    }

    public String getSheetName() {
        return sheetName;
    }
    public int getSheetVersion() {
        return sheetVersion;
    }
    public String getUserName() {
        return userName;
    }

}

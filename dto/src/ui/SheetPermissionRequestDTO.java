package ui;

public class SheetPermissionRequestDTO {
    private String username;
    private String sheetName;
    private String permissionType;

    public SheetPermissionRequestDTO(String userName,String sheetName, String permissionType) {
        this.username = userName;
        this.sheetName = sheetName;
        this.permissionType = permissionType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }
}

package engine;

public class SheetDetailsDTO {
    private final String uploadedByUser;
    private final String sheetName;
    private final String sheetSize;
    private final String userPermission;

    public SheetDetailsDTO(String uploadedByUser, String sheetName, String sheetSize, String userPermission) {
        this.uploadedByUser = uploadedByUser;
        this.sheetName = sheetName;
        this.sheetSize = sheetSize;
        this.userPermission = userPermission;
    }

    public String getUploadedByUser() {
        return uploadedByUser;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getSheetSize() {
        return sheetSize;
    }

    public String getUserPermission() {
        return userPermission;
    }

    @Override
    public String toString() {
        return "SheetDetailsDTO{" +
                "uploadedByUser='" + uploadedByUser + '\'' +
                ", sheetName='" + sheetName + '\'' +
                ", sheetSize='" + sheetSize + '\'' +
                ", userPermission='" + userPermission + '\'' +
                '}';
    }
}

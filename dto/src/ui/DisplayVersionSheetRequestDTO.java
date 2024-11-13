package ui;

public class DisplayVersionSheetRequestDTO {
    private   int versionNumber;

    public DisplayVersionSheetRequestDTO(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public int getVersionNumber() {
        return versionNumber;
    }
}

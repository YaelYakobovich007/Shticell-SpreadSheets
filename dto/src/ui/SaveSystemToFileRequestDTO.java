package ui;

public class SaveSystemToFileRequestDTO{
    private final String fileName;

    public SaveSystemToFileRequestDTO(String filePath) {
        this.fileName = filePath;
    }

    public String getFilePath() {
        return fileName;
    }
}

package ui;

public class LoadRequestDTO {
    private final String filePath;

    public LoadRequestDTO(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}

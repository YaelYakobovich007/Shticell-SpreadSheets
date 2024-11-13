package ui;

import java.io.InputStream;

public class LoadRequestDTO {
    private final InputStream file;
    private final String userName;

    public LoadRequestDTO(InputStream file, String userName) {
        this.file = file;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public InputStream getFile() {
        return file;
    }
}

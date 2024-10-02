package components.app;

import api.Engine;
import engine.SheetDTO;
import javafx.concurrent.Task;

public class FileLoadingTask extends Task<SheetDTO> {
    private final Engine engine;
    private final String filePath;

    public FileLoadingTask(Engine engine, String filePath) {
        this.engine = engine;
        this.filePath = filePath;
    }


    @Override
    protected SheetDTO call() throws Exception {
        try {
            updateMessage("Loading file...");
            updateProgress(0, 100);

            Thread.sleep(1000);
            updateProgress(25, 100);

            System.out.println("Loading sheet from file: " + filePath);
            engine.loadSheetFromFile(new ui.LoadRequestDTO(filePath));

            updateProgress(50, 100);
            Thread.sleep(500);

            SheetDTO sheetDTO = engine.getCurrentSheet();

            if (sheetDTO == null) {
                throw new RuntimeException("Failed to load sheet: SheetDTO is null");
            }

            updateMessage("Sheet loaded successfully.");
            updateProgress(100, 100);

            return sheetDTO;

        } catch (Exception e) {
//            System.err.println("Error loading file: " + e.getMessage());
//            updateMessage("Error loading sheet."+ e.getMessage());
//            throw new RuntimeException("Failed to load sheet", e);

            String errorMessage = "Error loading sheet: " + e.getMessage();
            updateMessage(errorMessage);
            // החזר את ההודעה יחד עם השגיאה
            throw new RuntimeException(errorMessage, e);
        }
    }
}

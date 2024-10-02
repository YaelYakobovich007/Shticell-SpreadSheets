package components.app;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WindowManager {
    public static Stage showProgressWindow(Window owner, Task<?> task) {
        Stage progressStage = new Stage();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.progressProperty().bind(task.progressProperty());

        VBox progressBox = new VBox(10, new Label("Loading file..."), progressBar);
        progressBox.setAlignment(Pos.CENTER);

        Scene progressScene = new Scene(progressBox, 500, 150);
        progressStage.setScene(progressScene);
        progressStage.setTitle("File Loading");
        progressStage.initOwner(owner);

        progressStage.show();
        return progressStage;
    }

    public static void showErrorWindow(Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR, throwable.getMessage(), ButtonType.OK);
        alert.setTitle("File Loading Error");
        alert.setHeaderText("Error Loading File");
        alert.showAndWait();
    }
}

package components.updateCell;

import AnimationUtil.PopUpWindowAnimator;
import components.viewSheetMain.ViewSheetMainController;
import engine.SheetDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import java.io.IOException;

public class UpdateCellController {
    @FXML private TextField cellValueField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private ScrollPane mainContinerScrollPane;
    private ViewSheetMainController mainController;
    private Stage stage;

    @FXML
    private void initialize() {
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(ViewSheetMainController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popUpWindowDesign/popupWindow_" +newStyle+".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    @FXML void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) cellValueField.getScene().getWindow();
        PopUpWindowAnimator.applySlideOut(stage);
    }


    @FXML
    void handleUpdateButton(ActionEvent event) {
        String newValue = cellValueField.getText();
        successLabel.setVisible(false);
        errorLabel.setVisible(false);

        mainController.updateCell(newValue, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    PopupManager.showErrorPopup("Failed to update cell: " + e.getMessage());
                    cellValueField.clear();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        mainController.updateAllCellContent(sheetDTO);
                        mainController.updateHeaderInfo();
                        PopupManager.showSuccessPopup("Cell updated successfully!");
                        stage.close();
                    });
                } else {
                    String errorMessage = responseBody.isEmpty() ? "Unknown error" : responseBody;
                    Platform.runLater(() -> {
                        PopupManager.showErrorPopup("Failed to update cell: " + errorMessage);
                        cellValueField.clear();
                    });
                }
            }
        });
    }

    public void setMainController(ViewSheetMainController viewSheetMainController) {
        this.mainController = viewSheetMainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

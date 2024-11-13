package components.deleteRangePopUp;

import AnimationUtil.PopUpWindowAnimator;
import components.viewSheetMain.ViewSheetMainController;
import engine.SheetDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class DeleteRangePopUpController {

    @FXML private TextField rangeNameTextField;
    @FXML private ScrollPane mainContienrScrollPane;
    private ViewSheetMainController mainController;
    private Stage stage;

    @FXML
    private void initialize() {
        setupThemeListener();
        applyCurrentTheme(ViewSheetMainController.themeProperty().get());
    }

    private void setupThemeListener() {
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> applyCurrentTheme(newTheme));
    }

    private void applyCurrentTheme(String newStyle) {
        String styleSheet = getClass().getResource("/util/popUpWindowDesign/popupWindow_" +newStyle+".css").toExternalForm();
        mainContienrScrollPane.getStylesheets().clear();
        mainContienrScrollPane.getStylesheets().add(styleSheet);
    }

    @FXML
    void handleCancelButtonAction(ActionEvent event) {
        PopUpWindowAnimator.applySlideOut(stage);
    }

    @FXML
    private void handleOKButtonAction(ActionEvent event) {
        if (!rangeNameTextField.getText().isEmpty()) {
            deleteRange();
        } else {
            PopupManager.showErrorPopup("Range name is required.");
        }
    }

    private void deleteRange() {
        String rangeName = rangeNameTextField.getText();
        mainController.deleteRange(rangeName, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to delete range: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    SheetDTO updatedSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        mainController.updateContentAfterDeleteRange(updatedSheet, rangeName);
                        PopupManager.showSuccessPopup("Range deleted successfully.");
                        stage.close();
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to delete range: " + errorMessage));
                }
            }
        });
    }

    public void setMainController(ViewSheetMainController mainController) {
        this.mainController = mainController;}

    public void setStage(Stage stage) {
        this.stage = stage;}
}

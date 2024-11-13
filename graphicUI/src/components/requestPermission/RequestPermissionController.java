package components.requestPermission;

import components.appMain.AppMainController;
import components.viewSheetMain.ViewSheetMainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import util.http.HttpClientUtil;

import java.io.IOException;

public class RequestPermissionController {
    @FXML private Label sheetNameLabel;
    @FXML private ComboBox<String> permissionLevelComboBox;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    @FXML private ScrollPane mainContainerScrollPane;

    private AppMainController mainController;
    private Stage stage;
    private String selectedSheetName;

    @FXML
    private void initialize() {
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(ViewSheetMainController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popUpWindowDesign/popupWindow_" +newStyle+".css").toExternalForm();
        mainContainerScrollPane.getStylesheets().clear();
        mainContainerScrollPane.getStylesheets().add(styleSheet);
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSelectedSheetName(String sheetName) {
        this.selectedSheetName = sheetName;
        sheetNameLabel.setText("Request permission for: " + sheetName);
    }

    @FXML
    private void handleOkAction() {
        String selectedPermission = permissionLevelComboBox.getValue();
        if (selectedPermission == null) {
            PopupManager.showErrorPopup("Please select a permission level.");
            return;
        }
        String requestUrl = Constants.PERMISSIONS_OPERATION_URL + "?sheet_name=" + selectedSheetName + "&permission_type=" + selectedPermission;
        HttpClientUtil.runAsyncPost(requestUrl, "", new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Request failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> PopupManager.showSuccessPopup("Permission request submitted successfully"));
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Request failed: " + errorMessage));
                    }
                }
            }
        });
        stage.close();
    }

    @FXML
    private void handleCancelAction() {
        stage.close();
    }
}
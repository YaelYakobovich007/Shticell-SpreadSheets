package components.NewRangePopup;

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

public class NewRangePopupController {

    @FXML private TextField fromBoundariesTextField;
    @FXML private TextField toBoundariesTextField;
    @FXML private TextField rangeNameTextField;
    @FXML private ScrollPane mainContinerScrollPane;

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
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    @FXML void handleCancelButtonAction(ActionEvent event) {
        PopUpWindowAnimator.applySlideOut(stage);
    }

    @FXML
    private void handleOKButtonAction(ActionEvent event) {
        if (areFieldsValid()) {
            createNewRange();
        } else {
            PopupManager.showErrorPopup("All fields are required.");
        }
    }

    private boolean areFieldsValid() {
        return !fromBoundariesTextField.getText().isEmpty()
                && !toBoundariesTextField.getText().isEmpty()
                && !rangeNameTextField.getText().isEmpty();
    }

    private void createNewRange() {
        String fromBoundaries = fromBoundariesTextField.getText();
        String toBoundaries = toBoundariesTextField.getText();
        String rangeName = rangeNameTextField.getText();

        mainController.createNewRange(rangeName, fromBoundaries, toBoundaries, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    PopupManager.showErrorPopup("Failed to create range: " + e.getMessage());
                    fromBoundariesTextField.clear();
                    toBoundariesTextField.clear();
                    rangeNameTextField.clear();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        mainController.updateContentAfterCreateNewRange(sheetDTO,rangeName);

                        PopupManager.showSuccessPopup("Range created successfully.");
                        stage.close();
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> {
                        PopupManager.showErrorPopup("Failed to create range: " + errorMessage);
                        fromBoundariesTextField.clear();
                        toBoundariesTextField.clear();
                        rangeNameTextField.clear();
                    });
                }
            }
        });
    }

    public void setMainController(ViewSheetMainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

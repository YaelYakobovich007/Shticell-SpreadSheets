package components.deleteRangePopUp;

import AnimationUtil.PopUpWindowAnimator;
import components.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.PopupManager;

public class DeleteRangePopUpController {

    @FXML private TextField rangeNameTextField;
    @FXML private ScrollPane mainContienrScrollPane;

    private AppController mainController;
    private Stage stage;

    @FXML
    private void initialize() {
        setupThemeListener();
        applyCurrentTheme(AppController.themeProperty().get());
    }

    private void setupThemeListener() {
        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> applyCurrentTheme(newTheme));
    }

    private void applyCurrentTheme(String newStyle) {
        String styleSheet = getClass().getResource("/util/popupWindow_" +newStyle+".css").toExternalForm();
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
        try {
            mainController.deleteRange(rangeName);
            PopupManager.showSuccessPopup("Range deleted successfully.");
            stage.close();
        } catch (Exception e) {
            PopupManager.showErrorPopup("Error: " + e.getMessage());
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;}

    public void setStage(Stage stage) {
        this.stage = stage;}
}

package components.NewRangePopup;

import AnimationUtil.PopUpWindowAnimator;
import components.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.PopupManager;

public class NewRangePopupController {

    @FXML private TextField fromBoundariesTextField;
    @FXML private TextField toBoundariesTextField;
    @FXML private TextField rangeNameTextField;
    @FXML private ScrollPane mainContinerScrollPane;

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
        try {
            mainController.createNewRange(rangeName, fromBoundaries, toBoundaries);
            PopupManager.showSuccessPopup("Range created successfully.");
            stage.close();
        } catch (Exception e) {
            PopupManager.showErrorPopup("Error: " + e.getMessage());
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

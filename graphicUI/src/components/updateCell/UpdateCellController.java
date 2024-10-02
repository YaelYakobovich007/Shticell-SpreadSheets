package components.updateCell;

import AnimationUtil.PopUpWindowAnimator;
import components.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.PopupManager;

public class UpdateCellController {
    @FXML private TextField cellValueField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private ScrollPane mainContinerScrollPane;
    private AppController mainController;
    private Stage stage;

    @FXML
    private void initialize() {
        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(AppController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popupWindow_" +newStyle+".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    @FXML void handleCancelButton(ActionEvent event) {
        Stage stage = (Stage) cellValueField.getScene().getWindow();
        PopUpWindowAnimator.applySlideOut(stage);
    }

    @FXML void handleUpdateButton(ActionEvent event){
        String newValue = cellValueField.getText();
        successLabel.setVisible(false);
        errorLabel.setVisible(false);
        try {
            mainController.updateCell(newValue);
            PopupManager.showSuccessPopup("Cell updated successfully!");
            stage.close();
            cellValueField.clear();
        } catch (Exception e){
            PopupManager.showErrorPopup(e.getMessage());
            errorLabel.setVisible(true);
            cellValueField.clear();
        }

    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}

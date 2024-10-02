package components.dynamicAnalysis;

import components.app.AppController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.PopupManager;

public class DynamicAnalysisController {
    public StackPane mainStackPane;
    @FXML private TextField cellTextField;
    @FXML private TextField minValueTextField;
    @FXML private TextField maxValueTextField;
    @FXML private TextField stepSizeTextField;
    @FXML private Label selectCellForAnalysisLabel;
    @FXML private Label cellValueLabel;
    @FXML private Button selectCellButton;
    @FXML private Button startAnalysisButton;
    @FXML private ScrollPane mainContinerScrollPane;

    private String cellValue;
    private AppController mainController;
    private Stage stage;
    private DynamicSliderController dynamicSliderController;
    private Parent dynamicSlider;
    private String cellId;

    @FXML
    private void initialize() {
        setupThemeListener();
        applyInitialTheme(AppController.themeProperty().get());
    }

    private void setupThemeListener() {
        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> applyInitialTheme(newTheme));
    }

    private void applyInitialTheme(String theme) {
        String styleSheet = getClass().getResource("/util/popupWindow_" + theme + ".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSliderController(DynamicSliderController dynamicSliderController) {
        this.dynamicSliderController = dynamicSliderController;
    }

    public void setDynamicSlider(Parent dynamicSlider){
        this.dynamicSlider = dynamicSlider;
    }

    @FXML
    void handleSelectCellAction(ActionEvent event) {
        if (cellTextField.getText().trim().isEmpty()) {
            PopupManager.showErrorPopup("Selection failed:The field is mandatory.Please enter a valid cell ID.");
        }else{
            attemptCellSelection();
        }
    }

    private void attemptCellSelection() {
        try {
            cellValue = mainController.isCellOriginalValueNumeric(cellTextField.getText());
            cellId = cellTextField.getText();
            proceedToAnalysisSetup();
        } catch (RuntimeException e) {
            PopupManager.showErrorPopup(e.getMessage());
        }
    }

    private void proceedToAnalysisSetup() {
        toggleCellSelectionVisibility(false);
        enableAnalysisFields();
        displayCellValue();
    }

    private void toggleCellSelectionVisibility(boolean isVisible) {
        selectCellButton.setVisible(isVisible);
        cellTextField.setVisible(isVisible);
        selectCellForAnalysisLabel.setVisible(isVisible);
    }

    private void enableAnalysisFields() {
        minValueTextField.setDisable(false);
        maxValueTextField.setDisable(false);
        stepSizeTextField.setDisable(false);
        startAnalysisButton.setDisable(false);
    }

    private void displayCellValue() {
        cellValueLabel.setVisible(true);
        cellValueLabel.setText("Cell Value: " + cellValue);
    }

    @FXML
    private void startAnalysis() {
        if (areAnalysisFieldsEmpty()) {
            PopupManager.showErrorPopup("All fields must be filled out to proceed.");
        } else {
            try {
                executeAnalysis();
            } catch (NumberFormatException e) {
                PopupManager.showErrorPopup("Please enter valid numeric values for Min, Max, and Step fields.");
            } catch (RuntimeException e) {
                PopupManager.showErrorPopup(e.getMessage());
            }
        }
    }

    private boolean areAnalysisFieldsEmpty() {
        return minValueTextField.getText().isEmpty() ||
                maxValueTextField.getText().isEmpty() ||
                stepSizeTextField.getText().isEmpty();
    }

    private void executeAnalysis() throws NumberFormatException {
        double minValue = Double.parseDouble(minValueTextField.getText());
        double maxValue = Double.parseDouble(maxValueTextField.getText());
        double stepSize = Double.parseDouble(stepSizeTextField.getText());
        double numericCellValue = Double.parseDouble(cellValue);

        if (validateInput(minValue, maxValue, stepSize, numericCellValue)) {
            dynamicSliderController.setSliderValues(minValue, maxValue, stepSize, numericCellValue, cellId);
            stage.close();
            mainController.showSlider(dynamicSlider);
        }
    }

    public boolean validateInput(double minValue, double maxValue, double stepSize, double cellValue) {
        if (minValue >= maxValue) {
            PopupManager.showErrorPopup("Error: Min value must be less than Max value.");
            return false;
        }

        if (cellValue < minValue || cellValue > maxValue) {
            PopupManager.showErrorPopup("Error: Cell value must be between Min and Max values.");
            return false;
        }

        double range = maxValue - minValue+1;
        if (stepSize <= 0 || stepSize > range) {
            PopupManager.showErrorPopup("Error: Step size must be positive and less than or equal to the range between Min and Max values.");
            return false;
        }

        if ((range % stepSize) != 0) {
            PopupManager.showErrorPopup("Error: Step size must divide the range evenly.");
            return false;
        }
        return true;
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        stage.close();
    }
}
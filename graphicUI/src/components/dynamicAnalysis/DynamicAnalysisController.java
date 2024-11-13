package components.dynamicAnalysis;

import com.google.gson.Gson;
import components.viewSheetMain.ViewSheetMainController;
import engine.CellDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.*;

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
    @FXML private Button addAnotherCellButton;
    private String cellValue;
    private ViewSheetMainController mainController;
    private Stage stage;
    private DynamicSliderController dynamicSliderController;
    private Parent dynamicSlider;
    private String cellId;
    private final HashMap<String,SelectedCell> selectedCells = new HashMap<>();


    @FXML
    private void initialize() {
        setupThemeListener();
        applyInitialTheme(ViewSheetMainController.themeProperty().get());
        addAnotherCellButton.setDisable(true);
    }

    private void setupThemeListener() {
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> applyInitialTheme(newTheme));
    }

    private void applyInitialTheme(String theme) {
        String styleSheet = getClass().getResource("/util/popUpWindowDesign/popupWindow_" + theme + ".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    public void setMainController(ViewSheetMainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSliderController(DynamicSliderController dynamicSliderController) {
        this.dynamicSliderController = dynamicSliderController;
    }

    public void setDynamicSlider(Parent dynamicSlider) {
        this.dynamicSlider = dynamicSlider;
    }

    @FXML
    void handleSelectCellAction(ActionEvent event) {
        cellId = cellTextField.getText().trim().toUpperCase();
        if (cellId.isEmpty()) {
            PopupManager.showErrorPopup("Selection failed: The field is mandatory. Please enter a valid cell ID.");
        } else if (selectedCells.containsKey(cellId)) {
            PopupManager.showErrorPopup("Selection failed: This cell has already been selected. Please choose a different cell.");
        } else {
            attemptCellSelection();
        }
    }


    private void attemptCellSelection() {
        String finalUrl = Constants.IS_CELL_NUMERIC_URL + "?cellId=" + cellTextField.getText();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Gson gson = new Gson();
                    CellDTO cellDTO = gson.fromJson(responseBody, CellDTO.class);
                    Platform.runLater(() -> {
                        cellValue = cellDTO.getOriginalValue();
                        proceedToAnalysisSetup();
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> PopupManager.showErrorPopup(errorMessage));
                }
            }
        });
    }

    private void proceedToAnalysisSetup() {
        toggleCellSelectionVisibility(false);
        enableAnalysisFields();
        displayCellValue();
        addAnotherCellButton.setDisable(false);
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
        if (selectedCells.isEmpty() && areAnalysisFieldsEmpty()) {
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

    @FXML
    void handleAddAnotherCellAction(ActionEvent event) {
        if (areAnalysisFieldsEmpty()) {
            PopupManager.showErrorPopup("All fields must be filled out to add another cell.");
        } else {
            try {
                double minValue = Double.parseDouble(minValueTextField.getText());
                double maxValue = Double.parseDouble(maxValueTextField.getText());
                double stepSize = Double.parseDouble(stepSizeTextField.getText());

                if (validateInput(minValue, maxValue, stepSize, Double.parseDouble(cellValue))) {
                    saveCellParameters(minValue, maxValue, stepSize);
                    clearFieldsForNextCell();
                }
            } catch (NumberFormatException e) {
                PopupManager.showErrorPopup("Please enter valid numeric values for Min, Max, and Step fields.");
            }
        }
    }

    private void saveCellParameters(double minValue, double maxValue, double stepSize) {
        SelectedCell selectedCell = new SelectedCell(cellId,Double.parseDouble(cellValue), minValue, maxValue, stepSize);
        selectedCells.put(cellId,selectedCell);
        PopupManager.showSuccessPopup("Cell added successfully.");
    }

    private void clearFieldsForNextCell() {
        cellTextField.clear();
        minValueTextField.clear();
        maxValueTextField.clear();
        stepSizeTextField.clear();
        cellValueLabel.setVisible(false);
        addAnotherCellButton.setDisable(true);
        toggleCellSelectionVisibility(true);
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
            saveCellParameters(minValue, maxValue, stepSize);
            dynamicSliderController.setSelectedCells(new HashSet<>(selectedCells.values()));
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

        double range = maxValue - minValue + 1;
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
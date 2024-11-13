package components.dynamicAnalysis;

import components.viewSheetMain.ViewSheetMainController;
import engine.SheetDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DynamicSliderController {
    @FXML private VBox sliderContainer;
    @FXML private ScrollPane sliderScrollPane;
    @FXML private Button finishButton;
    private ViewSheetMainController mainController;
    private Set<SelectedCell> selectedCells;
    private final Map<String, String> cellOriginalValues = new HashMap<>();
    private final Map<String, String> updatedCells = new HashMap<>();


    public void setMainAppController(ViewSheetMainController mainController) {
        this.mainController = mainController;
    }

    public void setSelectedCells(Set<SelectedCell> selectedCells) {
        this.selectedCells = selectedCells;
        initializeSliders();
    }

    private void initializeSliders() {
        sliderContainer.getChildren().clear();
        for (SelectedCell cell : selectedCells) {
            HBox sliderBox = createSliderBox(cell);
            sliderContainer.getChildren().add(sliderBox);
            cellOriginalValues.put(cell.getCellId(), String.valueOf(cell.getOrignalValue()));
        }
    }

    private HBox createSliderBox(SelectedCell cell) {
        HBox cellBox = createLabelHBox("Cell for analysis:", cell.getCellId());
        HBox minValueBox = createLabelHBox("Min Value:", String.valueOf(cell.getMinValue()));
        HBox maxValueBox = createLabelHBox("Max Value:", String.valueOf(cell.getMaxValue()));
        HBox stepSizeBox = createLabelHBox("Step Size:", String.valueOf(cell.getStepSize()));
        HBox currentValueBox = createLabelHBox("Current Value:", String.format("%.2f", cell.getOrignalValue()));

        Slider valueSlider = createSlider(cell, currentValueBox);

        HBox sliderBox = new HBox(10);
        sliderBox.getChildren().addAll(valueSlider);
        sliderBox.setAlignment(Pos.CENTER_LEFT);
        sliderBox.setPrefWidth(800);
        HBox.setHgrow(valueSlider, Priority.ALWAYS);

        HBox sliderEntryBox = new HBox(10);
        sliderEntryBox.setPadding(new Insets(10));
        sliderEntryBox.getChildren().addAll(cellBox, minValueBox, maxValueBox, stepSizeBox, currentValueBox, sliderBox);
        HBox.setHgrow(sliderEntryBox, Priority.ALWAYS);

        return sliderEntryBox;
    }

    private HBox createLabelHBox(String labelText, String valueText) {
        HBox hbox = new HBox(10);
        Label label = new Label(labelText);
        Label valueLabel = new Label(valueText);
        hbox.getChildren().addAll(label, valueLabel);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    private Slider createSlider(SelectedCell cell, HBox currentValueBox) {
        Slider valueSlider = new Slider(cell.getMinValue(), cell.getMaxValue(), cell.getOrignalValue());
        valueSlider.setMajorTickUnit(cell.getStepSize());
        valueSlider.setBlockIncrement(cell.getStepSize());
        valueSlider.setMinorTickCount(0);
        valueSlider.setSnapToTicks(true);
        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);
        valueSlider.getStyleClass().add("slider");

        Label currentValueLabel = (Label) currentValueBox.getChildren().get(1);
        valueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newValueDouble = newValue.doubleValue();
            currentValueLabel.setText(String.format("%.2f", newValueDouble));
            handleSliderValueChange(cell.getCellId(), oldValue.doubleValue(), newValueDouble);
        });

        return valueSlider;
    }

    private void handleSliderValueChange(String cellID, double oldValue, double newValue) {
        if (newValue != oldValue) {
            updatedCells.put(cellID, String.valueOf(newValue));
            mainController.updateTemporaryValuesInSheet(updatedCells, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup(e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        SheetDTO updatedSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                        Platform.runLater(() -> mainController.updateAllCellContent(updatedSheet));
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup(errorMessage));
                    }
                }
            });
        }
    }

    @FXML
    void handleFinishAction() {
        mainController.hideSlider(cellOriginalValues);
    }
}

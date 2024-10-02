package components.dynamicAnalysis;

import components.app.AppController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class DynamicSliderController {

    @FXML private Label minValueLabel;
    @FXML private Label maxValueLabel;
    @FXML private Label stepSizeLabel;
    @FXML private Label currentValueLabel;
    @FXML private Slider valueSlider;
    @FXML private Label cellLabel;

    private String cellId;
    private double originalCellValue;
    private AppController mainController ;

    public void setMainAppController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setSliderValues(double minValue, double maxValue, double stepSize, double currentValue, String cellID) {
        initializeSlider(minValue, maxValue, stepSize, currentValue);
        initializeLabels(minValue, maxValue, stepSize, currentValue, cellID);
        addSliderValueChangeListener();
    }

    private void addSliderValueChangeListener() {
        valueSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newValueDouble = newValue.doubleValue();
            currentValueLabel.setText(String.format("%.2f", newValueDouble));
            handleSliderValueChange(oldValue.doubleValue(), newValueDouble);
        });
    }

    private void initializeSlider(double minValue, double maxValue, double stepSize, double currentValue) {
        this.originalCellValue = currentValue;
        valueSlider.setMin(minValue);
        valueSlider.setMax(maxValue);
        valueSlider.setValue(currentValue);
        valueSlider.setMajorTickUnit(stepSize);
        valueSlider.setBlockIncrement(stepSize);
        valueSlider.setMinorTickCount(0);
        valueSlider.setSnapToTicks(true);
        valueSlider.setShowTickMarks(true);
        valueSlider.setShowTickLabels(true);
    }

    private void initializeLabels(double minValue, double maxValue, double stepSize, double currentValue, String cellID) {
        this.cellId = cellID;
        cellLabel.setText(cellID);
        minValueLabel.setText(String.valueOf(minValue));
        maxValueLabel.setText(String.valueOf(maxValue));
        stepSizeLabel.setText(String.valueOf(stepSize));
        currentValueLabel.setText(String.format("%.2f", currentValue));
    }

    private void handleSliderValueChange(double oldValue,double newValue) {
        if(newValue!=oldValue) {
            mainController.updateTemporaryValuesInSheet(String.valueOf(newValue),cellId);
        }
    }

    @FXML
    void handleFinishAction(ActionEvent event) {
        mainController.hideSlider(cellId,originalCellValue);
    }



}

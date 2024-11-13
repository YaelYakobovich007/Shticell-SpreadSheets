package components.header;

import javafx.beans.property.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class HeaderUIModel {
    private final StringProperty lastUpdateCellVersionProperty;
    private final StringProperty originalCellValueProperty;
    private final StringProperty selectedCellIDProperty;
    private final StringProperty updateByUserNameProperty;

    public HeaderUIModel(Label lastUpdateCellVersionLabel,
                         Label originalCellValueLabel,
                         Label selectedCellIDLabel,
                         ComboBox<Integer> comboBox,
                         Label updateByUserLabel) {

        this.lastUpdateCellVersionProperty = new SimpleStringProperty("Last update cell version");
        this.originalCellValueProperty = new SimpleStringProperty("Original Cell value");
        this.selectedCellIDProperty = new SimpleStringProperty("Selected Cell ID");
        this.updateByUserNameProperty = new SimpleStringProperty("Update by User");

        bindLabelToProperty(lastUpdateCellVersionLabel, this.lastUpdateCellVersionProperty);
        bindLabelToProperty(originalCellValueLabel, this.originalCellValueProperty);
        bindLabelToProperty(selectedCellIDLabel, this.selectedCellIDProperty);
        bindLabelToProperty(updateByUserLabel, this.updateByUserNameProperty);
    }

    private void bindLabelToProperty(Label label, StringProperty property) {
        label.textProperty().bind(property);
    }


    public void setLastUpdate(String lastUpdate) {
        lastUpdateCellVersionProperty.set(lastUpdate);
    }


    public void setOriginalCellValue(String originalCellValue) {
        originalCellValueProperty.set(originalCellValue);
    }

    public void setSelectedCellID(String selectedCellID) {
        selectedCellIDProperty.set(selectedCellID);
    }

    public void setUpdateByUserName(String updateByUserName) {
        updateByUserNameProperty.set(updateByUserName);
    }
}

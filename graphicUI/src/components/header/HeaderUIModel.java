package components.header;

import javafx.beans.property.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class HeaderUIModel {
    private final StringProperty filePathProperty;
    private final StringProperty lastUpdateCellVersionProperty;
    private final StringProperty originalCellValueProperty;
    private final StringProperty selectedCellIDProperty;
    private final SimpleBooleanProperty isFileSelected;

    public HeaderUIModel(Label filePathLabel,
                         Label lastUpdateCellVersionLabel,
                         Label originalCellValueLabel,
                         Label selectedCellIDLabel,
                         SimpleBooleanProperty isFileSelected,
                         ComboBox<Integer> comboBox) {

        this.filePathProperty = new SimpleStringProperty("Currently Loaded File path");
        this.lastUpdateCellVersionProperty = new SimpleStringProperty("Last update cell version");
        this.originalCellValueProperty = new SimpleStringProperty("Original Cell value");
        this.selectedCellIDProperty = new SimpleStringProperty("Selected Cell ID");
        this.isFileSelected = isFileSelected;

        bindLabelToProperty(filePathLabel, this.filePathProperty);
        bindLabelToProperty(lastUpdateCellVersionLabel, this.lastUpdateCellVersionProperty);
        bindLabelToProperty(originalCellValueLabel, this.originalCellValueProperty);
        bindLabelToProperty(selectedCellIDLabel, this.selectedCellIDProperty);
    }

    private void bindLabelToProperty(Label label, StringProperty property) {
        label.textProperty().bind(property);
    }

    public void setFilePath(String path) {
        filePathProperty.set(path);
    }

    public void setLastUpdate(String lastUpdate) {
        lastUpdateCellVersionProperty.set(lastUpdate);
    }

    public void setFileLoaded(boolean loaded) {
        isFileSelected.set(loaded);
    }

    public void setOriginalCellValue(String originalCellValue) {
        originalCellValueProperty.set(originalCellValue);
    }

    public void setSelectedCellID(String selectedCellID) {
        selectedCellIDProperty.set(selectedCellID);
    }
}

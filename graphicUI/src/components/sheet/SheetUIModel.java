package components.sheet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import java.util.HashMap;
import java.util.Map;

public class SheetUIModel {
    private final Map<String, StringProperty> cellProperties = new HashMap<>();
    private final ObjectProperty<Label> selectedCell;
    private final Map<String, Boolean> colorFillStatus = new HashMap<>();


    public SheetUIModel(Map<String, Label> cellMap,ObjectProperty<Label> selectedCell){
        for (Map.Entry<String, Label> entry : cellMap.entrySet()) {
            StringProperty cellProperty = new SimpleStringProperty();
            entry.getValue().textProperty().bind(cellProperty);
            cellProperties.put(entry.getKey(), cellProperty);
            colorFillStatus.put(entry.getKey(), true);

        }
        this.selectedCell = selectedCell;

    }

    public void updateCellContent(String cellId, String content) {
        StringProperty property = cellProperties.get(cellId);
        if (property != null) {
            property.set(content);
        }
    }

    public ObjectProperty<Label> getSelectedCellProperty() {
        return selectedCell;
    }

    public void setColorFillStatus(String cellId, boolean status) {
        colorFillStatus.put(cellId, status);
    }

    public boolean getColorFillStatus(String cellId) {
        return colorFillStatus.getOrDefault(cellId, true);
    }
}

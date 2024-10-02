package components.sheet;

import engine.CellDTO;
import engine.RangeDTO;
import engine.SheetDTO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import sheet.coordinate.Coordinate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class sheetControllerImpl implements SheetController {
    private GridPane masterGridPane;
    private final Map<String, Label> cellMap = new HashMap<>();
    private final Set<String> previouslyHighlightedCells = new HashSet<>();
    private RangeDTO selectedRange=null;
    private SheetUIModel uiModel;
    private ObjectProperty<Label> selectedCell;

    public GridPane createDynamicGrid(SheetDTO sheetDTO) {
        SheetGridBuilder gridBuilder = new SheetGridBuilder();
        masterGridPane = gridBuilder.buildGrid(sheetDTO);
        cellMap.putAll(gridBuilder.getCellMap());
        attachClickEvents();
        setUpSelectedCellListener();
        uiModel = new SheetUIModel(cellMap, selectedCell);

        sheetDTO.getActiveCells().forEach((coordinate, cellDTO) -> {
            String cellId = coordinate.toString();
            Label cellLabel = cellMap.get(cellId);

            uiModel.updateCellContent(cellId, FormattedValuePrinter.formatValue(cellDTO.getEffectiveValue()));

            Color backgroundColor = sheetDTO.getBackgroundColorsAsColors().getOrDefault(coordinate, Color.WHITE);
            if (!backgroundColor.equals(Color.WHITE)) {
                cellLabel.getStyleClass().remove("background-cell");
                BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY);
                cellLabel.setBackground(new Background(backgroundFill));
            }
            Color textColor = sheetDTO.getTextColorsAsColors().getOrDefault(coordinate, Color.BLACK);
            cellLabel.setTextFill(textColor);

        });

        sheetDTO.getBackgroundColorsAsColors().forEach((coordinate, color) -> {
            String cellId = coordinate.toString();
            Label cellLabel = cellMap.get(cellId);
            if (cellLabel != null) {
                applyBackgroundColor(cellLabel, color);
            }
        });

        sheetDTO.getTextColorsAsColors().forEach((coordinate, color) -> {
            String cellId = coordinate.toString();
            Label cellLabel = cellMap.get(cellId);
            if (cellLabel != null) {
                applyTextColor(cellLabel, color);
            }
        });
        return masterGridPane;
    }


    public void updateAllCellContent(SheetDTO sheetDTO){
        sheetDTO.getActiveCells().forEach((coordinate, cellDTO) -> {
            String cellId = coordinate.toString();
            uiModel.updateCellContent(cellId, FormattedValuePrinter.formatValue(cellDTO.getEffectiveValue()));
        });
    }

    private void setUpSelectedCellListener() {
        selectedCell = new SimpleObjectProperty<>();
        selectedCell.addListener((observableValue, oldLabelSelection, newSelectedLabel) -> {
            if (oldLabelSelection != null) {
                oldLabelSelection.setId(null);
            }
            if (newSelectedLabel != null) {
                newSelectedLabel.setId("selected-cell");
            }
        });
    }
    private void attachClickEvents() {
        cellMap.values().forEach(this::addClickEventForCell);
    }

    private void addClickEventForCell(Label label) {
        label.setOnMouseClicked(event -> selectedCell.set(label));
    }

    public SheetUIModel getSheetUIModel() {
        return uiModel;
    }

    public void handleCellClick(CellDTO cellDTO) {
        clearPreviousHighlights();
        if(cellDTO!=null) {
            highlightDependsOnCells(cellDTO);
            highlightInfluenceOnCells(cellDTO);
        }
    }

    public void highlightDependsOnCells(CellDTO cellDTO) {
        cellDTO.getDependentSources().forEach(dependsOnCell -> {
            String dependsOnCellId = dependsOnCell.getCoordinate().toString();
            Label dependsOnLabel = cellMap.get(dependsOnCellId);
            if (dependsOnLabel != null) {
                dependsOnLabel.getStyleClass().add("depends-on-cell");
                previouslyHighlightedCells.add(dependsOnCellId);
            }
        });
    }

    public void highlightInfluenceOnCells(CellDTO cellDTO) {
        cellDTO.getInfluencedCells().forEach(influencedCell -> {
            String influencedCellId = influencedCell.getCoordinate().toString();
            Label influencedLabel = cellMap.get(influencedCellId);
            if (influencedLabel != null) {
                influencedLabel.getStyleClass().add("influence-on-cell");
                previouslyHighlightedCells.add(influencedCellId);
            }
        });
    }

    private void clearPreviousHighlights() {
        previouslyHighlightedCells.forEach(cellId -> {
            Label label = cellMap.get(cellId);
            if (label != null) {
                label.getStyleClass().remove("depends-on-cell");
                label.getStyleClass().remove("influence-on-cell");
            }
        });
        previouslyHighlightedCells.clear();
    }

    public int getSelectedCellColumnWidth() {
        Integer columnIndex = GridPane.getColumnIndex(selectedCell.get());
        return (int) masterGridPane.getColumnConstraints().get(columnIndex).getPrefWidth();
    }

    public int getSelectedCellRowHeight() {
        Integer rowIndex = GridPane.getRowIndex(selectedCell.get());
        return (int) masterGridPane.getRowConstraints().get(rowIndex).getPrefHeight();
    }

    public void applyRowHeight(int rowHeight) {
        if (selectedCell.get() != null) {
            Integer rowIndex = GridPane.getRowIndex(selectedCell.get());
            if (rowIndex != null) {
                RowConstraints rowConstraints = masterGridPane.getRowConstraints().get(rowIndex);
                rowConstraints.setMinHeight(rowHeight);
                rowConstraints.setPrefHeight(rowHeight);
                rowConstraints.setMaxHeight(rowHeight);
            }
        }
    }

    public void applyColumnWidth(int columnWidth) {
        if (selectedCell.get() != null) {
            Integer columnIndex = GridPane.getColumnIndex(selectedCell.get());
            if (columnIndex != null) {
                ColumnConstraints columnConstraints = masterGridPane.getColumnConstraints().get(columnIndex);
                columnConstraints.setMinWidth(columnWidth);
                columnConstraints.setPrefWidth(columnWidth);
                columnConstraints.setMaxWidth(columnWidth);
            }
        }
    }

    public void changeColumnAlignment(Pos alignment) {
        Integer columnIndex = GridPane.getColumnIndex(selectedCell.get());
        for (Label label : cellMap.values()) {
            Integer labelColumnIndex = GridPane.getColumnIndex(label);
            if (labelColumnIndex != null && labelColumnIndex.equals(columnIndex)) {
                label.setAlignment(alignment);
            }
        }
    }

    public void setSelectedCellBackgroundColor(Color color) {
        if (selectedCell != null && selectedCell.get() != null){
            selectedCell.get().getStyleClass().remove("background-cell");
            BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
            selectedCell.get().setBackground(new Background(backgroundFill));
        }
    }

    public void setSelectedCellTextColor(Color color) {
        if (selectedCell != null && selectedCell.get() != null) {
            selectedCell.get().setTextFill(color);
        }
    }

    public void resetCellStyle(){
        selectedCell.get().getStyleClass().add("background-cell");
        selectedCell.get().setTextFill(Color.BLACK);
    }


    public void showSelectRange(RangeDTO selectedRange){
        resetSelectedRange();
        this.selectedRange=selectedRange;
        Set<Coordinate> range= selectedRange.getCoordinateInRange();
        for(Coordinate coordinate : range){
            Label cellInRange= cellMap.get(coordinate.toString());
            cellInRange.getStyleClass().add("cell-in-range");
        }
    }

    public void resetSelectedRange() {
        if (selectedRange != null) {
            for (Coordinate coordinate : selectedRange.getCoordinateInRange()) {
                Label cellInRange = cellMap.get(coordinate.toString());
                cellInRange.getStyleClass().remove("cell-in-range");
            }
            selectedRange = null;
        }
    }

    private void applyBackgroundColor(Label label, Color color) {
        if (!color.equals(Color.WHITE)) {
            label.getStyleClass().remove("background-cell");
            BackgroundFill backgroundFill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
            label.setBackground(new Background(backgroundFill));
        }
    }

    private void applyTextColor(Label label, Color color) {
        label.getStyleClass().remove("background-cell");
        label.setTextFill(color);
    }
}

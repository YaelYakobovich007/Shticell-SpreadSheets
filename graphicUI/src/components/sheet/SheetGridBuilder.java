package components.sheet;

import engine.SheetDTO;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.Map;

public class SheetGridBuilder {
    private  final GridPane gridPane;
    private final Map<String, Label> cellMap;

    public SheetGridBuilder() {
        this.gridPane = new GridPane();
        this.cellMap = new HashMap<>();
    }

    public GridPane buildGrid(SheetDTO sheetDTO) {
        int rows = sheetDTO.getLayout().getNumOfRows();
        int columns = sheetDTO.getLayout().getNumOfColumns();
        int columnWidth = sheetDTO.getLayout().getLayoutSize().getColumnWidthUnit();
        int rowHeight = sheetDTO.getLayout().getLayoutSize().getRowHeightUnit();

        setupGridPane();
        addColumnConstraints(columns, columnWidth);
        addRowConstraints(rows, rowHeight);
        addColumnHeaders(columns);
        addRowHeaders(rows);
        createCells(sheetDTO);

        return gridPane;
    }

    private void setupGridPane(){
        gridPane.setHgap(15.0);
        gridPane.setVgap(15.0);
        gridPane.setPadding(new Insets(20, 0, 0, 0));
        gridPane.getStylesheets().add(getClass().getResource("single-cell.css").toExternalForm());
    }

    private void createCells(SheetDTO sheetDTO){
        int rows = sheetDTO.getLayout().getNumOfRows();
        int columns = sheetDTO.getLayout().getNumOfColumns();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= columns; col++) {
                String cellId = createCellId(col, row);
                Label cellLabel = createCellLabel();
                cellLabel.setUserData(cellId);
                GridPane.setHalignment(cellLabel, HPos.CENTER);
                GridPane.setColumnIndex(cellLabel, col + 1);
                GridPane.setRowIndex(cellLabel, row);
                gridPane.add(cellLabel, col + 1, row);
                cellMap.put(cellId, cellLabel);
            }
        }
    }

    private Label createCellLabel(){
        Label cellLabel = new Label();
        cellLabel.setAlignment(Pos.CENTER);
        cellLabel.setMaxWidth(Double.MAX_VALUE);
        cellLabel.setMaxHeight(Double.MAX_VALUE);
        cellLabel.getStyleClass().addAll("single-cell", "background-cell");
        return cellLabel;
    }

    private String createCellId(int col, int row) {
        String stringRow = String.valueOf((char) ('0' + row));
        String stringCol = String.valueOf((char) ('A' + (col - 1)));
        return stringCol + stringRow;
    }

    private void addColumnConstraints(int numCols, int columnWidth) {
        ColumnConstraints spaceCol = new ColumnConstraints();
        spaceCol.setHgrow(Priority.ALWAYS);
        spaceCol.setMinWidth(10.0);
        spaceCol.setPrefWidth(100.0);
        gridPane.getColumnConstraints().add(spaceCol);

        ColumnConstraints headercol = new ColumnConstraints();
        headercol.setFillWidth(false);
        headercol.setHalignment(HPos.CENTER);
        headercol.setHgrow(Priority.NEVER);
        headercol.setPrefWidth(20.0);
        gridPane.getColumnConstraints().add(headercol);


        for (int i = 0; i < numCols; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(javafx.scene.layout.Priority.NEVER);
            col.setMaxWidth(columnWidth);
            col.setMinWidth(columnWidth);
            col.setPrefWidth(columnWidth);
            gridPane.getColumnConstraints().add(col);
        }
        ColumnConstraints spaceColeEnd = new ColumnConstraints();
        spaceColeEnd.setHgrow(Priority.ALWAYS);
        spaceColeEnd.setMinWidth(10.0);
        spaceColeEnd.setPrefWidth(100.0);
        gridPane.getColumnConstraints().add(spaceColeEnd);
    }

    private void addRowConstraints(int numRows, int rowHeight) {
        RowConstraints spacerow = new RowConstraints();
        spacerow.setVgrow(Priority.SOMETIMES);
        spacerow.setMinHeight(10.0);
        spacerow.setPrefHeight(rowHeight);
        gridPane.getRowConstraints().add(spacerow);

        for (int i = 0; i <= numRows; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(javafx.scene.layout.Priority.NEVER);
            row.setMinHeight(rowHeight);
            row.setPrefHeight(rowHeight);
            row.setPrefHeight(rowHeight);
            gridPane.getRowConstraints().add(row);
        }
        RowConstraints row1 = new RowConstraints();
        row1.setFillHeight(false);
        row1.setMaxHeight(20);

        row1.setMaxHeight(Double.MAX_VALUE);

        row1.setMinHeight(30.0);
        row1.setPrefHeight(30.0);
        row1.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(row1);
    }

    private void addColumnHeaders(int numCols){
        for (int col = 0; col < numCols; col++){
            Label columnHeader = new Label(String.valueOf((char) ('A' + col)));
            GridPane.setHalignment(columnHeader,HPos.CENTER);
            GridPane.setHgrow(columnHeader, Priority.ALWAYS);
            GridPane.setColumnIndex(columnHeader, col+2);
            GridPane.setRowIndex(columnHeader, 0);
            gridPane.add(columnHeader, col + 2, 0);
        }
    }

    private void addRowHeaders(int numRows) {
        for (int row = 0; row < numRows; row++) {
            Label rowHeader = new Label(String.format("%02d", row + 1));
            rowHeader.setAlignment(Pos.TOP_CENTER);
            GridPane.setColumnIndex(rowHeader, 1);
            GridPane.setRowIndex(rowHeader, row+1);
            gridPane.add(rowHeader, 1, row + 1);
        }
    }

    public Map<String, Label> getCellMap() {
        return cellMap;
    }
}
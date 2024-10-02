package components.sheet;

import engine.CellDTO;
import engine.RangeDTO;
import engine.SheetDTO;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
public interface SheetController {
    GridPane createDynamicGrid(SheetDTO sheetDTO);
    SheetUIModel getSheetUIModel();
     void handleCellClick(CellDTO cellDTO);
     void applyRowHeight(int rowHeight);
     void applyColumnWidth(int columnWidth);
     int getSelectedCellColumnWidth();
     int getSelectedCellRowHeight();
     void changeColumnAlignment(Pos alignment);
     void setSelectedCellBackgroundColor(Color color) ;
     void setSelectedCellTextColor(Color color);
     void resetCellStyle();
     void updateAllCellContent(SheetDTO sheetDTO);
     void showSelectRange(RangeDTO selectedRange);
     void resetSelectedRange();
}

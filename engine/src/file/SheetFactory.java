package file;

import jaxb.schema.STLCell;
import jaxb.schema.STLRange;
import jaxb.schema.STLRanges;
import jaxb.schema.STLSheet;
import parse.ExpressionFactory;
import range.RangesManager;
import sheet.api.Layout;
import sheet.api.CellSize;
import sheet.api.Sheet;
import sheet.impl.LayoutImpl;
import sheet.impl.CellSizeImpl;
import sheet.impl.SheetImpl;
import java.util.List;


public class SheetFactory {
    public static Sheet createSheet(STLSheet stlSheet) {
        Layout layout = createLayout(stlSheet);
        RangesManager ranges= createRangesManager(stlSheet.getSTLRanges(),layout);
        Sheet sheet = new SheetImpl(stlSheet.getName(), layout, ranges);

        ExpressionFactory.setSheet(sheet);
        PopulateActiveCellsMap(stlSheet, sheet);

        return sheet;
    }

    private static int convertColumnToNumber(String column) {
        column = column.trim().toUpperCase();
        return column.charAt(0) - 'A' + 1;
    }

    private static Layout createLayout(STLSheet stlSheet) {
        CellSize cellSize = new CellSizeImpl(stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits(), stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits());
        return new LayoutImpl(stlSheet.getSTLLayout().getRows(), stlSheet.getSTLLayout().getColumns(), cellSize);
    }

    private static void PopulateActiveCellsMap(STLSheet stlSheet, Sheet sheet) {
        for (STLCell stlCell : stlSheet.getSTLCells().getSTLCell()) {
            int row = stlCell.getRow();
            int column = convertColumnToNumber(stlCell.getColumn());
            String value = stlCell.getSTLOriginalValue();
            sheet.initializeCell(row, column, value);
        }
    }

    private  static RangesManager createRangesManager(STLRanges stlRanges, Layout layout){
        RangesManager rangesManager= new RangesManager(layout);
        List<STLRange> ranges= stlRanges.getSTLRange();

        ranges.forEach(stlRange -> rangesManager.createRange(stlRange.getName(), stlRange.getSTLBoundaries().getFrom(), stlRange.getSTLBoundaries().getTo()));

        return rangesManager;
    }
}




package components.sort;

import java.util.HashSet;
import java.util.Set;

public class RangeUtils {
    public static Set<String> getColumnsFromRange(String from, String to) {
        Set<String> columnSet = new HashSet<>();
        String topLeftColumn = from.replaceAll("[0-9]", "");
        String bottomRightColumn = to.replaceAll("[0-9]", "");

        for (int colIndex = getColumnIndex(topLeftColumn.charAt(0)); colIndex <= getColumnIndex(bottomRightColumn.charAt(0)); colIndex++) {
            columnSet.add("Column " + getColumnLetter(colIndex));
        }
        return columnSet;
    }

    private static int getColumnIndex(char column) {
        return column - 'A' + 1;
    }

    private static String getColumnLetter(int index) {
        return Character.toString((char) (index + 'A' - 1)).toUpperCase();
    }



}

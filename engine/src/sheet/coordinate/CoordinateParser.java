package sheet.coordinate;

import exception.InvalidCoordinateException;
import sheet.api.Layout;

public class CoordinateParser {
    public static int[] parseCoordinate(String coordinate) {
        coordinate = coordinate.trim().toUpperCase();

        if (coordinate.length() < 2) {
            throw new InvalidCoordinateException(
                    "Invalid coordinate: " + coordinate + ". A valid coordinate should start with a letter followed by a number (e.g., A1, T5)."
            );
        }

        char columnChar = coordinate.charAt(0);
        int row;
        try {
            row = Integer.parseInt(coordinate.substring(1));
        } catch (NumberFormatException e) {
            throw new InvalidCoordinateException(
                    "Invalid row number in coordinate: " + coordinate + ". The row number should be a valid integer (e.g., A1, B10)."
            );
        }
        int column = columnChar - 'A' + 1;

        return new int[]{row, column};
    }

    public static void validateCoordinates(int row, int column, Layout layout) {
        int maxRows = layout.getNumOfRows();
        int maxColumns = layout.getNumOfColumns();

        if (row < 1 || row > maxRows) {
            throw new InvalidCoordinateException(
                    "Invalid row number: " + row + ". The row number must be between 1 and " + maxRows + "."
            );
        }

        if (column < 1 || column > maxColumns) {
            throw new InvalidCoordinateException(
                    "Invalid column number: " + column + ". The column must be between 1 (A) and " + maxColumns + " (" + (char) ('A' + maxColumns - 1) + ")."
            );
        }
    }
}

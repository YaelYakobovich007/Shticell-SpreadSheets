package sheet.coordinate;

import sheet.api.Layout;
import java.util.HashMap;
import java.util.Map;

public class CoordinateFactory {
    private static final Map<String, Coordinate> cachedCoordinates = new HashMap<>();

    public static Coordinate createCoordinate(int row, int column) {
        String key = row + ":" + column;

        if (cachedCoordinates.containsKey(key)) {
            return cachedCoordinates.get(key);
        }
        CoordinateImpl coordinate = new CoordinateImpl(row, column);
        cachedCoordinates.put(key, coordinate);

        return coordinate;
    }

    public static Coordinate createCoordinate(String coordinateString, Layout layout) {
        int []parseCoordinate=  CoordinateParser.parseCoordinate(coordinateString.trim().toUpperCase());
        CoordinateParser.validateCoordinates(parseCoordinate[0], parseCoordinate[1],layout);
        return CoordinateFactory.createCoordinate(parseCoordinate[0], parseCoordinate[1]);
    }
}

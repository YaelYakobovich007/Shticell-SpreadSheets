package range;

import sheet.api.Layout;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import java.io.Serializable;

public class BoundariesFactory implements Serializable {

    public static Boundaries createBoundaries(String from, String to, Layout layout){

        Coordinate fromCoordinate= CoordinateFactory.createCoordinate(from,layout);
        Coordinate toCoordinate= CoordinateFactory.createCoordinate(to,layout);

        if (isValidBoundaries(fromCoordinate,toCoordinate)){
            return new Boundaries(fromCoordinate,toCoordinate);
        }
        else{
            throw new RuntimeException("Invalid boundaries");
        }
    }

    private static boolean isValidBoundaries(Coordinate from, Coordinate to){
        return (from.getRow()  <= to.getRow()) && (from.getColumn() <= to.getColumn());

    }
}

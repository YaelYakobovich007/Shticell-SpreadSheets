package expression.impl.numrical;

import expression.api.Expression;
import parse.ExpressionFactory;
import parse.ExpressionParser;
import parse.SUMParser;
import range.Range;
import sheet.cell.api.Cell;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;
import sheet.coordinate.Coordinate;
import java.util.Set;

public class SUM implements Expression {
    public final Range range;
    public static final ExpressionParser<SUM> PARSER;

    public SUM(Range range) {
        this.range = range;
    }

    @Override
    public EffectiveValue evaluate()
    {
        if(range== null){
            return  new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        double sum=0;
        Set<Coordinate> coordinateInRange=  range.getCoordinateInRange();

        for(Coordinate coordinate : coordinateInRange){
            Cell cell= ExpressionFactory.currentSheet.getCell(coordinate.getRow(),coordinate.getColumn());
            if(cell== null){
                sum= Double.NaN;
            }
            try {
                sum += cell.getEffectiveValue().extractValueWithExpectation(Double.class);
                ExpressionFactory.currentCell.addDependency(cell);
            }catch (IllegalArgumentException e){
                sum= Double.NaN;
            }
        }
        return new EffectiveValueImpl(CellType.NUMERIC, sum);
    }

    static {
        PARSER = new SUMParser<>("SUM", SUM.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.NUMERIC;
    }


}

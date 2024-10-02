package expression.impl.numrical;

import expression.api.Expression;
import parse.AVERAGEParser;
import parse.ExpressionFactory;
import parse.ExpressionParser;
import range.Range;
import sheet.cell.api.Cell;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;
import sheet.coordinate.Coordinate;
import java.util.Set;

public class AVERAGE implements Expression {
    public final Range range;
    public static final ExpressionParser<AVERAGE> PARSER;

    public AVERAGE(Range range) {
        this.range = range;
    }

    @Override
    public EffectiveValue evaluate()
    {
        if(range==null){
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        double sum=0;
        double average;

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
        average= sum/coordinateInRange.size();

        return new EffectiveValueImpl(CellType.NUMERIC, average);
    }

    static {
        PARSER = new AVERAGEParser<>("AVERAGE", AVERAGE.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.NUMERIC;
    }

}

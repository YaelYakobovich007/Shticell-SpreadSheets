package expression.impl;

import expression.api.Expression;
import parse.ExpressionFactory;
import parse.ExpressionParser;
import parse.RefParser;
import sheet.cell.api.Cell;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;
import sheet.coordinate.Coordinate;

public class REF implements Expression {

    private final Coordinate coordinate;
    public static final ExpressionParser<REF> PARSER;

    public REF(Coordinate coordinate){
        this.coordinate=coordinate;
    }

    @Override
    public EffectiveValue evaluate()
    {
        Cell referenceCell =ExpressionFactory.currentSheet.getCell(coordinate.getRow(),coordinate.getColumn());
        if(referenceCell == null){
            return new EffectiveValueImpl(CellType.UNKNOWN,null);
        }
        ExpressionFactory.currentCell.addDependency(referenceCell);
        return  referenceCell.getEffectiveValue();
    }

    static {
        PARSER = new RefParser<>("REF", REF.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.UNKNOWN;
    }

}

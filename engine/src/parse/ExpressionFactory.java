package parse;

import expression.impl.Number;
import expression.impl.*;
import expression.api.Expression;
import expression.impl.logic.*;
import expression.impl.numrical.*;
import expression.impl.string.CONCAT;
import expression.impl.string.SUB;
import sheet.api.SheetReadActions;
import sheet.cell.api.Cell;
import java.util.HashMap;
import java.util.Map;

public class ExpressionFactory {
    public static SheetReadActions currentSheet;
    public static Cell currentCell;
    private static final Map<String, ExpressionParser<?>> parserMap = new HashMap<>();
    public static void setSheet(SheetReadActions sheet){
        ExpressionFactory.currentSheet = sheet;
    }
    public static void setCurrentCell(Cell cell) {
        ExpressionFactory.currentCell = cell;
    }

    static {
        parserMap.put("PLUS", PLUS.PARSER);
        parserMap.put("MINUS", MINUS.PARSER);
        parserMap.put("TIMES", TIMES.PARSER);
        parserMap.put("DIVIDE", DIVIDE.PARSER);
        parserMap.put("SUB", SUB.PARSER);
        parserMap.put("CONCAT", CONCAT.PARSER);
        parserMap.put("MOD", MOD.PARSER);
        parserMap.put("ABS", ABS.PARSER);
        parserMap.put("POW", POW.PARSER);
        parserMap.put("REF", REF.PARSER);
        parserMap.put("AND", AND.PARSER);
        parserMap.put("BIGGER", BIGGER.PARSER);
        parserMap.put("EQUAL", EQUAL.PARSER);
        parserMap.put("LESS", LESS.PARSER);
        parserMap.put("NOT", NOT.PARSER);
        parserMap.put("OR", OR.PARSER);
        parserMap.put("SUM", SUM.PARSER);
        parserMap.put("AVERAGE", AVERAGE.PARSER);
        parserMap.put("PERCENT", PERCENT.PARSER);
        parserMap.put("IF", IF.PARSER);
    }

    public static Expression createExpression(String input) {
        if (StringValidators.isNumber(input)) {
            return new Number(Double.parseDouble(input));
        }
        if (StringValidators.isBoolean(input)) {
            return new BooleanValue(Boolean.parseBoolean(input));
        }
        if (StringValidators.isFunction(input)){
            return parseFunction(input);
        }
        else {
            return new Text(input);}

    }
    private static Expression parseFunction(String input) {
        String functionName = extractFunctionName(input);
        ExpressionParser<?> parser = parserMap.get(functionName);
        if (parser != null) {
            return parser.parse(input);
        } else {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    private static String extractFunctionName(String input){
        int start = input.indexOf("{") + 1;
        int end = input.indexOf(",");
        if (start < end) {
            return input.substring(start, end).toUpperCase();
        }
        return "";
    }
}

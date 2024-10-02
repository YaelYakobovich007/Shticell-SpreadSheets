package parse;
import exception.ErrorMessageUtil;
import expression.api.Expression;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;
import java.util.List;

public class RefParser<T extends Expression> extends FunctionParser<T> {

    public RefParser(String functionName, Class<T> expressionClass) {
        super(functionName,expressionClass);
    }

    @Override
    protected T createExpressionFromArguments(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new RuntimeException(ErrorMessageUtil.generateIncorrectNumberOfArgumentsMessage(functionName,1,arguments.size()));
        }
        int []parsedCoordinate=  CoordinateParser.parseCoordinate(arguments.getFirst().trim().toUpperCase());
        CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1],ExpressionFactory.currentSheet.getLayout());
        Coordinate coordinate= CoordinateFactory.createCoordinate(parsedCoordinate[0], parsedCoordinate[1]);
        try {
            return expressionClass.getConstructor(Coordinate.class).newInstance(coordinate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + expressionClass.getSimpleName(), e);
        }
    }
}

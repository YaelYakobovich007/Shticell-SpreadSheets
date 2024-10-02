package exception;

public class ErrorMessageUtil {
    public static String generateIncorrectNumberOfArgumentsMessage(String functionName, int expectedCount, int actualCount) {
        return String.format(
                "Function '%s' expects %d arguments, but got %d.\n",
                functionName,
                expectedCount,
                actualCount
        );
    }

    private static String getExampleUsage(String functionName) {
        return switch (functionName) {
            case "PLUS" -> "{PLUS, 5, 10} - Adds two numbers and returns the result.";
            case "MINUS" -> "{MINUS, 15, 5} - Subtracts the second number from the first and returns the result.";
            case "TIMES" -> "{TIMES, 4, 5} - Multiplies two numbers and returns the result.";
            case "DIVIDE" ->
                    "{DIVIDE, 20, 4} - Divides the first number by the second and returns the result. If the second number is 0, the result is NaN.";
            case "MOD" -> "{MOD, 10, 3} - Performs a modulo operation between two numbers and returns the remainder.";
            case "POW" ->
                    "{POW, 2, 3} - Raises the first number to the power of the second number and returns the result.";
            case "ABS" -> "{ABS, -5} - Returns the absolute value of the argument.";
            case "CONCAT" -> "{CONCAT, 'Hello', ' World'} - Concatenates two strings and returns the combined string.";
            case "SUB" ->
                    "{SUB, 'Hello World', 0, 4} - Extracts a substring from the source string, starting from the first index and ending at the second index (inclusive).";
            case "REF" -> "{REF, A1} - References the value of the cell identified by the given cell ID.";
            default -> "{FUNCTION_NAME, arg1, arg2, ...} - General format.";
        };
    }

}

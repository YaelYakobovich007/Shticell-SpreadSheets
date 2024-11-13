package parse;

public class StringValidators
{
    public static boolean isBoolean(String input)
    {
        return input.equalsIgnoreCase("TRUE") || input.equalsIgnoreCase("FALSE");
    }

    public static boolean isNumber(String input)
    {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static boolean isFunction(String input)
    {
        return input.startsWith("{") && input.endsWith("}");
    }
}

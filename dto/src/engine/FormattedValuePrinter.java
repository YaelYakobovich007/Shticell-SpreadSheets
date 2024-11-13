package engine;

import sheet.cell.api.EffectiveValue;

import java.text.NumberFormat;
import java.util.Locale;

public class FormattedValuePrinter {
    public static String formatValue(EffectiveValue value) {
        switch (value.getCellType()) {
            case NUMERIC:
                return formatNumericValue(value);
            case BOOLEAN: {
                Boolean booleanValue = value.extractValueWithExpectation(Boolean.class);
                if (booleanValue == null) {
                    return "UNKNOWN";
                }
                return booleanValue.toString().toUpperCase();
            }
            case STRING:
                String stringValue = value.extractValueWithExpectation(String.class);
                return stringValue.trim();
            case UNKNOWN:
            default:
                return "";
        }
    }

    private static String formatNumericValue(EffectiveValue value) {
        double numericValue = value.extractValueWithExpectation(Double.class);
        if (numericValue % 1 == 0) {
            return NumberFormat.getIntegerInstance(Locale.US).format(numericValue);
        } else {
            return String.format(Locale.US, "%,.2f", numericValue);
        }
    }
}

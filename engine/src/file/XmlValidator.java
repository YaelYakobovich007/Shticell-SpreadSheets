package file;

import exception.InvalidSheetException;
import exception.XmlProcessingException;
import jaxb.schema.STLCell;
import jaxb.schema.STLSheet;
import java.io.File;

public  class XmlValidator {
    public static  void validateLogicXmlFile(STLSheet sheet){
        isCellsWithinSheetBounds(sheet);
        isValidSheetSize(sheet);
    }

    public static void valideXmlFile(String xmlPath){
        isFileExists(xmlPath);
        isXmlFileType(xmlPath);
    }

    private static  int convertColumnToNumber(String column){
        column = column.trim().toUpperCase(); // Normalize input
        return column.charAt(0) - 'A' + 1;
    }

    private static void  isCellsWithinSheetBounds(STLSheet sheet)
    {
        for (STLCell cell : sheet.getSTLCells().getSTLCell()) {
            int row = cell.getRow();
            int column = convertColumnToNumber(cell.getColumn());

            if (row < 1 || row > sheet.getSTLLayout().getRows()){
                throw new InvalidSheetException("The position of the cell is incorrect.\n" +
                        "The row number " + row + " is out of bounds according to the sheet's defined limits.\n" +
                        "Valid row numbers must be between 1 and " + sheet.getSTLLayout().getRows()+ " as per the sheet's layout.\n" +
                        "Please adjust the row number to be within this valid range.");
            }

            if (column < 1 || column > sheet.getSTLLayout().getColumns()) {// קצת מוזר לי שצריך לעשות פה המרה
                throw new InvalidSheetException("The position of the cell is incorrect.\n" +
                        "The column number " + column + " is out of bounds according to the sheet's defined limits.\n" +
                        "Valid row numbers must be between 1 and " + sheet.getSTLLayout().getColumns() +" as per the sheet's layout.\n" +
                        "Please adjust the column number to be within this valid range.");
            }
        }
    }

    private static void isValidSheetSize(STLSheet sheet)
    {
        if (sheet.getSTLLayout().getRows() < 1 || sheet.getSTLLayout().getRows() > 50 || sheet.getSTLLayout().getColumns() < 1 || sheet.getSTLLayout().getColumns() > 20){
            throw new InvalidSheetException("Invalid sheet size detected. The provided sheet size is not within the allowed range.\n" +
                    "Valid sheet size:\n" +
                    "- Rows: between 1 and 50\n" +
                    "- Columns: between 1 and 20\n" +
                    "Please ensure that the sheet conforms to these size constraints.");
        }
    }

    private static void isFileExists(String xmlPath)
    {
        File file = new File(xmlPath.trim());
        if (!file.exists() || !file.isFile()) {
            throw new XmlProcessingException("The provided file does not exist or is not a valid file. Please check the file path: " + xmlPath);
        }
    }

    private static void isXmlFileType(String xmlPath)
    {
        if(!xmlPath.trim().endsWith(".xml")) {
            throw new XmlProcessingException("The provided file is not an XML file.\n " +
                    "The file name does not end with the required '.xml' extension.\n " +
                    "Please ensure that the file you are attempting to load is in the correct XML format.");
        }
    }
}

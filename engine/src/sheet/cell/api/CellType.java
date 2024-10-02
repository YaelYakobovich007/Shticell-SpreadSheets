package sheet.cell.api;

public enum CellType  {
    NUMERIC(Double.class){
        @Override
        public String toString(){
            return "Numerical Expression (number or function returning number)";
        }
    } ,
    STRING(String.class) {
        @Override
        public String toString(){
            return "String Expression (String or function returning string)";
        }
    },
    BOOLEAN(Boolean.class) {
        @Override
        public String toString() {
            return "Boolean Expression (Boolean or function returning Boolean)";
        }
    },
    UNKNOWN(Void.class),

    UNKNOWN_BOOLEAN(Boolean.class);


    private final Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
}

package util;

import com.google.gson.Gson;

public class Constants {
    // global constants
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/web-Shticell_Web";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public static final String SHEETS_LIST_URL = FULL_SERVER_PATH + "/sheetList";
    public static final String SORT_ROWS_URL = FULL_SERVER_PATH +"/sortRows";
    public static final String VALIDATE_BOUNDARIES_URL = FULL_SERVER_PATH+ "/validateBoundaries";
    public static final String FILTER_SHEET_URL = FULL_SERVER_PATH + "/filterSheet";
    public static final String IS_CELL_NUMERIC_URL = FULL_SERVER_PATH+ "/isCellNumeric";
    public static final String DYNAMIC_ANALYSIS = FULL_SERVER_PATH + "/dynamicAnalysis";
    public static final String GET_SHEET_BY_VERSION_URL = FULL_SERVER_PATH+ "/getSheetByVersion";
    public static final String PERMISSION_LIST_URL = FULL_SERVER_PATH + "/permissionList";
    public static final String GET_PENDING_REQUESTS_URL = FULL_SERVER_PATH+ "/getPendingRequestsForOwner";
    public static final String IS_VERSION_UP_TO_DATE = FULL_SERVER_PATH + "/isVersionUpToDate";
    public static final String GET_SHEET_VERSION_COUNT_URL = FULL_SERVER_PATH + "/getSheetVersionCount";
    public static final String CLEAR_SESSION_URL = FULL_SERVER_PATH + "/clear-session";
    public static final String RANGE_OPERATION_URL = FULL_SERVER_PATH + "/range";
    public static final String SHEET_OPERATION_URL = FULL_SERVER_PATH + "/sheet";
    public static final String CELL_OPERATION_URL = FULL_SERVER_PATH + "/cell";
    public static final String UPDATE_CELL_STYLE_URL= FULL_SERVER_PATH+ "/updateCellStyle";
    public static final String PERMISSIONS_OPERATION_URL = FULL_SERVER_PATH + "/permissions";
    public static final String UPDATE_TYPE=  "updateType";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}

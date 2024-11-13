package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static utils.ServletUtils.sendErrorResponse;

public class SessionValidator {

    public static String validateUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        if (usernameFromSession == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Error: User not logged in.");
            return null;
        }
        return usernameFromSession;
    }

    public static String validateSheetName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sheetName = SessionUtils.getCurrentSheetName(request);
        if (sheetName == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: No sheet is selected. Please select a sheet to view first.");
            return null;
        }
        return sheetName;
    }

    public static String validateQueryParam(HttpServletRequest request, HttpServletResponse response, String parameterName) throws IOException {
        String paramValue = request.getParameter(parameterName);
        if (paramValue == null || paramValue.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Missing parameter '" + parameterName + "'.");
            return null;
        }
        return paramValue;
    }

}

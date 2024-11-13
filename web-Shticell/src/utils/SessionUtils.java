package utils;

import constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import static utils.ServletUtils.sendErrorResponse;

public class SessionUtils {
    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }
    public static String getCurrentSheetName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.CURRENT_SHEET_NAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static Integer getCurrentSheetVersion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.CURRENT_SHEET_VERSION) : null;
        return sessionAttribute != null ? (Integer) sessionAttribute : null;
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }

    public static void clearSheetInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.CURRENT_SHEET_NAME);
            session.removeAttribute(Constants.CURRENT_SHEET_VERSION);
        }
    }

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

}

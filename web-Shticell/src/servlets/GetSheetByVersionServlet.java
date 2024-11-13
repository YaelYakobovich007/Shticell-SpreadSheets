package servlets;

import api.Engine;
import constants.Constants;
import engine.SheetDTO;
import ui.DisplayVersionSheetRequestDTO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionValidator;
import java.io.IOException;

public class GetSheetByVersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String usernameFromSession = SessionValidator.validateUsername(request, response);
        if (usernameFromSession == null) {
            return;
        }

        String sheetName = SessionValidator.validateSheetName(request, response);
        if (sheetName == null) {
            return;
        }

        String versionNumberParam = request.getParameter(Constants.VERSION_NUMBER);
        if (versionNumberParam == null) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Missing versionNumber parameter.");
            return;
        }
        Engine engine = ServletUtils.getEngine(getServletContext());
        try {
            int versionNumber = Integer.parseInt(versionNumberParam);
            SheetDTO sheetDTO = engine.getSheetByVersion(new DisplayVersionSheetRequestDTO(versionNumber), sheetName);

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(sheetDTO);
            ServletUtils.sendJsonResponse(response, jsonResponse);
        } catch (NumberFormatException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Invalid versionNumber parameter.");
        } catch (RuntimeException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }
}

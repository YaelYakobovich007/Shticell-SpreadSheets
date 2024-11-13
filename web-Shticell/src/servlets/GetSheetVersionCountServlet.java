package servlets;

import api.Engine;
import com.google.gson.Gson;
import engine.NumOfVersionsDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionValidator;

import java.io.IOException;

public class GetSheetVersionCountServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionValidator.validateUsername(request, response);
        if (usernameFromSession == null) {
            return;
        }

        String sheetName = SessionValidator.validateSheetName(request, response);
        if (sheetName == null) {
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());

        try {
            NumOfVersionsDTO numOfVersionsDTO = engine.getNumOfVersions(sheetName);
            String jsonResponse = gson.toJson(numOfVersionsDTO.getNumOfVersions());
            ServletUtils.sendJsonResponse(response, jsonResponse);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }
}

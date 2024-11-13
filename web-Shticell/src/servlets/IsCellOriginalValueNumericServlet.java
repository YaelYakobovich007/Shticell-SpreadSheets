package servlets;

import api.Engine;
import constants.Constants;
import engine.CellDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ui.IsCellOriginalValueNumericRequestDTO;
import ui.SheetUserIdentifierDTO;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.SessionValidator;
import com.google.gson.Gson;
import java.io.IOException;

public class IsCellOriginalValueNumericServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionValidator.validateUsername(request, response);
        if (usernameFromSession == null) {
            return;
        }

        String sheetName = SessionValidator.validateSheetName(request, response);
        if (sheetName == null) {
            return;
        }

        String cellId = request.getParameter(Constants.CELL_ID);
        if (cellId == null) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Missing 'cellId' parameter.");
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
        try {
            CellDTO cellDTO = engine.isCellOriginalValueNumeric(
                    new IsCellOriginalValueNumericRequestDTO(cellId),
                    new SheetUserIdentifierDTO(sheetName, currentSheetVersion)
            );

            ServletUtils.sendJsonResponse(response, gson.toJson(cellDTO));
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }
}

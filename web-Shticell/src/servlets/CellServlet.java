package servlets;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import constants.Constants;
import engine.CellDTO;
import engine.SheetDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ui.CellUpdateRequestDTO;
import ui.DisplayCellyRequestDTO;
import ui.SheetUserIdentifierDTO;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.SessionValidator;
import java.io.IOException;

public class CellServlet extends HttpServlet {
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
        if (cellId == null || cellId.isEmpty()) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Missing cellId parameter.");
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
        try {
            CellDTO cellDTO = engine.getCellValue(new DisplayCellyRequestDTO(cellId), new SheetUserIdentifierDTO(sheetName, currentSheetVersion));
            ServletUtils.sendJsonResponse(response, gson.toJson(cellDTO));
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            CellUpdateRequestDTO cellUpdateRequestDTO = gson.fromJson(request.getReader(), CellUpdateRequestDTO.class);
            Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
            SheetDTO updatedSheet = engine.updateCellValue(cellUpdateRequestDTO, new SheetUserIdentifierDTO(sheetName, currentSheetVersion, usernameFromSession));
            request.getSession(true).setAttribute(Constants.CURRENT_SHEET_VERSION, updatedSheet.getVersionNumber());
            ServletUtils.sendJsonResponse(response, gson.toJson(updatedSheet));
        } catch (JsonSyntaxException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Malformed JSON input.");
        } catch (IOException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: Unable to read input data.");
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

    }
}

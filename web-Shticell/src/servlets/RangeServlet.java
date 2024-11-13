package servlets;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import engine.RangeDTO;
import engine.SheetDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ui.AddNewRangeRequestDTO;
import ui.DeleteRangeRequestDTO;
import ui.SheetUserIdentifierDTO;
import ui.ViewRangeRequestDTO;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.SessionValidator;

import java.io.IOException;

import static constants.Constants.RANGE_NAME;

public class RangeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String username = SessionValidator.validateUsername(request, response);
        String sheetName = SessionValidator.validateSheetName(request, response);
        Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
        if (username == null || sheetName == null) {
            return;
        }

        String rangeName = request.getParameter(RANGE_NAME);
        if (rangeName == null || rangeName.isEmpty()) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Range name is required.");
            return;
        }

        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            RangeDTO selectedRange = engine.viewRange(new ViewRangeRequestDTO(rangeName), new SheetUserIdentifierDTO(sheetName, currentSheetVersion, username));
            ServletUtils.sendJsonResponse(response,  gson.toJson(selectedRange));
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String username = SessionValidator.validateUsername(request, response);
        String sheetName = SessionValidator.validateSheetName(request, response);
        Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
        if (username == null || sheetName == null) {
            return;
        }
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            AddNewRangeRequestDTO addNewRangeRequestDTO = gson.fromJson(request.getReader(), AddNewRangeRequestDTO.class);
            SheetDTO updatedSheet = engine.createNewRange(addNewRangeRequestDTO, new SheetUserIdentifierDTO(sheetName, currentSheetVersion, username));
            ServletUtils.sendJsonResponse(response, gson.toJson(updatedSheet));
        } catch (JsonSyntaxException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Malformed JSON input.");
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String username = SessionValidator.validateUsername(request, response);
        String sheetName = SessionValidator.validateSheetName(request, response);
        Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
        if (username == null || sheetName == null) {
            return;
        }

        String rangeName = request.getParameter(RANGE_NAME);
        if (rangeName == null || rangeName.isEmpty()) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Range name is required.");
            return;
        }
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            SheetDTO updatedSheet = engine.deleteRange(new DeleteRangeRequestDTO(rangeName), new SheetUserIdentifierDTO(sheetName, currentSheetVersion, username));
            ServletUtils.sendJsonResponse(response, gson.toJson(updatedSheet));
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "error: " + e.getMessage());
        }
    }
}

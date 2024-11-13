package servlets;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import constants.Constants;
import engine.SheetDTO;
import engine.UniqueValuesForColumnDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ui.FilterRequestDTO;
import ui.SheetUserIdentifierDTO;
import ui.UniqueValuesForColumnRequestDTO;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.SessionValidator;
import java.io.IOException;

public class FilterServlet extends HttpServlet {
    private final Gson gson = new Gson();

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
        try {
            FilterRequestDTO filterRequestDTO = gson.fromJson(request.getReader(), FilterRequestDTO.class);
            Engine engine = ServletUtils.getEngine(getServletContext());
            Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
            SheetDTO filteredSheet = engine.filterSheet(filterRequestDTO, new SheetUserIdentifierDTO(sheetName,currentSheetVersion));
            ServletUtils.sendJsonResponse(response, gson.toJson(filteredSheet));
        } catch (JsonSyntaxException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Malformed JSON input.");
        }catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String sheetName = SessionValidator.validateSheetName(request, response);
        if (sheetName == null) {
            return;
        }

        String column = request.getParameter(Constants.COLUMN);
        String fromCoordinate = request.getParameter(Constants.FROM_COORDINATE);
        String toCoordinate = request.getParameter(Constants.TO_COORDINATE);

        if (column == null || fromCoordinate == null || toCoordinate == null) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Missing parameters.");
            return;
        }

        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);

            UniqueValuesForColumnDTO uniqueValues = engine.getUniqueValuesForColumn(
                    new UniqueValuesForColumnRequestDTO(column.charAt(0), fromCoordinate, toCoordinate),
                    new SheetUserIdentifierDTO(sheetName,currentSheetVersion)
            );
            ServletUtils.sendJsonResponse(response, gson.toJson(uniqueValues));
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }
}

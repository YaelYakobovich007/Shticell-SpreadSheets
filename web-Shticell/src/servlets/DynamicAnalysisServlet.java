package servlets;

import api.Engine;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import engine.SheetDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ui.SheetUserIdentifierDTO;
import ui.UpdateTemporaryValuesInSheetRequestDTO;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.SessionValidator;

import java.io.IOException;

public class DynamicAnalysisServlet extends HttpServlet {
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

        Engine engine = ServletUtils.getEngine(getServletContext());
        Integer currentSheetVersion = SessionUtils.getCurrentSheetVersion(request);
        try {
        UpdateTemporaryValuesInSheetRequestDTO updateRequestDTO = gson.fromJson(request.getReader(), UpdateTemporaryValuesInSheetRequestDTO.class);
            SheetDTO updatedSheet = engine.updateTemporaryCellValue(updateRequestDTO, new SheetUserIdentifierDTO(sheetName, currentSheetVersion));
            ServletUtils.sendJsonResponse(response, gson.toJson(updatedSheet));
        } catch (JsonSyntaxException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Malformed JSON input.");
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }
}

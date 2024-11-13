package servlets;

import api.Engine;
import ui.UpdateCellStyleRequestDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import com.google.gson.Gson;
import utils.SessionValidator;
import java.io.IOException;
import static constants.Constants.UPDATE_TYPE;

public class UpdateCellStyleServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String userName= SessionValidator.validateUsername(request,response);
        if(userName==null){
            return;
        }
        String sheetName= SessionValidator.validateSheetName(request,response);
        if(sheetName==null){
            return;
        }
        Engine engine = ServletUtils.getEngine(getServletContext());
        UpdateCellStyleRequestDTO updateRequest = parseUpdateRequest(request, response);
        if (updateRequest == null) {
            return;
        }

        String updateType = request.getParameter(UPDATE_TYPE);
        if (updateType == null || updateType.isEmpty()) {
            ServletUtils.sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST, "Error: Missing updateType parameter.");
            return;
        }

        try {
            if (updateType.equalsIgnoreCase("background")) {
                engine.updateCellBackgroundColor(updateRequest, sheetName);
            } else if (updateType.equalsIgnoreCase("text")) {
                engine.updateCellTextColor(updateRequest, sheetName);
            } else {
                ServletUtils.sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST, "Error: Invalid updateType parameter. Must be 'background' or 'text'.");
                return;
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST, "error: " + e.getMessage());
        }
    }
    private UpdateCellStyleRequestDTO parseUpdateRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            return gson.fromJson(request.getReader(), UpdateCellStyleRequestDTO.class);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,"Error parsing request body: " + e.getMessage());
            return null;
        }
    }

}

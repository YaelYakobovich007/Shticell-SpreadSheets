package servlets;

import api.Engine;
import com.google.gson.JsonSyntaxException;
import engine.SheetDTO;
import ui.SheetUserIdentifierDTO;
import ui.SortRequestDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import com.google.gson.Gson;
import utils.SessionValidator;
import java.io.IOException;

public class SortRowsServlet extends HttpServlet {
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
        Integer currentSheetVersion= SessionUtils.getCurrentSheetVersion(request);
        Gson gson = new Gson();

        try {
            SortRequestDTO sortRequestDTO = gson.fromJson(request.getReader(), SortRequestDTO.class);
            SheetDTO sortedSheet = engine.rowSorting(sortRequestDTO, new SheetUserIdentifierDTO(sheetName, currentSheetVersion));
            ServletUtils.sendJsonResponse(response, gson.toJson(sortedSheet));
        } catch (JsonSyntaxException e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Malformed JSON input.");
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
        }
    }
}

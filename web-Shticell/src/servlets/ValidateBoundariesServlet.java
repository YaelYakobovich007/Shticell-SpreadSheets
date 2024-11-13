package servlets;

import api.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionValidator;
import java.io.IOException;
import java.io.PrintWriter;

public class ValidateBoundariesServlet extends HttpServlet {
    @Override
    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String userName= SessionValidator.validateUsername(request,response);
        if(userName==null){
            return;
        }
        String sheetName= SessionValidator.validateSheetName(request,response);
        if(sheetName==null){
            return;
        }
        String from =SessionValidator.validateQueryParam(request,response, "from");
        String to =SessionValidator.validateQueryParam(request,response, "from");
        if (from == null || to == null) {
            return;
        }
        Engine engine = ServletUtils.getEngine(getServletContext());
        try {
            boolean isValid = engine.isValidBoundaries(from, to, sheetName);
            response.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter out = response.getWriter()){
                out.print(isValid);
                out.flush();
            }
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}

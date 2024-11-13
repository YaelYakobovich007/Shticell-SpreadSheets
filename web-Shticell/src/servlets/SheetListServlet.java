package servlets;

import api.Engine;
import engine.SheetDetailsDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;
import utils.SessionUtils;
import utils.SessionValidator;


public class SheetListServlet extends HttpServlet {
    @Override
    public   void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String usernameFromSession = SessionValidator.validateUsername(request, response);
        if (usernameFromSession == null) {
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());
            List<SheetDetailsDTO> sheetDetailsList = engine.getSheetDetailsList(usernameFromSession);
            String json = gson.toJson(sheetDetailsList);
            out.println(json);
            out.flush();
        }

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Engine engine = ServletUtils.getEngine(getServletContext());
            List<SheetDetailsDTO> sheetDetailsList = engine.getSheetDetailsList(usernameFromSession);

            if (sheetDetailsList == null) {
                sheetDetailsList = Collections.emptyList();
            }
            String json = gson.toJson(sheetDetailsList);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error: Unable to fetch sheets list."+ e.getMessage());
        }
    }
}

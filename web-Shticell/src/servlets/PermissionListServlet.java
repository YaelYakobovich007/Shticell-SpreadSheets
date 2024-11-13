package servlets;

import api.Engine;
import com.google.gson.Gson;
import engine.PermissionRequestDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PermissionListServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String ownerUsername = SessionValidator.validateUsername(request, response);
        if (ownerUsername == null) {
            return;
        }
        String sheetName = request.getParameter("sheetName");
        if (sheetName == null || sheetName.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.println("Error: Missing sheetName parameter.");
                out.flush();
            }
            return;
        }
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            List<PermissionRequestDTO> permissionRequests = engine.getPermissionRequestsForSheet(sheetName);
            String jsonResponse = gson.toJson(permissionRequests);
            ServletUtils.sendJsonResponse(response, jsonResponse);
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Unable to fetch permission requests. " + e.getMessage());
        }
    }
}

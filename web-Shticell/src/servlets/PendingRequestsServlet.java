package servlets;

import api.Engine;
import engine.PermissionRequestDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionValidator;

import java.io.IOException;
import java.util.List;

public class PendingRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String ownerUsername = SessionValidator.validateUsername(request, response);
        if (ownerUsername == null) {
            return;
        }

        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            List<PermissionRequestDTO> pendingRequests = engine.getPendingRequestsForOwner(ownerUsername);

            String jsonResponse = new com.google.gson.Gson().toJson(pendingRequests);
            ServletUtils.sendJsonResponse(response, jsonResponse);

        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}

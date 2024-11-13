package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;
import java.io.IOException;

public class ClearSessionServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUtils.clearSheetInfo(request);
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("Session has been cleared");
        response.getWriter().println("Session cleared successfully.");
    }
}

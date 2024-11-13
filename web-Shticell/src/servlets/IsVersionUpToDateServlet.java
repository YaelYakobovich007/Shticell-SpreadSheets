package servlets;

import api.Engine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.SessionValidator;
import java.io.IOException;
import java.io.PrintWriter;

public class IsVersionUpToDateServlet extends HttpServlet {
    @Override
    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

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
        int latestVersionNumber = engine.getNumOfVersions(sheetName).getNumOfVersions();
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_OK);
            if (currentSheetVersion == latestVersionNumber) {
                out.println("true");
            } else {
                out.println("false");
            }
            out.flush();
        }
    }
}

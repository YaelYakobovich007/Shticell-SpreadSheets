package servlets;

import api.Engine;
import constants.Constants;
import engine.SheetDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ui.LoadRequestDTO;
import utils.ServletUtils;
import utils.SessionUtils;
import com.google.gson.Gson;
import utils.SessionValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import static constants.Constants.SHEET_NAME;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class SheetServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        if (!validateUserSession(request, response)) {
            return;
        }
        String sheetName= SessionValidator.validateQueryParam(request,response,SHEET_NAME);
        if (sheetName== null) {
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        handleGetSheetRequest(request, response, engine, sheetName);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");

        if (!validateUserSession(request, response)) {
            return;
        }

        Collection<Part> parts = request.getParts();
        if (!validateFileParts(parts, response)) {
            return;
        }

        Part filePart = parts.iterator().next();
        if (!validateFileContentType(filePart, response)) {
            return;
        }

        handleFileUpload(filePart, request, response);
    }

    private boolean validateUserSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        if (usernameFromSession == null) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Error: User not logged in.");
            return false;
        }
        return true;
    }


    private boolean validateFileParts(Collection<Part> parts, HttpServletResponse response) throws IOException {
        if (parts.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: No file part in the request.");
            return false;
        }
        return true;
    }

    private boolean validateFileContentType(Part filePart, HttpServletResponse response) throws IOException {
        if (!isSupportedContentType(filePart.getContentType())) {
            sendErrorResponse(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Error: Only XML files are supported.");
            return false;
        }
        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType != null && (
                contentType.equals("text/xml") ||
                        contentType.equals("application/xml") ||
                        contentType.equals("application/octet-stream")
        );
    }

    private void handleGetSheetRequest(HttpServletRequest request, HttpServletResponse response, Engine engine, String sheetName) throws IOException {
        try {
            getSheetByNameAndUpdateSession(request, response, engine, sheetName);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    private void getSheetByNameAndUpdateSession(HttpServletRequest request, HttpServletResponse response, Engine engine, String sheetName) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        SheetDTO sheetDTO = engine.getSheetByName(sheetName, usernameFromSession);
        updateSessionWithSheetInfo(request, sheetName, sheetDTO);
        sendJsonResponse(response, HttpServletResponse.SC_OK, gson.toJson(sheetDTO));
    }

    private void updateSessionWithSheetInfo(HttpServletRequest request, String sheetName, SheetDTO sheetDTO) {
        request.getSession(true).setAttribute(Constants.CURRENT_SHEET_NAME, sheetName);
        request.getSession(true).setAttribute(Constants.CURRENT_SHEET_VERSION, sheetDTO.getVersionNumber());
    }

    private void handleFileUpload(Part filePart, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            processFileUpload(filePart, request);
            sendSuccessResponse(response, "File uploaded successfully.");
        } catch (RuntimeException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing file: " + e.getMessage());
        }
    }

    private void processFileUpload(Part filePart, HttpServletRequest request) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        Engine engine = ServletUtils.getEngine(getServletContext());
        engine.loadSheetFromFile(new LoadRequestDTO(filePart.getInputStream(), usernameFromSession));
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.println(message);
            out.flush();
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        try (PrintWriter out = response.getWriter()) {
            out.println(message);
            out.flush();
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, String json) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter out = response.getWriter()) {
            out.println(json);
            out.flush();
        }
    }
}

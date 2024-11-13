package servlets;

import api.Engine;
import com.google.gson.Gson;
import engine.PermissionRequestDTO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ui.SheetPermissionRequestDTO;
import utils.ServletUtils;
import utils.SessionValidator;

import java.io.BufferedReader;
import java.io.IOException;

import static constants.Constants.*;

public class PermissionServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String usernameFromSession = SessionValidator.validateUsername(request, response);
        if (usernameFromSession == null) {
            return;
        }
        String sheetName = SessionValidator.validateQueryParam(request, response, SHEET_NAME);
        if (sheetName == null) {
            return;
        }

        String permissionType = SessionValidator.validateQueryParam(request, response, PERMISSION_TYPE);
        if (permissionType == null) {
            return;
        }
        try {
            SheetPermissionRequestDTO permissionRequest = new SheetPermissionRequestDTO(usernameFromSession, sheetName, permissionType);
            Engine engine = ServletUtils.getEngine(getServletContext());
            engine.askForSheetPermission(permissionRequest);

            ServletUtils.sendJsonResponse(response, "Permission request submitted successfully.");
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String ownerUsername = SessionValidator.validateUsername(request, response);
        if (ownerUsername == null) {
            return;
        }
        String statusParam = SessionValidator.validateQueryParam(request, response, STATUS);
        if (statusParam == null) {
            return;
        }
        boolean isApproved;
        if (statusParam.equalsIgnoreCase(STATUS_APPROVED)) {
            isApproved = true;
        } else if (statusParam.equalsIgnoreCase(STATUS_REJECTED)) {
            isApproved = false;
        } else {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid 'status' parameter value");
            return;
        }

        BufferedReader reader = request.getReader();
        PermissionRequestDTO requestDTO = gson.fromJson(reader, PermissionRequestDTO.class);
        if (requestDTO == null || requestDTO.getSheetName() == null) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error: Missing or invalid request body.");
            return;
        }

        Engine engine = ServletUtils.getEngine(getServletContext());
        try {
            engine.approveOrDenyPermissionRequest(requestDTO, ownerUsername, requestDTO.getSheetName(), isApproved);
            ServletUtils.sendJsonResponse(response, "Request processed successfully");
        } catch (Exception e) {
            ServletUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}

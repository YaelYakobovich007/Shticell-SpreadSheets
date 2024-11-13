package sheet.impl;

import engine.PermissionRequestDTO;
import permission.PermissionManager;
import permission.PermissionType;
import sheet.api.Sheet;
import sheet.version.impl.VersionManager;
import ui.SheetPermissionRequestDTO;
import java.util.List;

public class SheetRecord {
    private Sheet sheet;
    private final VersionManager versionManager;
    private final PermissionManager permissionManager;

    public SheetRecord(Sheet sheet, VersionManager versionManager, String ownerName) {
        this.sheet = sheet;
        this.versionManager = versionManager;
        this.permissionManager = new PermissionManager(ownerName);
    }

    public Sheet getSheet() {
        return sheet;
    }

    public  void setSheet(Sheet sheet) {
        this.sheet= sheet;
    }
    public VersionManager getVersionManager() {
        return versionManager;
    }

    public void approveOrDenyRequest(PermissionRequestDTO requestDTO, String approverUsername, boolean isApproved) {
        if (approverUsername.equals(permissionManager.getOwner())) {
            permissionManager.processRequest(requestDTO, isApproved);
        } else {
            throw new RuntimeException("Only the owner can approve or deny requests.");
        }
    }

    public PermissionType getUserPermission(String username) {
        return permissionManager.getUserPermission(username);
    }

    public String getOwner() {
        return permissionManager.getOwner();
    }

    public List<PermissionRequestDTO> getPendingRequests(String ownerUsername) {
        if (ownerUsername.equals(permissionManager.getOwner())) {
            return permissionManager.getPendingRequestsForOwner(ownerUsername);
        } else {
            throw new RuntimeException("Only the owner can view pending requests.");
        }
    }

    public List<PermissionRequestDTO> getAllRequests() {
        return permissionManager.getAllRequests();
    }

    public void askForSheetPermission(SheetPermissionRequestDTO requestDTO) {
        permissionManager.addRequest(requestDTO, sheet.getName());
    }
}

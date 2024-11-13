package engine;

import permission.PermissionRequest;

public class PermissionRequestDTO {
    private final String id;
    private final String username;
    private final String requestType;
    private final String status;
    private final String sheetName;


    public PermissionRequestDTO(PermissionRequest permissionRequest) {
        this.id=permissionRequest.getId();
        this.username=permissionRequest.getUsername();
        this.status=permissionRequest.getStatus().toString();
        this.requestType=permissionRequest.getPermissionType().toString();
        this.sheetName=permissionRequest.getSheetName();
    }
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getStatus() {
        return status;
    }
    public String getSheetName() {
        return sheetName;
    }



}

package permission;

import engine.PermissionRequestDTO;
import java.util.UUID;

public class PermissionRequest {
    private String id;
    private String username;
    private PermissionRequestStatus status;
    private PermissionType permissionType;
    private  String sheetName;

    public PermissionRequest(String username,PermissionType permissionType, String sheetName) {
        this.username = username;
        this.permissionType = permissionType;
        this.sheetName = sheetName;
        this.status= PermissionRequestStatus.PENDING;
        this.id= UUID.randomUUID().toString();
    }

    public  PermissionRequest(String ownerName){
        this.username = ownerName;
        this.permissionType = PermissionType.OWNER;
        this.status= PermissionRequestStatus.APPROVED;
        this.id= UUID.randomUUID().toString();
    }

    public PermissionRequestDTO toDTO() {
        return new PermissionRequestDTO(this);
    }

    public String getUsername() {
        return username;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public String getId() {
        return id;
    }

    public PermissionRequestStatus getStatus() {
        return status;
    }
    public String getSheetName() {
        return sheetName;
    }

    public void setStatus(PermissionRequestStatus status) {
        this.status = status;
    }
}



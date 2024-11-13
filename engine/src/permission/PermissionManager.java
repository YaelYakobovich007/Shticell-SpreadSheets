package permission;

import engine.PermissionRequestDTO;
import ui.SheetPermissionRequestDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PermissionManager {
    private final String ownerName;
    private List<PermissionRequest> requests;
    private Map<String, PermissionType> userPermissions;

    public PermissionManager(String ownerName) {
        this.ownerName = ownerName;
        userPermissions = new HashMap<>();
        requests = new ArrayList<>();
        userPermissions.put(ownerName, PermissionType.OWNER);
        requests.add(new PermissionRequest(ownerName));
    }

    public String getOwner() {
       return ownerName;
    }

    public List<PermissionRequestDTO> getAllRequests() {
        return requests.stream().map(PermissionRequest::toDTO).collect(Collectors.toList());
    }

    public void addRequest(SheetPermissionRequestDTO requestDTO, String sheetName) {
        if (requestDTO.getUsername().equals(ownerName)) {
            throw new RuntimeException("The owner cannot request permissions on their own sheet.");
        }
        requests.add(new PermissionRequest(requestDTO.getUsername(), PermissionType.valueOf(requestDTO.getPermissionType().toUpperCase()), sheetName));
    }


    public void processRequest(PermissionRequestDTO requestDTO, boolean isApproved) {
        PermissionRequest request = findRequestById(requestDTO.getId());
        if (request != null) {
            if (isApproved) {
                request.setStatus(PermissionRequestStatus.APPROVED);
                setActivePermission(request);
            } else {
                request.setStatus(PermissionRequestStatus.REJECTED);
            }
        }
    }

    private void setActivePermission(PermissionRequest approvedRequest) {
        String username = approvedRequest.getUsername();
        PermissionType newPermission = approvedRequest.getPermissionType();

        userPermissions.put(username, newPermission);
    }

    private PermissionRequest findRequestById(String requestId) {
        return requests.stream().filter(req -> req.getId().equals(requestId)).findFirst().orElse(null);
    }

    public PermissionType getUserPermission(String username) {
        return userPermissions.getOrDefault(username, PermissionType.NONE);
    }

    public List<PermissionRequestDTO> getPendingRequestsForOwner(String ownerUsername) {
        return requests.stream()
                .filter(req -> req.getStatus() == PermissionRequestStatus.PENDING && ownerUsername.equals(ownerName))
                .map(PermissionRequest::toDTO)
                .collect(Collectors.toList());
    }
}

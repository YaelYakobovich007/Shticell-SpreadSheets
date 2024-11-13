package permission;

public enum PermissionRequestStatus {
    PENDING {
        @Override
        public String toString() {
            return "PENDING";
        }
    },
    APPROVED {
        @Override
        public String toString() {
            return "APPROVED";
        }
    },
    REJECTED{
        @Override
        public String toString() {
            return "REJECTED";
        }
    }
}

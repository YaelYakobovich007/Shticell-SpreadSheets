package permission;

public enum PermissionType {
    OWNER {
        @Override
        public String toString() {
            return "Owner";
        }
    },
    READER {
        @Override
        public String toString() {
            return "Reader";
        }
    },
    WRITER {
        @Override
        public String toString() {
            return "Writer";
        }
    },
    NONE {
        @Override
        public String toString() {
            return "None";
        }
    }
}

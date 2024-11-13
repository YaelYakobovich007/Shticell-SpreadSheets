package ui;

import java.util.Map;

public class UpdateTemporaryValuesInSheetRequestDTO {
    private final Map<String, String> cellUpdates;

    public UpdateTemporaryValuesInSheetRequestDTO(Map<String, String> cellUpdates) {
        this.cellUpdates = cellUpdates;
    }

    public Map<String, String> getCellUpdates() {
        return cellUpdates;
    }

}

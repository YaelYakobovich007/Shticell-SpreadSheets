package engine;

import sheet.version.api.Version;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VersionChangesDTO {

    private Map<Integer, Integer> changedCellsPerVersion;

    public VersionChangesDTO(Collection<Version> versions){

        this.changedCellsPerVersion = new HashMap<>();
        versions.forEach(version -> {
            int versionNumber = version.getVersionNumber();
            int changedCellsCount = version.getChangedCellsCount();
            this.changedCellsPerVersion.put(versionNumber, changedCellsCount);
        });
    }

    public Map<Integer, Integer> getChangedCellsPerVersion() {
        return changedCellsPerVersion;
    }
}

package sheet.version.impl;
import sheet.api.Sheet;
import sheet.version.api.Version;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VersionManager implements Serializable {
    private final Map<Integer, Version> sheetVersions;

    public VersionManager() {
        sheetVersions = new HashMap<>();
    }

    public void addVersion(int versionNumber, int changedCellsCount, Sheet sheet ) {
        Version version= new VersionImpl(versionNumber, changedCellsCount, sheet);
        sheetVersions.put(version.getVersionNumber(), version);
    }

    public Version getVersionDetails(int versionNumber) {
        if (sheetVersions.containsKey(versionNumber)){
            return sheetVersions.get(versionNumber);
        }
        else{
            throw  new IllegalArgumentException("Version number " + versionNumber + " not found");
        }
    }

    public Collection<Version> getVersions() {
         return sheetVersions.values();
    }

    public Integer getNumOfVersions() {
        return sheetVersions.size();
    }

}

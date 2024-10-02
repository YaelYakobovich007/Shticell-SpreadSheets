package sheet.version.impl;

import sheet.api.Sheet;
import sheet.version.api.Version;

public class VersionImpl implements Version {
    private final int versionNumber;
    private final Sheet sheet;
    private final int changedCellsCount;

    public VersionImpl(int versionNumber, int changedCellsCount, Sheet sheet){
        this.versionNumber = versionNumber;
        this.sheet = sheet;
        this.changedCellsCount = changedCellsCount;
    }

    @Override
    public int getVersionNumber() {
        return versionNumber;
    }

    @Override
    public int getChangedCellsCount() {
        return changedCellsCount;
    }

    @Override
    public Sheet getSheet() {
        return sheet;
    }
}

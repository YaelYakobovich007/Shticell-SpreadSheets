package sheet.impl;
import engine.*;
import parse.StringValidators;
import permission.PermissionType;
import range.Boundaries;
import range.Range;
import sheet.api.Layout;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;
import sheet.version.impl.VersionManager;
import ui.SheetPermissionRequestDTO;
import ui.UpdateTemporaryValuesInSheetRequestDTO;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SheetsManager {
    private final Map<String, SheetRecord> sheets;
    private final Map<String, ReentrantReadWriteLock> sheetLocks;
    private final ReentrantReadWriteLock globalLock;

    public SheetsManager() {
        sheets = new HashMap<>();
        sheetLocks = new HashMap<>();
        globalLock = new ReentrantReadWriteLock();
    }

    public void addSheet(String sheetName, SheetRecord sheetRecord) {
        globalLock.writeLock().lock();
        try {
            if (sheets.containsKey(sheetName)) {
                throw new RuntimeException("Sheet name must be unique");
            }
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            sheetLocks.put(sheetName, lock);
            sheets.put(sheetName, sheetRecord);
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public SheetRecord getSheetRecord(String sheetName) {
        globalLock.readLock().lock();
        try {
            SheetRecord sheetRecord = sheets.get(sheetName);
            if (sheetRecord == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            return sheetRecord;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public SheetDTO updateCellValue(String sheetName, int row, int column, String newValue, String updatedByUser) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SheetRecord currentSheetRecord = sheets.get(sheetName);
            CoordinateParser.validateCoordinates(row, column, currentSheetRecord.getSheet().getLayout());
            currentSheetRecord.setSheet(currentSheetRecord.getSheet().updateCellValueAndCalculate(row, column, newValue, updatedByUser));
            currentSheetRecord.getVersionManager().addVersion(currentSheetRecord.getSheet().getVersionNumber(), currentSheetRecord.getSheet().getChangedCellsCount(), currentSheetRecord.getSheet());
            return new SheetDTO(currentSheetRecord.getSheet());
        } finally {
            writeLock.unlock();
        }
    }

    public CellDTO getCellValue(String sheetName, int row, int column, int sheetVersionNumber) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SheetRecord currentSheetRecord = sheets.get(sheetName);
            CoordinateParser.validateCoordinates(row, column, currentSheetRecord.getSheet().getLayout());
            Cell cell = currentSheetRecord.getVersionManager().getVersionDetails(sheetVersionNumber).getSheet().getCell(row, column);
            return CellDTO.createCellDTO(cell);
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO getSheetByName(String sheetName, String username) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SheetRecord sheetRecord = sheets.get(sheetName);
            return new SheetDTO(sheetRecord.getSheet(), sheetRecord.getUserPermission(username).toString());
        } finally {
            readLock.unlock();
        }
    }

    public PermissionType getUserPermission(String sheetName, String username) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            return sheets.get(sheetName).getUserPermission(username);
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO getSheetByVersion(String sheetName, int versionNumber) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            VersionManager currentVersionManager = sheets.get(sheetName).getVersionManager();
            return new SheetDTO(currentVersionManager.getVersionDetails(versionNumber).getSheet());
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO createNewRange(String sheetName, String rangeName, String from, String to,int currentVersion) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SheetRecord currentSheetRecord = sheets.get(sheetName);
            Sheet updatedSheet = currentSheetRecord.getVersionManager().getVersionDetails(currentVersion).getSheet().createNewRange(rangeName, from, to);
            currentSheetRecord.getVersionManager().getVersionDetails(currentVersion).updateSheet(updatedSheet);
            return new SheetDTO(updatedSheet);
        } finally {
            writeLock.unlock();
        }
    }

    public SheetDTO deleteRange(String sheetName, String rangeName, int currentVersion) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SheetRecord currentSheetRecord = sheets.get(sheetName);
            Sheet updatedSheet = currentSheetRecord.getVersionManager().getVersionDetails(currentVersion).getSheet().deleteRange(rangeName);
            currentSheetRecord.getVersionManager().getVersionDetails(currentVersion).updateSheet(updatedSheet);
            return new SheetDTO(updatedSheet);
        } finally {
            writeLock.unlock();
        }
    }

    public RangeDTO viewRange(String sheetName, String rangeName, int currentVersion) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Range range = sheets.get(sheetName).getVersionManager().getVersionDetails(currentVersion).getSheet().getRange(rangeName);
            return new RangeDTO(range);
        } finally {
            readLock.unlock();
        }
    }

    public Layout getSheetLayout(String sheetName) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            return sheets.get(sheetName).getSheet().getLayout();
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO rowSorting(String sheetName, int versionNumber, Coordinate fromCoordinate, Coordinate toCoordinate, List<Character> selectedColumns) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet sheet = sheets.get(sheetName).getVersionManager().getVersionDetails(versionNumber).getSheet();
            Sheet sortedSheet = sheet.rowSorting(fromCoordinate, toCoordinate, selectedColumns);
            return new SheetDTO(sortedSheet);
        } finally {
            readLock.unlock();
        }
    }

    public void updateCellTextColor(String sheetName, Coordinate cellCoordinate, String newColor) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            Sheet currentSheet = sheets.get(sheetName).getSheet();
            currentSheet.setTextColor(cellCoordinate, newColor);
        } finally {
            writeLock.unlock();
        }
    }

    public void updateCellBackgroundColor(String sheetName, Coordinate cellCoordinate, String newColor) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            Sheet currentSheet = sheets.get(sheetName).getSheet();
            currentSheet.setBackgroundColor(cellCoordinate, newColor);
        } finally {
            writeLock.unlock();
        }
    }

    public UniqueValuesForColumnDTO getUniqueValuesForColumn(String sheetName, int sheetVersionNumber,  Coordinate from, Coordinate to, Character targetColumn) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet currentSheet = sheets.get(sheetName).getVersionManager().getVersionDetails(sheetVersionNumber).getSheet();
            return new UniqueValuesForColumnDTO(currentSheet.getUniqueValuesForColumn(targetColumn, from, to));
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO filterSheet(String sheetName,int versionNumber, Boundaries boundaries, Map<Character, List<String>> selectedColumnsToUniqueValues) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet sheet = sheets.get(sheetName).getVersionManager().getVersionDetails(versionNumber).getSheet();
            Sheet filteredSheet = sheet.filtering(boundaries, selectedColumnsToUniqueValues);
            return new SheetDTO(filteredSheet);
        } finally {
            readLock.unlock();
        }
    }

    public NumOfVersionsDTO getNumOfVersions(String sheetName) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SheetRecord sheetRecord = sheets.get(sheetName);
            return new NumOfVersionsDTO(sheetRecord.getVersionManager().getNumOfVersions());
        } finally {
            readLock.unlock();
        }
    }

    public CellDTO isCellOriginalValueNumeric(String sheetName,int versionNumber, Coordinate coordinate) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            Sheet currentSheet = sheets.get(sheetName).getVersionManager().getVersionDetails(versionNumber).getSheet();
            Cell cell = currentSheet.getCell(coordinate.getRow(), coordinate.getColumn());
            if (cell != null) {
                if (StringValidators.isNumber(cell.getOriginalValue())) {
                    return CellDTO.createCellDTO(cell);
                } else {
                    throw new RuntimeException("The selected cell must have an original value that is a numerical value.");
                }
            } else {
                throw new RuntimeException("The selected cell is empty.");
            }
        } finally {
            readLock.unlock();
        }
    }

    public SheetDTO updateTemporaryCellValue(String sheetName, int versionNumber,UpdateTemporaryValuesInSheetRequestDTO updateRequestDTO) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();

        try {
            SheetRecord currentSheetRecord = sheets.get(sheetName);
            Sheet copySheet = currentSheetRecord.getVersionManager().getVersionDetails(versionNumber).getSheet().copySheet();
            Layout sheetLayout = copySheet.getLayout();

            for (Map.Entry<String, String> entry : updateRequestDTO.getCellUpdates().entrySet()) {
                String cellId = entry.getKey();
                String newValue = entry.getValue();
                int[] parsedCoordinate = CoordinateParser.parseCoordinate(cellId);
                CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1], sheetLayout);
                Coordinate coordinate = CoordinateFactory.createCoordinate(parsedCoordinate[0], parsedCoordinate[1]);
                copySheet = copySheet.updateTemporaryCellValue(coordinate.getRow(), coordinate.getColumn(), newValue);
            }
            return new SheetDTO(copySheet);

        } finally {
            writeLock.unlock();
        }
    }

    public List<SheetDetailsDTO> getSheetDetailsList(String userName) {
        List<SheetDetailsDTO> sheetDetailsList = new ArrayList<>();
        for (Map.Entry<String, SheetRecord> entry : sheets.entrySet()) {
            String sheetName = entry.getKey();
            ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
            readLock.lock();
            try {
                SheetRecord sheetRecord = entry.getValue();
                String uploadedByUser = sheetRecord.getOwner();
                String sheetSize = sheetRecord.getSheet().getLayout().getNumOfRows() + "X" + sheetRecord.getSheet().getLayout().getNumOfColumns();
                String userPermission = sheetRecord.getUserPermission(userName).toString();

                SheetDetailsDTO sheetDetails = new SheetDetailsDTO(uploadedByUser, sheetName, sheetSize, userPermission);
                sheetDetailsList.add(sheetDetails);
            } finally {
                readLock.unlock();
            }
        }
        return sheetDetailsList;
    }

    public List<PermissionRequestDTO> getPendingRequestsForOwner(String ownerUsername) {
        List<PermissionRequestDTO> pendingRequests = new ArrayList<>();

        for (Map.Entry<String, SheetRecord> entry : sheets.entrySet()) {
            String sheetName = entry.getKey();
            ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
            readLock.lock();
            try {
                SheetRecord sheetRecord = entry.getValue();
                if (sheetRecord.getOwner().equals(ownerUsername)) {
                    pendingRequests.addAll(sheetRecord.getPendingRequests(ownerUsername));
                }
            } finally {
                readLock.unlock();
            }
        }
        return pendingRequests;
    }

    public void askForSheetPermission(SheetPermissionRequestDTO requestDTO) {
        String sheetName = requestDTO.getSheetName();
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SheetRecord sheetRecord = getSheetRecord(sheetName);
            if (sheetRecord == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            sheetRecord.askForSheetPermission(requestDTO);
        } finally {
            writeLock.unlock();
        }
    }

    public int getLatestVersionNumber(String sheetName) {
        ReentrantReadWriteLock.ReadLock readLock = sheetLocks.get(sheetName).readLock();
        readLock.lock();
        try {
            SheetRecord sheetRecord = getSheetRecord(sheetName);
            if (sheetRecord == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            return sheetRecord.getVersionManager().getNumOfVersions();
        } finally {
            readLock.unlock();
        }
    }

    public void approveOrDenyPermissionRequest(String sheetName, PermissionRequestDTO requestDTO, String ownerUserName, boolean isApproved) {
        ReentrantReadWriteLock.WriteLock writeLock = sheetLocks.get(sheetName).writeLock();
        writeLock.lock();
        try {
            SheetRecord sheetRecord = sheets.get(sheetName);
            if (sheetRecord == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }
            sheetRecord.approveOrDenyRequest(requestDTO, ownerUserName, isApproved);
        } finally {
            writeLock.unlock();
        }
    }
}

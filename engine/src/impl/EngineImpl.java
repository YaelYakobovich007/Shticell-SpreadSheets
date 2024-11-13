package impl;

import api.Engine;
import engine.*;
import file.SheetFactory;
import file.XmlFileLoader;
import file.XmlValidator;
import jaxb.schema.STLSheet;
import permission.PermissionType;
import range.Boundaries;
import range.BoundariesFactory;
import sheet.api.Layout;
import sheet.api.Sheet;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;
import sheet.impl.SheetRecord;
import sheet.impl.SheetsManager;
import sheet.version.impl.VersionManager;
import ui.*;
import users.impl.UserManager;
import java.util.List;

public class EngineImpl implements Engine {
    private final UserManager userManager;
    private final SheetsManager sheetsManager;

    public EngineImpl() {
        this.userManager = new UserManager();
        this.sheetsManager = new SheetsManager();
    }

    @Override
    public void loadSheetFromFile(LoadRequestDTO loadRequestDTO) {
        STLSheet stlSheet = XmlFileLoader.loadXmlFile(loadRequestDTO.getFile());
        XmlValidator.validateLogicXmlFile(stlSheet);
        Sheet newSheet = SheetFactory.createSheet(stlSheet, loadRequestDTO.getUserName());
        VersionManager newVersionManager= new VersionManager();
        newVersionManager.addVersion(1,newSheet.getChangedCellsCount(),newSheet);
        SheetRecord sheetRecord= new SheetRecord(newSheet, newVersionManager, loadRequestDTO.getUserName());
        sheetsManager.addSheet(newSheet.getName(),sheetRecord);
    }

    @Override
    public SheetDTO updateCellValue(CellUpdateRequestDTO cellUpdateRequestDTO, SheetUserIdentifierDTO userIdentifierDTO) {
        PermissionType permission = sheetsManager.getUserPermission(userIdentifierDTO.getSheetName(), userIdentifierDTO.getUserName());
        if (permission != PermissionType.OWNER && permission != PermissionType.WRITER) {
            throw new RuntimeException("User does not have permission to update cell value");
        }
        int latestVersionNumber = sheetsManager.getLatestVersionNumber(userIdentifierDTO.getSheetName());
        if (userIdentifierDTO.getSheetVersion() < latestVersionNumber) {
            throw new RuntimeException("The version you are viewing is outdated. There is a newer version available. Please update to the latest version and try again.");
        }

        int[] parsedCoordinate = CoordinateParser.parseCoordinate(cellUpdateRequestDTO.getCellId());
        sheetsManager.updateCellValue(userIdentifierDTO.getSheetName(), parsedCoordinate[0], parsedCoordinate[1], cellUpdateRequestDTO.getNewValue(), userIdentifierDTO.getUserName());
        return new SheetDTO(sheetsManager.getSheetRecord(userIdentifierDTO.getSheetName()).getSheet());
    }

    @Override
    public CellDTO getCellValue(DisplayCellyRequestDTO displayCellyRequestDTO,SheetUserIdentifierDTO userIdentifierDTO) {
        int[] parsedCoordinate = CoordinateParser.parseCoordinate(displayCellyRequestDTO.getId());
        return sheetsManager.getCellValue(userIdentifierDTO.getSheetName(), parsedCoordinate[0], parsedCoordinate[1], userIdentifierDTO.getSheetVersion());
    }

    @Override
    public SheetDTO getSheetByName(String sheetName, String username) {
        PermissionType permission = sheetsManager.getUserPermission(sheetName, username);
        if (permission == null || permission == PermissionType.NONE) {
            throw new RuntimeException("User does not have permission to view this sheet.");
        }
        return sheetsManager.getSheetByName(sheetName, username);
    }

    @Override
    public SheetDTO getSheetByVersion(DisplayVersionSheetRequestDTO displayVersionSheetRequestDTO, String sheetName) {
        int versionNumber = displayVersionSheetRequestDTO.getVersionNumber();
        return sheetsManager.getSheetByVersion(sheetName, versionNumber);
    }

    @Override
    public SheetDTO createNewRange(AddNewRangeRequestDTO addNewRangeRequestDTO, SheetUserIdentifierDTO userIdentifierDTO) {
        PermissionType permission = sheetsManager.getUserPermission(userIdentifierDTO.getSheetName(), userIdentifierDTO.getUserName());
        if (permission != PermissionType.OWNER && permission != PermissionType.WRITER) {
            throw new RuntimeException("User does not have permission to create a new range.");
        }
        return sheetsManager.createNewRange(userIdentifierDTO.getSheetName(), addNewRangeRequestDTO.getName(), addNewRangeRequestDTO.getFrom(), addNewRangeRequestDTO.getTo(), userIdentifierDTO.getSheetVersion());
    }

    @Override
    public SheetDTO deleteRange(DeleteRangeRequestDTO deleteRangeRequestDTO,SheetUserIdentifierDTO userIdentifierDTO) {
        PermissionType permission = sheetsManager.getUserPermission(userIdentifierDTO.getSheetName(), userIdentifierDTO.getUserName());
        if (permission != PermissionType.OWNER && permission != PermissionType.WRITER) {
            throw new RuntimeException("User does not have permission to create a new range.");
        }
        return sheetsManager.deleteRange(userIdentifierDTO.getSheetName(), deleteRangeRequestDTO.getName(), userIdentifierDTO.getSheetVersion());
    }

    @Override
    public RangeDTO viewRange(ViewRangeRequestDTO viewRangeRequestDTO, SheetUserIdentifierDTO userIdentifierDTO) {
        return sheetsManager.viewRange(userIdentifierDTO.getSheetName(), viewRangeRequestDTO.getName(), userIdentifierDTO.getSheetVersion());
    }

    @Override
    public boolean isValidBoundaries(String from, String to, String sheetName) {
        Layout sheetLayout = sheetsManager.getSheetLayout(sheetName);
        BoundariesFactory.createBoundaries(from, to, sheetLayout);
        return true;
    }

    @Override
    public SheetDTO rowSorting(SortRequestDTO sortRequestDTO,SheetUserIdentifierDTO userIdentifierDTO) {
        Layout sheetLayout = sheetsManager.getSheetLayout(userIdentifierDTO.getSheetName());
        Coordinate fromCoordinate = CoordinateFactory.createCoordinate(sortRequestDTO.getFromRange(),sheetLayout);
        Coordinate toCoordinate = CoordinateFactory.createCoordinate(sortRequestDTO.getToRange(), sheetLayout);
        return sheetsManager.rowSorting(userIdentifierDTO.getSheetName(), userIdentifierDTO.getSheetVersion(), fromCoordinate, toCoordinate, sortRequestDTO.getSelectedColumns());
    }

    @Override
    public void updateCellTextColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO, String sheetName) {
        Layout sheetLayout = sheetsManager.getSheetLayout(sheetName);
        Coordinate cellCoordinate = CoordinateFactory.createCoordinate(updateCellStyleRequestDTO.getCellId(), sheetLayout);
        sheetsManager.updateCellTextColor(sheetName, cellCoordinate, updateCellStyleRequestDTO.getNewColor());
    }

    @Override
    public void updateCellBackgroundColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO, String sheetName) {
        Layout sheetLayout = sheetsManager.getSheetLayout(sheetName);
        Coordinate cellCoordinate = CoordinateFactory.createCoordinate(updateCellStyleRequestDTO.getCellId(), sheetLayout);
        sheetsManager.updateCellBackgroundColor(sheetName, cellCoordinate, updateCellStyleRequestDTO.getNewColor());
    }

    @Override
    public UniqueValuesForColumnDTO getUniqueValuesForColumn(UniqueValuesForColumnRequestDTO uniqueValuesForColumnRequestDTO,SheetUserIdentifierDTO userIdentifierDTO) {
        Coordinate from = CoordinateFactory.createCoordinate(uniqueValuesForColumnRequestDTO.getFromCoordinate(), sheetsManager.getSheetLayout(userIdentifierDTO.getSheetName()));
        Coordinate to = CoordinateFactory.createCoordinate(uniqueValuesForColumnRequestDTO.getToCoordinate(), sheetsManager.getSheetLayout(userIdentifierDTO.getSheetName()));
        Character targetColumn = uniqueValuesForColumnRequestDTO.getTargetColumn();
        return sheetsManager.getUniqueValuesForColumn(userIdentifierDTO.getSheetName(),userIdentifierDTO.getSheetVersion(), from, to, targetColumn);
    }

    @Override
    public SheetDTO filterSheet(FilterRequestDTO filterRequestDTO,SheetUserIdentifierDTO userIdentifierDTO) {
        Layout layout = sheetsManager.getSheetLayout(userIdentifierDTO.getSheetName());
        Boundaries boundaries = BoundariesFactory.createBoundaries(filterRequestDTO.getFromCoordinate(), filterRequestDTO.getToCoordinate(), layout);
        return sheetsManager.filterSheet(userIdentifierDTO.getSheetName(), userIdentifierDTO.getSheetVersion(), boundaries, filterRequestDTO.getSelectedColumns2UniqueValues());
    }

    @Override
    public NumOfVersionsDTO getNumOfVersions(String sheetName) {
        return sheetsManager.getNumOfVersions(sheetName);
    }

    @Override
    public CellDTO isCellOriginalValueNumeric(IsCellOriginalValueNumericRequestDTO cellOriginalValueNumericRequestDTO, SheetUserIdentifierDTO userIdentifierDTO) {
        int[] parsedCoordinate = CoordinateParser.parseCoordinate(cellOriginalValueNumericRequestDTO.getCellID());
        Layout layout = sheetsManager.getSheetLayout(userIdentifierDTO.getSheetName());
        CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1], layout);
        Coordinate coordinate = CoordinateFactory.createCoordinate(parsedCoordinate[0], parsedCoordinate[1]);
        return sheetsManager.isCellOriginalValueNumeric(userIdentifierDTO.getSheetName(), userIdentifierDTO.getSheetVersion(),coordinate);
    }

    @Override
    public SheetDTO updateTemporaryCellValue(UpdateTemporaryValuesInSheetRequestDTO updateRequestDTO, SheetUserIdentifierDTO userIdentifierDTO) {
        return sheetsManager.updateTemporaryCellValue(userIdentifierDTO.getSheetName(),userIdentifierDTO.getSheetVersion(), updateRequestDTO);
    }

    @Override
    public synchronized void addUser(String username) {
        userManager.addUser(username);
    }


    @Override
    public boolean isUserExists(String username) {
        return userManager.isUserExists(username);
    }

    @Override
    public List<SheetDetailsDTO> getSheetDetailsList(String userName){
        return sheetsManager.getSheetDetailsList(userName);
    }

    @Override
    public void approveOrDenyPermissionRequest(PermissionRequestDTO requestDTO, String ownerUserName, String sheetName, boolean isApproved) {
        sheetsManager.approveOrDenyPermissionRequest(sheetName, requestDTO, ownerUserName, isApproved);
    }

    @Override
    public List<PermissionRequestDTO> getPendingRequestsForOwner(String ownerUsername) {
       return  sheetsManager.getPendingRequestsForOwner(ownerUsername);
    }

    @Override
    public List<PermissionRequestDTO> getPermissionRequestsForSheet(String sheetName) {
       return  sheetsManager.getSheetRecord(sheetName).getAllRequests();
    }

    @Override
    public void askForSheetPermission(SheetPermissionRequestDTO requestDTO) {
       sheetsManager.askForSheetPermission(requestDTO);
    }
}

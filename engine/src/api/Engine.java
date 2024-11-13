package api;

import engine.*;
import ui.*;
import java.io.Serializable;
import java.util.List;

public interface Engine extends Serializable {
    void loadSheetFromFile(LoadRequestDTO loadRequestDTO);
    SheetDTO updateCellValue(CellUpdateRequestDTO cellUpdateRequestDTO,SheetUserIdentifierDTO userIdentifierDTO);
    CellDTO getCellValue(DisplayCellyRequestDTO cellQueryDTO, SheetUserIdentifierDTO userIdentifierDTO);
    SheetDTO rowSorting(SortRequestDTO sortRequestDTO, SheetUserIdentifierDTO userIdentifierDTO);
    SheetDTO getSheetByVersion(DisplayVersionSheetRequestDTO displayVersionSheetRequestDTO, String sheetName);
    SheetDTO createNewRange(AddNewRangeRequestDTO addNewRangeRequestDTO,SheetUserIdentifierDTO userIdentifierDTO);
    SheetDTO deleteRange(DeleteRangeRequestDTO deleteRangeRequestDTO, SheetUserIdentifierDTO userIdentifierDTO);
    RangeDTO viewRange(ViewRangeRequestDTO viewRangeRequestDTO, SheetUserIdentifierDTO userIdentifierDTO);
    boolean isValidBoundaries(String from, String to, String sheetName);
     void updateCellTextColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO,String sheetName);
     void updateCellBackgroundColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO, String sheetName);
    UniqueValuesForColumnDTO getUniqueValuesForColumn(UniqueValuesForColumnRequestDTO uniqueValuesForColumnRequestDTO,SheetUserIdentifierDTO userIdentifierDTO);
    SheetDTO  filterSheet(FilterRequestDTO filterRequestDTO, SheetUserIdentifierDTO userIdentifierDTO);
    NumOfVersionsDTO getNumOfVersions(String sheetName);
    CellDTO isCellOriginalValueNumeric(IsCellOriginalValueNumericRequestDTO cellOriginalValueNumericRequestDTO,SheetUserIdentifierDTO userIdentifierDTO);
    SheetDTO updateTemporaryCellValue(UpdateTemporaryValuesInSheetRequestDTO updateRequestDTO, SheetUserIdentifierDTO userIdentifierDTO);
    void addUser(String username);
    boolean isUserExists(String username);
    List<SheetDetailsDTO> getSheetDetailsList(String userName);
    SheetDTO getSheetByName(String sheetName, String username);
    void askForSheetPermission(SheetPermissionRequestDTO requestDTO);
    List<PermissionRequestDTO> getPermissionRequestsForSheet(String sheetName);
    List<PermissionRequestDTO> getPendingRequestsForOwner(String ownerUsername);
    void approveOrDenyPermissionRequest(PermissionRequestDTO requestDTO, String ownerUserName, String sheetName, boolean isApproved);
}

package api;

import engine.*;
import ui.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
public interface Engine extends Serializable {
    void loadSheetFromFile(LoadRequestDTO loadRequestDTO);
    void updateCellValue(CellUpdateRequestDTO cellUpdateRequestDTO);
    CellDTO getCellValue(DisplayCellyRequestDTO cellQueryDTO);
    SheetDTO getCurrentSheet();
    SheetDTO getSheetByVersion(DisplayVersionSheetRequestDTO displayVersionSheetRequestDTO);
    VersionChangesDTO getChangedCellsPerVersion();
    void saveSystemStateToFile(SaveSystemToFileRequestDTO saveSystemToFileRequestDTO);
    void loadSystemStateFromFile(LoadRequestDTO loadRequestDTO);
    void createNewRange(AddNewRangeRequestDTO addNewRangeRequestDTO);
    void deleteRange(DeleteRangeRequestDTO deleteRangeRequestDTO);
    RangeDTO viewRange(ViewRangeRequestDTO viewRangeRequestDTO);
    Map<String,RangeDTO> getRanges();
    boolean isValidBoundaries(String from, String to);
    SheetDTO rowSorting(String from, String to, List<Character> selectedColumns);
     void updateCellTextColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO);
     void updateCellBackgroundColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO);
    UniqueValuesForColumnDTO getUniqueValuesForColumn(UniqueValuesForColumnRequestDTO uniqueValuesForColumnRequestDTO);
    SheetDTO  filtering(FilterRequestDTO filterRequestDTO);
    NumOfVersionsDTO getNumOfVersions();
     CellDTO isCellOriginalValueNumeric(IsCellOriginalValueNumericRequestDTO cellOriginalValueNumericRequestDTO);
    SheetDTO updateTemporaryCellValue(CellUpdateRequestDTO cellUpdateRequestDTO);
}

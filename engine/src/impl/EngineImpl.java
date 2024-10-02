package impl;

import api.Engine;
import engine.*;
import jaxb.schema.STLSheet;
import parse.StringValidators;
import range.Boundaries;
import range.BoundariesFactory;
import range.Range;
import sheet.api.Sheet;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.CoordinateParser;
import sheet.version.impl.VersionManager;
import sheet.cell.api.Cell;
import ui.*;
import file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngineImpl implements Engine {
    private Sheet sheet;
    private VersionManager versionManager;

    @Override
    public void loadSheetFromFile(LoadRequestDTO loadRequestDTO) {
        STLSheet stlSheet = XmlFileLoader.loadXmlFile(loadRequestDTO.getFilePath());
        XmlValidator.validateLogicXmlFile(stlSheet);
        this.sheet = SheetFactory.createSheet(stlSheet);
        this.versionManager= new VersionManager();
        versionManager.addVersion(1,sheet.getChangedCellsCount(),sheet);
    }

    @Override
    public void updateCellValue(CellUpdateRequestDTO cellUpdateRequestDTO) {
        int[] parsedCoordinate=  CoordinateParser.parseCoordinate(cellUpdateRequestDTO.getCellId());
        CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1], sheet.getLayout());
         sheet= sheet.updateCellValueAndCalculate(parsedCoordinate[0], parsedCoordinate[1], cellUpdateRequestDTO.getNewValue());
         versionManager.addVersion(sheet.getVersionNumber(),sheet.getChangedCellsCount(),sheet);
    }

    @Override
    public CellDTO getCellValue(DisplayCellyRequestDTO displayCellyRequestDTO){
        int[] parsedCoordinate=  CoordinateParser.parseCoordinate(displayCellyRequestDTO.getId());
        CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1], sheet.getLayout());
        Cell cell=  sheet.getCell(parsedCoordinate[0], parsedCoordinate[1]);
        return  CellDTO.createCellDTO(cell);
    }

    @Override
    public SheetDTO getCurrentSheet(){
        return  new SheetDTO(sheet);
    }

    @Override
    public SheetDTO getSheetByVersion(DisplayVersionSheetRequestDTO displayVersionSheetRequestDTO){
        int versionNumber = displayVersionSheetRequestDTO.getVersionNumber();
        return  new SheetDTO(versionManager.getVersionDetails(versionNumber).getSheet());
    }

    @Override
    public VersionChangesDTO getChangedCellsPerVersion() {
        return new VersionChangesDTO(versionManager.getVersions());
    }

    @Override
    public void loadSystemStateFromFile(LoadRequestDTO loadRequestDTO){
        String filePath = loadRequestDTO.getFilePath().trim() + ".dat";
        EngineImpl engine=  StateSystemSaverLoader.readSystemFromFile(filePath);
        this.sheet= engine.sheet;
        this.versionManager= engine.versionManager;
    }

    @Override
    public void createNewRange(AddNewRangeRequestDTO addNewRangeRequestDTO) {
      sheet=  sheet.createNewRange(addNewRangeRequestDTO.getName(),addNewRangeRequestDTO.getFrom(),addNewRangeRequestDTO.getTo());
    }

    @Override
    public void deleteRange(DeleteRangeRequestDTO deleteRangeRequestDTO) {
        sheet= sheet.deleteRange(deleteRangeRequestDTO.getName());

    }

    @Override
    public RangeDTO viewRange(ViewRangeRequestDTO viewRangeRequestDTO) {
        Range range= sheet.getRange(viewRangeRequestDTO.getName());
        return  new RangeDTO(range);
    }

    @Override
    public void saveSystemStateToFile(SaveSystemToFileRequestDTO saveSystemToFileRequestDTO){
        String filePath = saveSystemToFileRequestDTO.getFilePath().trim() + ".dat";
        StateSystemSaverLoader.writeSystemToFile(this, filePath);
    }

    public Map<String,RangeDTO> getRanges(){
        Map<String, Range> ranges= sheet.getRanges();
        Map<String,RangeDTO> map= new HashMap<>();
        for (Map.Entry<String, Range> entry : ranges.entrySet()) {
            String key = entry.getKey();
            Range range = entry.getValue();
            RangeDTO rangeDTO = new RangeDTO(range);
            map.put(key, rangeDTO);
        }
        return map;
    }

    public boolean isValidBoundaries(String from, String to){
        BoundariesFactory.createBoundaries(from, to, sheet.getLayout());
        return true;
    }

    public SheetDTO rowSorting(String from, String to, List<Character> selectedColumns){
        Coordinate fromCoordinate= CoordinateFactory.createCoordinate(from,sheet.getLayout());
        Coordinate toCoordinate= CoordinateFactory.createCoordinate(to,sheet.getLayout());
        Sheet sortSheet= sheet.rowSorting(fromCoordinate, toCoordinate,selectedColumns);
        return new SheetDTO(sortSheet);
    }

    public void updateCellTextColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO){
        Coordinate cellCoordinate = CoordinateFactory.createCoordinate(updateCellStyleRequestDTO.getCellId(),sheet.getLayout());
        sheet.setTextColor(cellCoordinate, updateCellStyleRequestDTO.getNewColor());


    }

    public void updateCellBackgroundColor(UpdateCellStyleRequestDTO updateCellStyleRequestDTO){
        Coordinate cellCoordinate = CoordinateFactory.createCoordinate(updateCellStyleRequestDTO.getCellId(),sheet.getLayout());
        sheet.setBackgroundColor(cellCoordinate, updateCellStyleRequestDTO.getNewColor());
    }

    public UniqueValuesForColumnDTO getUniqueValuesForColumn(UniqueValuesForColumnRequestDTO uniqueValuesForColumnRequestDTO){
        Coordinate from= CoordinateFactory.createCoordinate(uniqueValuesForColumnRequestDTO.getFromCoordinate(),sheet.getLayout());
        Coordinate to = CoordinateFactory.createCoordinate(uniqueValuesForColumnRequestDTO.getToCoordinate(),sheet.getLayout());
        Character targetColumn=uniqueValuesForColumnRequestDTO.getTargetColumn();

        return  new UniqueValuesForColumnDTO(sheet.getUniqueValuesForColumn(targetColumn,from,to));
    }

    public  SheetDTO  filtering(FilterRequestDTO filterRequestDTO){
        Boundaries boundaries=BoundariesFactory.createBoundaries(filterRequestDTO.getFromCoordinate(),filterRequestDTO.getToCoordinate(),sheet.getLayout());
        return new SheetDTO(sheet.filtering(boundaries,filterRequestDTO.getSelectedColumns2UniqueValues()));
    }

    public  NumOfVersionsDTO getNumOfVersions(){
        return  new NumOfVersionsDTO(versionManager.getNumOfVersions());
    }


    public CellDTO isCellOriginalValueNumeric(IsCellOriginalValueNumericRequestDTO cellOriginalValueNumericRequestDTO) {
        int[] parsedCoordinate = CoordinateParser.parseCoordinate(cellOriginalValueNumericRequestDTO.getCellID());
        CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1], sheet.getLayout());
        Cell cell = sheet.getCell(parsedCoordinate[0], parsedCoordinate[1]);
        if (cell != null) {
            if (StringValidators.isNumber(cell.getOriginalValue())) {
                return CellDTO.createCellDTO(cell);
            } else {
                throw new RuntimeException("The selected cell must have an original value that is a numerical value.");
            }
        } else {
            throw new RuntimeException("The selected cell is empty.");
        }
    }

    public SheetDTO updateTemporaryCellValue(CellUpdateRequestDTO cellUpdateRequestDTO){
        int[] parsedCoordinate=  CoordinateParser.parseCoordinate(cellUpdateRequestDTO.getCellId());
        CoordinateParser.validateCoordinates(parsedCoordinate[0], parsedCoordinate[1], sheet.getLayout());
        sheet= sheet.updateTemporaryCellValue(parsedCoordinate[0], parsedCoordinate[1], cellUpdateRequestDTO.getNewValue());
        return  new SheetDTO(sheet);
    }
}

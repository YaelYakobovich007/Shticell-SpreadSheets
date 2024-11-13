package engine;

import range.Range;
import sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;

import static engine.CellDTO.createCellDTO;


public class SheetDTO {
    private final String name;
    private final int versionNumber;
    private final LayoutDTO layout;
    private final Map<String, CellDTO> activeCells;
    private final Map<String,RangeDTO> ranges;
    private final Map<String, String> backgroundColors;
    private final Map<String , String> textColors;
    private String userPermission;


    public SheetDTO(Sheet sheet) {
        this.name = sheet.getName();
        this.versionNumber = sheet.getVersionNumber();
        this.layout = new LayoutDTO(sheet.getLayout());
        this.ranges = getRanges(sheet.getRanges());

        this.activeCells = new HashMap<>();
        sheet.getActiveCellMap().forEach((key, value) -> activeCells.put(key.toString(), createCellDTO(value)));

        this.backgroundColors = new HashMap<>();
        sheet.getBackgroundColors().forEach((key, value) -> backgroundColors.put(key.toString(), value));

        this.textColors = new HashMap<>();
        sheet.getTextColors().forEach((key, value) -> textColors.put(key.toString(), value));

    }

    public SheetDTO(Sheet sheet, String userPermission) {
        this.userPermission= userPermission;
        this.name = sheet.getName();
        this.versionNumber = sheet.getVersionNumber();
        this.layout = new LayoutDTO(sheet.getLayout());
        this.ranges = getRanges(sheet.getRanges());

        this.activeCells = new HashMap<>();
        sheet.getActiveCellMap().forEach((key, value) -> activeCells.put(key.toString(), createCellDTO(value)));

        this.backgroundColors = new HashMap<>();
        sheet.getBackgroundColors().forEach((key, value) -> backgroundColors.put(key.toString(), value));

        this.textColors = new HashMap<>();
        sheet.getTextColors().forEach((key, value) -> textColors.put(key.toString(), value));

    }

    public String getSheetName() {
        return name;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public LayoutDTO getLayout() {
        return layout;
    }

    public Map<String, CellDTO> getActiveCells() {
        return activeCells;
    }
    public Map<String, String> getBackgroundColors(){
        return backgroundColors;
    }

    public Map<String, String> getTextColors(){
        return textColors;
    }
    public Map<String, RangeDTO> getRanges() {
        return ranges;
    }

    private Map<String, RangeDTO> getRanges(Map<String, Range> sheetRanges) {
        Map<String, RangeDTO> rangeDTOMap = new HashMap<>();
        for (Map.Entry<String, Range> entry : sheetRanges.entrySet()) {
            rangeDTOMap.put(entry.getKey(), new RangeDTO(entry.getValue()));
        }
        return rangeDTOMap;
    }

    public String getUserPermission() {
        return userPermission;
    }


}

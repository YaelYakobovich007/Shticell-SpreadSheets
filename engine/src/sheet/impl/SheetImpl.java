package sheet.impl;

import exception.CircularReferenceException;
import filtering.SheetFilter;
import javafx.scene.paint.Color;
import parse.ExpressionFactory;
import range.Boundaries;
import range.Range;
import range.RangesManager;
import sheet.api.Layout;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;
import sheet.coordinate.CoordinateFactory;
import sort.SheetSorter;
import utils.ColorUtil;


public class SheetImpl implements Sheet {
    private Map<Coordinate, Cell> activeCells;
    private final String name;
    private final Layout layout;
    private int versionNumber;
    private  int changedCellsCount;
    private final RangesManager rangesManager;
    private Map<Coordinate, String> backgroundColors;
    private Map<Coordinate, String> textColors;

    public SheetImpl(String name, Layout layout, RangesManager rangesManager) {
        this.name = name;
        this.layout = layout;
        this.activeCells = new HashMap<>();
        this.versionNumber=1;
        this.changedCellsCount=0;
        this.rangesManager = rangesManager;
        this.backgroundColors=new HashMap<>();
        this.textColors=new HashMap<>();
    }

    @Override
    public int getVersionNumber() {
       return versionNumber;
    }

    @Override
    public Cell getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    public Sheet updateCellValueAndCalculate(int row, int column, String value){
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        SheetImpl newSheetVersion = copySheet();
        //
        newSheetVersion.rangesManager.resetAllRangeInUse();
        //
        int newVersionNumber = newSheetVersion.getVersionNumber()+ 1;
        ExpressionFactory.setSheet(newSheetVersion);

        Cell cell= newSheetVersion.getOrInitializeCell(coordinate,value, newVersionNumber);
        boolean isCellValueChanged= newSheetVersion.hasCellValueChanged(cell,value,newVersionNumber);
        try {
            List<Cell> changedCells = newSheetVersion.recalculateAndGetChangedCells();
            newSheetVersion.updateSheetVersionIfNecessary(changedCells, isCellValueChanged);

            return newSheetVersion;

        }catch (Exception e){
            ExpressionFactory.setSheet(this);
            throw e;
        }
    }

    private Cell getOrInitializeCell(Coordinate coordinate, String value, int newVersionNumber) {
        return Optional.ofNullable(activeCells.get(coordinate))
                .map(cell -> {
                    cell.setCellOriginalValue(value);
                    return cell;
                })
                .orElseGet(() -> {
                    Cell newCell = new CellImpl(coordinate.getRow(), coordinate.getColumn(), value, newVersionNumber);
                    activeCells.put(coordinate, newCell);
                    return newCell;
                });
    }

    private boolean hasCellValueChanged(Cell cell , String value, int newVersionNumber){
        if (value.isEmpty()) {
            activeCells.remove(cell.getCoordinate());

            //מה קורה שמוחקים עכשיו תא ?
            return true;
        }

        boolean hasValueChanged = cell.calculateEffectiveValue();

        if (hasValueChanged) {
            cell.updateVersion(newVersionNumber);
        }

        return hasValueChanged;

    }

    public List<Cell> recalculateAndGetChangedCells() {
        return topologicalSortCells()
                .stream()
                .filter(Cell::calculateEffectiveValue)
                .collect(Collectors.toList());
    }

    private void updateSheetVersionIfNecessary(List<Cell> cellsThatHaveChanged, boolean isCellValueChanged) {
        this.changedCellsCount = cellsThatHaveChanged.size();
        if (changedCellsCount > 0 || isCellValueChanged){
            increaseVersion();
            cellsThatHaveChanged.forEach(c -> c.updateVersion(getVersionNumber()));
            this.changedCellsCount=changedCellsCount + (isCellValueChanged ? 1 : 0);
        }
    }

    @Override
    public int getChangedCellsCount() {
        return changedCellsCount;
    }

    @Override
    public void initializeCell(int row, int column, String value) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        Cell cell = activeCells.get(coordinate);
        if (cell == null) {
            cell = new CellImpl(row, column,value, getVersionNumber());
        }
        ExpressionFactory.setSheet(this);
        activeCells.put(coordinate, cell);
        cell.calculateEffectiveValue();
        recalculateAndGetChangedCells().size();
        changedCellsCount++;
    }

    @Override
    public void increaseVersion(){
        versionNumber++;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public Map<Coordinate, Cell> getActiveCellMap() {
        return activeCells;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Cell> topologicalSortCells() {
        Map<Cell, Integer> inDegree = new HashMap<>();
        Queue<Cell> zeroInDegreeQueue = new LinkedList<>();
        List<Cell> sortedCells = new ArrayList<>();

        for (Cell cell : activeCells.values()) {
            inDegree.put(cell, 0);
        }
        for (Cell cell : activeCells.values()) {
            for (Cell dependent : cell.getInfluencingOn()) {
                inDegree.put(dependent, inDegree.get(dependent) + 1);
            }
        }

        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                zeroInDegreeQueue.add(entry.getKey());
            }
        }

        while (!zeroInDegreeQueue.isEmpty()) {
            Cell cell = zeroInDegreeQueue.poll();
            sortedCells.add(cell);

            for (Cell dependent : cell.getInfluencingOn()) {
                int newInDegree = inDegree.get(dependent) - 1;
                inDegree.put(dependent, newInDegree);
                if (newInDegree == 0) {
                    zeroInDegreeQueue.add(dependent);
                }
            }
        }

        if (sortedCells.size() != activeCells.size()) {
            throw new CircularReferenceException("Circular reference detected in the sheet.");
        }
        return sortedCells;
    }

    public SheetImpl copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImpl) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy sheet", e);
        }
    }

    public Sheet createNewRange(String name, String from, String to){
        rangesManager.createRange(name,from,to);
        SheetImpl newSheetVersion = copySheet();
        newSheetVersion.rangesManager.resetAllRangeInUse();
        ExpressionFactory.setSheet(newSheetVersion);
        try {
            List<Cell> changedCells = newSheetVersion.recalculateAndGetChangedCells();
            return newSheetVersion;

        }catch (Exception e){
            ExpressionFactory.setSheet(this);
            throw e;
        }
    }

    public Sheet deleteRange(String name){
        rangesManager.deleteRange(name);
        SheetImpl newSheetVersion = copySheet();
        newSheetVersion.rangesManager.resetAllRangeInUse();
        ExpressionFactory.setSheet(newSheetVersion);
        try {
            List<Cell> changedCells = newSheetVersion.recalculateAndGetChangedCells();
            return newSheetVersion;
        }catch (Exception e){
            ExpressionFactory.setSheet(this);
            throw e;
        }
    }

    public Range getRange(String name){
        return rangesManager.getRange(name);
    }

    public Map<String,Range> getRanges(){
        return rangesManager.getAllRanges();
    }

    public void addNewRangeInUse(String rangeName){
        rangesManager.addNewRangeInUse(rangeName);
    }

    public  Sheet rowSorting(Coordinate from, Coordinate to, List<Character> selectedColumns){
        Sheet copySheet= copySheet();
        SheetSorter sorter = new SheetSorter();
        sorter.sort(copySheet,from,to,selectedColumns);
        return  copySheet;
    }

    public  Sheet filtering(Boundaries boundaries, Map<Character,List<String>> selectedColumns2UniqueValues){
        Sheet copySheet= copySheet();
        SheetFilter sheetFilter= new SheetFilter(copySheet,selectedColumns2UniqueValues,boundaries);
        sheetFilter.filter();
        return  copySheet;
    }

    public void setActiveCell(Map<Coordinate,Cell> newActiveCell){
        this.activeCells= newActiveCell;
    }

    public void setBackgroundColor(Coordinate coordinate, Color color) {
        backgroundColors.put(coordinate, ColorUtil.colorToHex(color));
    }

    public void setTextColor(Coordinate coordinate, Color color) {
        textColors.put(coordinate, ColorUtil.colorToHex(color));
    }






    public Map<Coordinate, String> getBackgroundColors() {
        return backgroundColors;
    }

    public Map<Coordinate, String> getTextColors() {
        return textColors;
    }

    public void  setBackgroundColors(Map<Coordinate, String> newBackgroundColors){
        this.backgroundColors= newBackgroundColors;
    }

    public void  setTextColors(Map<Coordinate, String> newTextColors){
        this.textColors= newTextColors;
    }

    public Set<EffectiveValue> getUniqueValuesForColumn(Character column,Coordinate from, Coordinate to){

        int targetColumnIndex  = column - 'A' + 1;
        Set<EffectiveValue> uniqueValues = new HashSet<>();

        activeCells.forEach((coordinate, cell) -> {
            int row= coordinate.getRow();
            int col= coordinate.getColumn();

            if (row >= from.getRow() && row <= to.getRow() && col >= from.getColumn() && col <= to.getColumn() && col == targetColumnIndex) {
                uniqueValues.add(cell.getEffectiveValue());
            }
        });
        return uniqueValues;
    }

    public Sheet updateTemporaryCellValue(int row, int column, String value){
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        SheetImpl newSheetVersion = copySheet();
        newSheetVersion.rangesManager.resetAllRangeInUse();

        ExpressionFactory.setSheet(newSheetVersion);
        Cell cell= newSheetVersion.getOrInitializeCell(coordinate,value, newSheetVersion.versionNumber);

        try {
            List<Cell> changedCells = newSheetVersion.recalculateAndGetChangedCells();
            return newSheetVersion;

        }catch (Exception e){
            ExpressionFactory.setSheet(this);
            throw e;
        }
    }

}

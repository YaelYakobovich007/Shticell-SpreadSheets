package sheet.cell.impl;

import parse.ExpressionFactory;
import sheet.cell.api.Cell;
import expression.api.Expression;
import sheet.cell.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateImpl;
import java.io.Serializable;
import java.util.*;

public class CellImpl implements Cell, Serializable {

    private Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;

    public CellImpl(int row, int column, String originalValue, int version) {
        this.coordinate = new CoordinateImpl(row, column);
        this.influencingOn = new ArrayList<>();
        this.dependsOn = new ArrayList<>();
        this.version = version;
        setCellOriginalValue(originalValue);
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    @Override
    public void setCellOriginalValue(String value) {
        this.originalValue= value.trim();
        clearDependencies();
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }


    @Override
    public Boolean calculateEffectiveValue() {
        ExpressionFactory.setCurrentCell(this);
        Expression exp = ExpressionFactory.createExpression(originalValue);

        EffectiveValue newEffectiveValue = exp.evaluate();

        if (newEffectiveValue.equals(effectiveValue)) {
            return false;
        } else {
            effectiveValue = newEffectiveValue;
            return true;
        }
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<Cell> getInfluencingOn() {
        return influencingOn;
    }

    @Override
    public void addDependency(Cell cell) {
        if (!dependsOn.contains(cell)) {
            dependsOn.add(cell);
            cell.addInfluencingOn(this);
        }

    }

    @Override
    public void addInfluencingOn(Cell cell) {
        influencingOn.add(cell);
    }

    @Override
    public void updateVersion(int newVersion) {
        this.version = newVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellImpl cell = (CellImpl) o;
        return Objects.equals(coordinate, cell.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coordinate);
    }

    private void clearDependencies() {
        for (Cell cell : dependsOn) {
            cell.getInfluencingOn().remove(this);
        }
        dependsOn.clear();
    }

    public  void setCoordinate(Coordinate coordinate){
        this.coordinate=coordinate;
    }
}


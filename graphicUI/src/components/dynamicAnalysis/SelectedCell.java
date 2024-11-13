package components.dynamicAnalysis;

import java.util.Objects;

public class SelectedCell {
    private final String cellId;
    private final double minValue;
    private final double maxValue;
    private final double stepSize;
    private final double orignalValue;

    public SelectedCell(String cellId, double orignalValue, double minValue, double maxValue, double stepSize) {
        this.cellId = cellId;
        this.orignalValue= orignalValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
    }

    public String getCellId() {
        return cellId;
    }

    public double getMinValue() {
        return minValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectedCell that = (SelectedCell) o;
        return Objects.equals(cellId, that.cellId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cellId);
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStepSize() {
        return stepSize;
    }

    public double getOrignalValue(){
        return orignalValue;
    }
}



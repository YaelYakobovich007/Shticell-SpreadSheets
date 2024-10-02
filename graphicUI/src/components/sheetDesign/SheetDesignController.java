
package components.sheetDesign;
import AnimationUtil.AnimatedToggleSwitch;
import AnimationUtil.ToggleSwitchManager;
import components.app.AppController;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class SheetDesignController {

    private AppController mainController;
    @FXML private ColorPicker TextColorPicker;
    @FXML private ComboBox<String> alignmentComboBox;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private VBox colorFillCheckBox;
    @FXML private Spinner<Integer> columnWidthSpinner;
    @FXML private Spinner<Integer> rowHeightSpinner;

    private final IntegerProperty columnWidthProperty = new SimpleIntegerProperty();
    private final IntegerProperty rowHeightProperty = new SimpleIntegerProperty();
    private final StringProperty alignmentProperty = new SimpleStringProperty();
    AnimatedToggleSwitch toggleSwitch = new AnimatedToggleSwitch();
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;

    @FXML
    public void initialize() {
        setupSpinners();
        alignmentComboBox.setOnAction(event -> {
            String selectedAlignment = alignmentComboBox.getValue();
            mainController.applyAlignment(selectedAlignment);
        });
        alignmentComboBox.promptTextProperty().bind(alignmentProperty);



        colorFillCheckBox.getChildren().add(toggleSwitch);

        toggleSwitch.switchedOnProperty().addListener((observable, wasOn, isOn) -> toggleColorFill(isOn));


        backgroundColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleSwitch.isSwitchedOn()) {
                mainController.onApplyBackgroundColor(newValue);
            }
        });

        TextColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleSwitch.isSwitchedOn()) {
                mainController.onApplyTextColor(newValue);
            }
        });


        ToggleSwitchManager.registerToggleSwitch(toggleSwitch);


    }

    private void toggleColorFill(boolean isOn) {
        if (isOn) {
            mainController.onApplyColor(backgroundColorPicker.valueProperty().get(), TextColorPicker.valueProperty().get());
        } else {
            mainController.resetCellStyle();
            backgroundColorPicker.setValue(DEFAULT_BACKGROUND_COLOR);
            TextColorPicker.setValue(DEFAULT_TEXT_COLOR);
        }
    }


    public StringProperty alignmentProperty() {
        return alignmentProperty;
    }

    public  IntegerProperty columnWidthProperty() {
        return columnWidthProperty;
    }

    public IntegerProperty rowHeightProperty() {
        return rowHeightProperty;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }


    private void setupSpinners() {
        SpinnerValueFactory<Integer> columnWidthFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 1000, 100);
        columnWidthSpinner.setValueFactory(columnWidthFactory);

        SpinnerValueFactory<Integer> rowHeightFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 1000, 100);
        rowHeightSpinner.setValueFactory(rowHeightFactory);

        columnWidthFactory.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                mainController.onColumnWidthChange(newValue);
            }
        });

        rowHeightFactory.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                mainController.onRowHeightChange(newValue);
            }
        });
    }

    public void updateSpinnersForSelectedCell(int columnWidth, int rowHeight) {
        columnWidthSpinner.getValueFactory().setValue(columnWidth);
        rowHeightSpinner.getValueFactory().setValue(rowHeight);
    }

    public String getAlignmentString(Pos alignment) {
        if (alignment == Pos.CENTER_LEFT) {
            return "Left";
        } else if (alignment == Pos.CENTER_RIGHT) {
            return "Right";
        } else {
            return "Center";
        }
    }

    public Pos getAlignmentFromString(String alignment) {
        return switch (alignment) {
            case "Left" -> Pos.CENTER_LEFT;
            case "Right" -> Pos.CENTER_RIGHT;
            case "Center" -> Pos.CENTER;
            default -> Pos.CENTER;
        };
    }

    public void updateAlignmentForSelectedCell(String alignment)
    {
        alignmentComboBox.setValue(alignment);
    }


    public void updateColorPickersForSelectedCell(Color backgroundColor, Color textColor) {
        backgroundColorPicker.valueProperty().set(backgroundColor);
        TextColorPicker.valueProperty().set(textColor);
    }
}

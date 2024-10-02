package components.filter;

import components.app.AppController;
import components.sheet.*;
import components.sort.RangeUtils;
import engine.SheetDTO;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.PopupManager;
import java.util.*;

public class FilterController {

    @FXML private ComboBox<String> columnComboBox;
    @FXML private TextField fromRangeTextField;
    @FXML private TextField toRangeTextField;
    @FXML private Button nextColumnButton;
    @FXML private VBox rangeSelectionView;
    @FXML private VBox valueSelectionView;
    @FXML private VBox columnSelectionView;
    @FXML private ScrollPane mainContinerScrollPane;
    @FXML private ListView<String> valueListView;

    private final Map<Character, List<String>> selectedColumns2UniqueValues= new HashMap<>();
    private  Character currentColumn;
    private VBox[] steps;
    private Set<String> availableColumns = new HashSet<>();
    private AppController mainController;
    private Stage stage;
    List<String> uniqueValues;
    private ChangeListener<String> columnSelectionListener;

    @FXML
    public void initialize() {
        initializeUIComponents();
        setupThemeListener();
        configureColumnComboBoxListener();
    }

    private void initializeUIComponents() {
        steps = new VBox[]{rangeSelectionView, columnSelectionView, valueSelectionView};
        valueListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        showStep(0);
    }

    private void setupThemeListener() {
        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(AppController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popupWindow_" + newStyle + ".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    private void configureColumnComboBoxListener() {
        columnSelectionListener = (observable, oldValue, newValue) -> handleColumnSelection(oldValue, newValue);
    }

    @FXML
    void handleBackAction(ActionEvent event) {
        showStep(1);
    }

    @FXML
    void handleCancelAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void handleFinishButton(ActionEvent event) {
        collectSelectedValues();
        stage.close();
        SheetDTO filterSheet= mainController.filterSheet(fromRangeTextField.getText(),toRangeTextField.getText(),selectedColumns2UniqueValues);
        displayFilteredSheet(filterSheet);
    }

    @FXML
    private void handleNextButtonToSelectValueAction(ActionEvent event) {
        if (columnComboBox.getSelectionModel().getSelectedItem()!=null) {
            loadUniqueValuesForSelectedColumn();
        } else {
            PopupManager.showErrorPopup("You must select a column first before proceeding to the next step");
        }
    }

    private void loadUniqueValuesForSelectedColumn() {
        uniqueValues = getUniqueValuesForColumn();
        valueListView.getItems().setAll(uniqueValues);
        showStep(2);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    void handleColumnSelection(String prevColumnSelect,String newColumnSelect) {
        currentColumn=getCurrentColumn(newColumnSelect);
        if (prevColumnSelect != null) {
            availableColumns.add(prevColumnSelect);
            selectedColumns2UniqueValues.remove(getCurrentColumn(prevColumnSelect));
        }
        if (newColumnSelect != null) {
            availableColumns.remove(newColumnSelect);
            selectedColumns2UniqueValues.put(currentColumn,new ArrayList<>());
        }
        updateAddButtonState();
    }

    @FXML void handleNextColumnAction(ActionEvent event) {
        collectSelectedValues();
        resetColumnSelection();
        showStep(1);
    }

    private void resetColumnSelection() {
        columnComboBox.getSelectionModel().selectedItemProperty().removeListener(columnSelectionListener);
        columnComboBox.getSelectionModel().clearSelection();
        columnComboBox.getItems().clear();
        columnComboBox.getItems().addAll(availableColumns);
        columnComboBox.getSelectionModel().selectedItemProperty().addListener(columnSelectionListener);
    }


    @FXML void handleSelectRangeAction(ActionEvent event) {
        String fromRange = fromRangeTextField.getText();
        String toRange = toRangeTextField.getText();

        if (fromRange.isEmpty() || toRange.isEmpty()) {
            PopupManager.showErrorPopup("All fields are required.");
        } else {
            try {
                if (mainController.isValidBoundaries(fromRange, toRange)){
                    populateAvailableColumns(fromRange,toRange);
                    columnComboBox.getItems().addAll(availableColumns);
                    columnComboBox.getSelectionModel().selectedItemProperty().addListener(columnSelectionListener);
                    updateAddButtonState();
                    showStep(1);
                }
            } catch(Exception e) {
                PopupManager.showErrorPopup(e.getMessage());
            }
        }
    }

    private void populateAvailableColumns(String fromRange, String toRange) {
        availableColumns.clear();
        availableColumns= RangeUtils.getColumnsFromRange(fromRange,toRange);
    }

    public List<String> getUniqueValuesForColumn() {
        List<String> uniqueValues = new ArrayList<>();

        mainController.getUniqueValuesForColumn(currentColumn, fromRangeTextField.getText(),toRangeTextField.getText()).forEach(effectiveValue -> uniqueValues.add(FormattedValuePrinter.formatValue(effectiveValue)));
        return uniqueValues;
    }

    private Character getCurrentColumn(String selectedColumn) {
        Character column= null;
        if (selectedColumn != null) {
            String[] splitText = selectedColumn.split(" ");
            String columnLetter = splitText[splitText.length - 1];
            column=columnLetter.charAt(0);

        }
        return column;
    }

    private void showStep(int stepIndex) {
        for (int i = 0; i < steps.length; i++) {
            steps[i].setVisible(i == stepIndex);
        }
    }

    private void updateAddButtonState() {
        nextColumnButton.setDisable(availableColumns.isEmpty());
    }

    private void collectSelectedValues() {
        List<String> selectedValues = new ArrayList<>(valueListView.getSelectionModel().getSelectedItems());
        if (currentColumn != null) {
            selectedColumns2UniqueValues.put(currentColumn, selectedValues);
        }
    }

    private void displayFilteredSheet(SheetDTO sheetDTO){
        FlowPane flowPane = new FlowPane();
        SheetController sheetController = new sheetControllerImpl();
        GridPane sheetGrid = sheetController.createDynamicGrid(sheetDTO);

        flowPane.getChildren().clear();
        flowPane.getChildren().add(sheetGrid);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage newStage = new Stage();
        newStage.setTitle("Filtered Sheet");
        newStage.setScene(new Scene(scrollPane));
        newStage.show();
    }


}

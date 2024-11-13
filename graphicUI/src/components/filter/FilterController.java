package components.filter;

import components.viewSheetMain.ViewSheetMainController;
import components.sheet.*;
import components.sort.RangeUtils;
import engine.SheetDTO;
import engine.UniqueValuesForColumnDTO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import java.io.IOException;
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
    private ViewSheetMainController mainController;
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
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(ViewSheetMainController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popUpWindowDesign/popupWindow_" + newStyle + ".css").toExternalForm();
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
        mainController.filterSheet(fromRangeTextField.getText(), toRangeTextField.getText(), selectedColumns2UniqueValues, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to filter sheet: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    SheetDTO filteredSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        stage.close();
                        displayFilteredSheet(filteredSheet);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to filter sheet: " + errorMessage));
                }
            }
        });
    }

    @FXML
    private void handleNextButtonToSelectValueAction(ActionEvent event) {
        if (columnComboBox.getSelectionModel().getSelectedItem() != null) {
            loadUniqueValuesForSelectedColumn();
        } else {
            PopupManager.showErrorPopup("You must select a column first before proceeding to the next step");
        }
    }

    private void loadUniqueValuesForSelectedColumn() {
        mainController.getUniqueValuesForColumn(currentColumn, fromRangeTextField.getText(), toRangeTextField.getText(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to get unique values: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        UniqueValuesForColumnDTO uniqueValuesDTO = Constants.GSON_INSTANCE.fromJson(responseBody, UniqueValuesForColumnDTO.class);
                        uniqueValues = new ArrayList<>(uniqueValuesDTO.getUniqueValues());
                        Platform.runLater(() -> {
                            valueListView.getItems().setAll(uniqueValues);
                            showStep(2);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to get unique values: " + errorMessage));
                    }
                }
            }
        });
    }

    public void setMainController(ViewSheetMainController mainController) {
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


    @FXML
    void handleSelectRangeAction(ActionEvent event) {
        String fromRange = fromRangeTextField.getText();
        String toRange = toRangeTextField.getText();

        if (fromRange.isEmpty() || toRange.isEmpty()) {
            PopupManager.showErrorPopup("All fields are required.");
        } else {
            mainController.isValidBoundaries(fromRange, toRange, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Error: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (response) {
                        if (response.isSuccessful()) {
                            String responseBody = response.body() != null ? response.body().string() : "";
                            boolean isValid = Constants.GSON_INSTANCE.fromJson(responseBody, Boolean.class);
                            Platform.runLater(() -> {
                                if (isValid) {
                                    populateAvailableColumns(fromRange, toRange);
                                    columnComboBox.getItems().addAll(availableColumns);
                                    columnComboBox.getSelectionModel().selectedItemProperty().addListener(columnSelectionListener);
                                    updateAddButtonState();
                                    showStep(1);
                                } else {
                                    PopupManager.showErrorPopup("Invalid boundaries.");
                                }
                            });
                        } else {
                            Platform.runLater(() -> PopupManager.showErrorPopup("Error: Invalid boundaries."));
                        }
                    }
                }
            });
        }
    }

    private void populateAvailableColumns(String fromRange, String toRange) {
        availableColumns.clear();
        availableColumns= RangeUtils.getColumnsFromRange(fromRange,toRange);
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

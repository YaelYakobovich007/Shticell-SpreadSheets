package components.sort;

import AnimationUtil.PopUpWindowAnimator;
import components.app.AppController;
import components.sheet.SheetController;
import components.sheet.sheetControllerImpl;
import engine.SheetDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.PopupManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SortController {
    //צריכה להוסיםף את הקטע שאם נגמר כבר האופציות של העמודות אז לא יההי ניתן ללחוץ על הוספת מיון נוסף אבל שזה כל המזן יהיה איתו אקטיבי

    @FXML private Button sortButton;
    @FXML private TextField fromRangeTextField;
    @FXML private TextField toRangeTextField;
    @FXML private ComboBox<String> firstSortCoulmnComboBox;
    @FXML private VBox sortColumnsContainer;
    @FXML private Button addSortColumnButton;
    @FXML private ScrollPane sortColumnsScrollPane;
    private final List<ComboBox<String>> columnComboBoxes = new ArrayList<>();
    private Set<String> availableColumns = new HashSet<>();
    private int numOfAvailableColumns = 0;
    private AppController mainController;
    private Stage stage;
    private static final int MAX_WINDOW_HEIGHT = 600;
    private static final int ROW_HEIGHT = 50;


    @FXML
    private ScrollPane mainContianerScrollPane;

    @FXML
    private void initialize() {
        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(AppController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popupWindow_" +newStyle+".css").toExternalForm();
        mainContianerScrollPane.getStylesheets().clear();
        mainContianerScrollPane.getStylesheets().add(styleSheet);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    void handleAddSortColumnAction(ActionEvent event) {
        addSortColumn();
    }

    private void addSortColumn() {
        HBox newSortRow = new HBox(10);
        Label label = new Label("then By ");
        label.getStyleClass().add("label-primary");
        label.setPrefHeight(20);
        label.setPrefWidth(100);
        newSortRow.getChildren().add(label);

        ComboBox<String> columnComboBox = new ComboBox<>();
        columnComboBox.getStyleClass().add("combo-box");
        columnComboBox.setPrefWidth(firstSortCoulmnComboBox.getPrefWidth());
        columnComboBox.setPromptText("Select column");
        newSortRow.getChildren().add(columnComboBox);
        columnComboBoxes.add(columnComboBox);
        columnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleColumnSelection(columnComboBox,oldValue,newValue));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("button-primary");
        deleteButton.setPrefHeight(35);
        deleteButton.setPrefWidth(100);
        newSortRow.getChildren().add(deleteButton);
        deleteButton.setOnAction(e -> handleRemoveSortColumn(newSortRow, columnComboBox));
        sortColumnsContainer.getChildren().add(newSortRow);
        columnComboBox.getItems().addAll(availableColumns);



        double newHeight = stage.getHeight() + ROW_HEIGHT;
        if (newHeight <= MAX_WINDOW_HEIGHT) {
            stage.setHeight(newHeight);
        } else {
            sortColumnsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }


        updateAddButtonState();

    }

    private void handleRemoveSortColumn(HBox sortRow, ComboBox<String> comboBox) {
        String selectedColumn = comboBox.getValue();
        if (selectedColumn != null) {
            availableColumns.add(selectedColumn);
            for (ComboBox<String> cb : columnComboBoxes) {
                if (cb != comboBox) {
                    cb.getItems().add(selectedColumn);
                }
            }
        }
        sortColumnsContainer.getChildren().remove(sortRow);
        columnComboBoxes.remove(comboBox);
        updateAddButtonState();
    }

    private void handleColumnSelection(ComboBox<String> comboBox, String oldValue, String newValue) {
        if (oldValue != null) {
            availableColumns.add(oldValue);

            for (ComboBox<String> cb : columnComboBoxes) {
                if (cb != comboBox) {
                    cb.getItems().add(oldValue);
                }
            }
        }
        if (newValue != null) {
            availableColumns.remove(newValue);

            for (ComboBox<String> cb : columnComboBoxes) {
                if (cb != comboBox) {
                    cb.getItems().remove(newValue);
                }
            }
        }
        updateAddButtonState();
    }


    @FXML
    void handleCancelAction(ActionEvent event) {
        PopUpWindowAnimator.applySlideOut(stage);
    }

    @FXML
    void handleSortAction(ActionEvent event) {
        String fromRange = fromRangeTextField.getText();
        String toRange = toRangeTextField.getText();
        List<Character> selectedColumns = new ArrayList<>();
        for (ComboBox<String> comboBox : columnComboBoxes) {
            String selectedColumn = comboBox.getValue();
            if (selectedColumn != null) {
                String[] splitText = selectedColumn.split(" ");
                String columnLetter = splitText[splitText.length - 1];
                selectedColumns.add(columnLetter.charAt(0));
            }
        }
        SheetDTO sortSheet= mainController.rowSorting(fromRange,toRange,selectedColumns);
        showSortSheet(sortSheet);
    }

    @FXML
    void handleSelectRangeAction(ActionEvent event) {
        String fromRange = fromRangeTextField.getText();
        String toRange = toRangeTextField.getText();
        if (fromRange.isEmpty() || toRange.isEmpty()) {
            PopupManager.showErrorPopup("All fields are required.");
        } else {
            try {
                if (mainController.isValidBoundaries(fromRange, toRange)) {
                    populateAvailableColumns(fromRange,toRange);
                    firstSortCoulmnComboBox.getItems().addAll(availableColumns);
                    firstSortCoulmnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleColumnSelection(firstSortCoulmnComboBox,oldValue,newValue));
                    columnComboBoxes.add(firstSortCoulmnComboBox);
                    sortButton.setDisable(false);
                    firstSortCoulmnComboBox.setDisable(false);
                    updateAddButtonState();
                    fromRangeTextField.setDisable(true);
                    toRangeTextField.setDisable(true);
                    ((Button) event.getSource()).setDisable(true);
                }
            } catch(Exception e) {
                PopupManager.showErrorPopup(e.getMessage());
            }
        }
    }

    private void populateAvailableColumns(String fromRange, String toRange) {
        availableColumns.clear();
        availableColumns= RangeUtils.getColumnsFromRange(fromRange,toRange);
        numOfAvailableColumns= availableColumns.size();
    }

    private void updateAddButtonState() {
        if (availableColumns.isEmpty()||columnComboBoxes.size()==numOfAvailableColumns) {
            addSortColumnButton.setDisable(true);
        } else {
            addSortColumnButton.setDisable(false);
        }
    }

    private void showSortSheet(SheetDTO sheetDTO){
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


        newStage.setTitle("Sorted Sheet");

        newStage.setScene(new Scene(scrollPane));


        stage.close();
        newStage.show();
    }
}

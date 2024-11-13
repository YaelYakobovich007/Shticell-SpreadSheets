package components.sort;

import AnimationUtil.PopUpWindowAnimator;
import components.viewSheetMain.ViewSheetMainController;
import components.sheet.SheetController;
import components.sheet.sheetControllerImpl;
import engine.SheetDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;

import java.io.IOException;
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
    private ViewSheetMainController mainController;
    private Stage stage;
    private static final int MAX_WINDOW_HEIGHT = 600;
    private static final int ROW_HEIGHT = 50;

    @FXML
    private ScrollPane mainContianerScrollPane;

    @FXML
    private void initialize() {
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        updateStyleSheet(ViewSheetMainController.themeProperty().get());
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/popUpWindowDesign/popupWindow_" +newStyle+".css").toExternalForm();
        mainContianerScrollPane.getStylesheets().clear();
        mainContianerScrollPane.getStylesheets().add(styleSheet);
    }

    public void setMainController(ViewSheetMainController mainController) {
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

        mainController.sortRows(fromRange, toRange, selectedColumns, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to sort rows: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    SheetDTO sortedSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                    Platform.runLater(() -> {
                        showSortSheet(sortedSheet);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to sort rows: " + errorMessage));
                }
            }
        });
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
                                    firstSortCoulmnComboBox.getItems().addAll(availableColumns);
                                    firstSortCoulmnComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleColumnSelection(firstSortCoulmnComboBox, oldValue, newValue));
                                    columnComboBoxes.add(firstSortCoulmnComboBox);
                                    sortButton.setDisable(false);
                                    firstSortCoulmnComboBox.setDisable(false);
                                    updateAddButtonState();
                                    fromRangeTextField.setDisable(true);
                                    toRangeTextField.setDisable(true);
                                    ((Button) event.getSource()).setDisable(true);
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

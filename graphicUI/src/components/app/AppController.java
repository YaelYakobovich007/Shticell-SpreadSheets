package components.app;

import AnimationUtil.PopUpWindowAnimator;
import api.Engine;
import components.header.HeaderController;
import components.header.HeaderUIModel;
import components.rangePanel.RangePanelController;
import components.settingsControlPanel.SettingsControlPanelController;
import components.sheet.SheetController;
import components.sheet.SheetUIModel;
import components.sheet.sheetControllerImpl;
import components.sheetDesign.SheetDesignController;
import components.sortFilterPanel.SortFilterPanelController;
import components.updateCell.UpdateCellController;
import engine.CellDTO;
import engine.RangeDTO;
import engine.SheetDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import sheet.cell.api.EffectiveValue;
import ui.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AppController {
    @FXML private GridPane header;
    @FXML private HeaderController headerController;
    @FXML private VBox rangePanel;
    @FXML private RangePanelController rangePanelController;
    @FXML private GridPane sheetDesign;
    @FXML private SheetDesignController sheetDesignController;
    @FXML private VBox sortFilterPane;
    @FXML private SortFilterPanelController sortFilterPanelController;
    @FXML private SettingsControlPanelController settingsControlPanelController;
    @FXML private VBox VboxStyleMenu;
    @FXML private FlowPane designFlowPane;
    @FXML private VBox flowPane;
    @FXML private ScrollPane mainContinerScrollPane;
    @FXML  private  VBox topAppVbox;
    @FXML private VBox leftMenuVbox;

    private Engine engine;
    private Stage primaryStage;
    private SheetUIModel sheetUIModel;
    private HeaderUIModel headerUIModel;
    private Parent dynamicSlider;
    private static StringProperty themeProperty = new SimpleStringProperty("green");
    public static StringProperty themeProperty() {
        return themeProperty;
    }
    private GridPane sheetGrid;
    private SheetController sheetController;


    @FXML
    public void initialize() {
        if (headerController != null) {
            headerController.setMainController(this);
            headerUIModel = headerController.getHeaderUIModel();
        }
        if (sheetDesignController != null) {
            sheetDesignController.setMainController(this);
        }
        if (rangePanelController!= null){
            rangePanelController.setMainController(this);
        }
        if(sortFilterPanelController!=null){
            sortFilterPanelController.setMainController(this);

            themeProperty().addListener((obs, oldTheme, newTheme) -> {
                updateStyleSheet(newTheme);
            });
        }
        if(settingsControlPanelController!=null){
            settingsControlPanelController.setMainController(this);
        }
        sheetDesignController.columnWidthProperty().addListener((obs, oldVal, newVal) -> onColumnWidthChange(newVal.intValue()));
        sheetDesignController.rowHeightProperty().addListener((obs, oldVal, newVal) -> onRowHeightChange(newVal.intValue()));
        sheetDesignController.alignmentProperty().addListener((obs, oldVal, newVal) -> applyAlignment(newVal));
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("app_" + newStyle + ".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    public void initializeSheet(SheetDTO sheetDTO) {
        sheetController = new sheetControllerImpl();
        sheetGrid = sheetController.createDynamicGrid(sheetDTO);

        Platform.runLater(() -> {
            flowPane.getChildren().clear();
            sheetGrid.setAlignment(Pos.CENTER);
            flowPane.getChildren().add(sheetGrid);
        });
        sheetUIModel = sheetController.getSheetUIModel();
        sheetGrid.visibleProperty().setValue(true);
        rangePanelController.populateRangeComboBox(getRanges());
    }

    public void loadFile(File selectedFile) {
        FileLoadingTask loadFileTask = new FileLoadingTask(engine, selectedFile.getAbsolutePath());
        final Stage progressStage = WindowManager.showProgressWindow(primaryStage, loadFileTask);
        loadFileTask.setOnSucceeded(e -> {
            SheetDTO sheetDTO = loadFileTask.getValue();
            if (sheetDTO != null) {
                Platform.runLater(() -> {
                    initializeSheet(sheetDTO);
                    VboxStyleMenu.setDisable(false);
                    leftMenuVbox.setDisable(false);
                    headerController.getUpdateValueButtonDisabled().bind(sheetUIModel.getSelectedCellProperty().isNull());
                    headerUIModel.setFilePath(selectedFile.getAbsolutePath());
                    headerUIModel.setFileLoaded(true);
                    sheetUIModel.getSelectedCellProperty().addListener((obs, oldCellId, newCellId) -> {
                        if (newCellId != null) {
                            handleCellClick(newCellId);
                        }
                    });
                    progressStage.close();
                });
            }
        });
        loadFileTask.setOnFailed(e -> {
            Throwable throwable = loadFileTask.getException();
            Platform.runLater(() -> {
                progressStage.close();
                WindowManager.showErrorWindow(throwable);
            });
        });
        new Thread(loadFileTask).start();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    private void handleCellClick(Label cellLabel) {
        String cellId = (String) cellLabel.getUserData();
        CellDTO cellDTO = engine.getCellValue(new ui.DisplayCellyRequestDTO(cellId));
        updateHeaderUIModel(cellId, cellDTO);
        sheetController.handleCellClick(cellDTO);
        updateSheetDesignUI();
        updateSelectedCellStyle();
        if (cellDTO != null) {
            updateCellInfoInHeader(cellDTO);
        } else {
            setEmptyCellInfoInHeader();
        }
    }

    private void updateHeaderUIModel(String cellId, CellDTO cellDTO) {
        headerUIModel.setSelectedCellID(cellId);
    }

    private void updateSheetDesignUI() {
        int columnWidth = sheetController.getSelectedCellColumnWidth();
        int rowHeight = sheetController.getSelectedCellRowHeight();
        sheetDesignController.updateSpinnersForSelectedCell(columnWidth, rowHeight);
        updateAlignmentForSelectedCell(sheetUIModel.getSelectedCellProperty().get().getAlignment());
    }

    private void updateSelectedCellStyle() {
        if (sheetUIModel.getSelectedCellProperty().get().getBackground() != null) {
            Paint backgroundFill = sheetUIModel.getSelectedCellProperty().get().getBackground().getFills().getFirst().getFill();
            sheetDesignController.updateColorPickersForSelectedCell((Color) backgroundFill, (Color) sheetUIModel.getSelectedCellProperty().get().getTextFill());
        } else {
            sheetDesignController.updateColorPickersForSelectedCell(Color.WHITE, (Color) sheetUIModel.getSelectedCellProperty().get().getTextFill());
        }
    }

    private void updateCellInfoInHeader(CellDTO cellDTO) {
        headerUIModel.setOriginalCellValue(cellDTO.getOriginalValue());
        headerUIModel.setLastUpdate(Integer.toString(cellDTO.getLastModifiedVersion()));
    }

    private void setEmptyCellInfoInHeader() {
        headerUIModel.setOriginalCellValue("Empty Cell");
        headerUIModel.setLastUpdate("Last update cell version");
    }


    public void onColumnWidthChange(int newWidth) {
        sheetController.applyColumnWidth(newWidth);
    }

    public void onRowHeightChange(int newHeight) {
        sheetController.applyRowHeight(newHeight);
    }

    public void applyAlignment(String alignment) {
        if(sheetUIModel.getSelectedCellProperty().get()!=null) {
            Pos alignmentPosition = sheetDesignController.getAlignmentFromString(alignment);
            sheetController.changeColumnAlignment(alignmentPosition);
        }
    }

    public void updateAlignmentForSelectedCell(Pos alignment) {
        if(sheetUIModel.getSelectedCellProperty().get()!=null) {
            String alignmentString = sheetDesignController.getAlignmentString(alignment);
            sheetDesignController.updateAlignmentForSelectedCell(alignmentString);
        }
    }

    public void onApplyTextColor(Color newTextColor) {
        if(sheetUIModel.getSelectedCellProperty().get()!=null) {
            sheetController.setSelectedCellTextColor(newTextColor);
            String cellId = (String) sheetUIModel.getSelectedCellProperty().get().getUserData();
            engine.updateCellTextColor(new UpdateCellStyleRequestDTO(cellId, newTextColor));
        }

    }

    public void onApplyBackgroundColor(Color newBackgroundColor) {
        if(sheetUIModel.getSelectedCellProperty().get()!= null) {
            sheetController.setSelectedCellBackgroundColor(newBackgroundColor);
            String cellId = (String) sheetUIModel.getSelectedCellProperty().get().getUserData();
            engine.updateCellBackgroundColor(new UpdateCellStyleRequestDTO(cellId, newBackgroundColor));
        }
    }

    public void onApplyColor(Color newBackgroundColor,Color newTextColor){
        if(sheetUIModel.getSelectedCellProperty().get()!= null) {
            sheetController.setSelectedCellTextColor(newTextColor);
            sheetController.setSelectedCellBackgroundColor(newBackgroundColor);
        }
    }

    public void resetCellStyle(){
        sheetController.resetCellStyle();
    }

    @FXML
    public void handleUpdateCellButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/updateCell/updateCell.fxml"));
            Parent root = loader.load();
            UpdateCellController updateCellController = loader.getController();
            updateCellController.setMainController(this);
            Stage stage = new Stage();
            stage.setTitle("Update Cell");
            stage.setScene(new Scene(root));
            updateCellController.setStage(stage);
            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateCell(String newValue){
        String cellID = (String) sheetUIModel.getSelectedCellProperty().get().getUserData();
        engine.updateCellValue(new CellUpdateRequestDTO(cellID, newValue));
        SheetDTO sheetDTO= engine.getCurrentSheet();
        sheetController.updateAllCellContent(sheetDTO);

        Integer numOfVersions= engine.getNumOfVersions().getNumOfVersions();
        ObservableList<Integer> integerValues = FXCollections.observableArrayList();

        for (int i = 1; i <= numOfVersions; i++) {
            integerValues.add(i);
        }
        headerController.updateVersionSelectorOptions(integerValues);
    }

    public void createNewRange(String rangeName,String from, String to){
        engine.createNewRange(new AddNewRangeRequestDTO(rangeName, from, to));
        rangePanelController.addNewRangeToComboBox(rangeName);
        SheetDTO sheetDTO= engine.getCurrentSheet();
        sheetController.updateAllCellContent(sheetDTO);
    }

    public void showSelectRange(String rangeName){
        RangeDTO selectedRange= engine.viewRange(new ViewRangeRequestDTO(rangeName));
        sheetController.showSelectRange(selectedRange);
    }

    public void resetSelectRange(){
        sheetController.resetSelectedRange();
    }

    public Set<String> getRanges(){
        return engine.getRanges().keySet();
    }

    public void deleteRange(String rangeName){
        engine.deleteRange(new DeleteRangeRequestDTO(rangeName));
        rangePanelController.removeRangeFromComboBox(rangeName);
        SheetDTO sheetDTO= engine.getCurrentSheet();
        sheetController.updateAllCellContent(sheetDTO);
    }

    public void updateTemporaryValuesInSheet(String newValue, String cellID) {
        SheetDTO sheetDTO= engine.updateTemporaryCellValue(new CellUpdateRequestDTO(cellID,newValue));
        sheetController.updateAllCellContent(sheetDTO);
    }

    public boolean isValidBoundaries(String from, String to){
        return engine.isValidBoundaries(from, to);
    }

    public SheetDTO rowSorting(String from, String to,List<Character> selectedColumns){
        return engine.rowSorting(from, to, selectedColumns);
    }

    public Set<EffectiveValue> getUniqueValuesForColumn(Character column,String fromCoordinate, String toCoordinate){
        return engine.getUniqueValuesForColumn(new UniqueValuesForColumnRequestDTO(column, fromCoordinate,toCoordinate)).getUniqueValues();
    }

    public SheetDTO filterSheet(String from, String to, Map<Character,List<String>> selectedColumns2UniqueValues){
        return engine.filtering( new FilterRequestDTO(from, to, selectedColumns2UniqueValues));
    }

    public SheetDTO getSheetByVersion(Integer versionNumber){
        return engine.getSheetByVersion(new DisplayVersionSheetRequestDTO(versionNumber));
    }

    public  String isCellOriginalValueNumeric(String cellID){
        return engine.isCellOriginalValueNumeric(new IsCellOriginalValueNumericRequestDTO(cellID)).getOriginalValue();
    }

    public void showSlider(Parent slider) {
        topAppVbox.getChildren().remove(VboxStyleMenu);
        VboxStyleMenu.setOpacity(0.5);
        dynamicSlider = slider;
        topAppVbox.getChildren().add(slider);
        slider.setVisible(true);
        slider.setOpacity(1);
        leftMenuVbox.setDisable(true);
        header.disableProperty().setValue(true);

    }

    public void hideSlider(String cellId, double originalCellValue) {
        if (dynamicSlider != null) {
            topAppVbox.getChildren().remove(dynamicSlider);
            dynamicSlider.setVisible(false);
            dynamicSlider.setOpacity(0);
        }
        topAppVbox.getChildren().add(VboxStyleMenu);
        VboxStyleMenu.setOpacity(1);
        header.disableProperty().setValue(false);
        leftMenuVbox.setDisable(false);
        SheetDTO sheetDTO = engine.updateTemporaryCellValue(new CellUpdateRequestDTO(cellId, String.valueOf(originalCellValue)));
        sheetController.updateAllCellContent(sheetDTO);
    }
}



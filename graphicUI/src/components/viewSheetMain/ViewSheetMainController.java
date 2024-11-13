package components.viewSheetMain;

import AnimationUtil.PopUpWindowAnimator;
import components.appMain.AppMainController;
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
import engine.SheetDetailsDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
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
import okhttp3.*;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;
import ui.*;
import util.ColorUtil;
import util.Constants;
import util.PopupManager;
import util.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ViewSheetMainController implements Closeable {
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
    @FXML private  BorderPane mainBorderPane;

    private Stage primaryStage;
    private SheetUIModel sheetUIModel;
    private HeaderUIModel headerUIModel;
    private Parent dynamicSlider;
    private final static StringProperty themeProperty = new SimpleStringProperty("green");
    public static StringProperty themeProperty() {
        return themeProperty;
    }
    private GridPane sheetGrid;
    private SheetController sheetController;
    private AppMainController mainController;

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

            themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
        }
        if(settingsControlPanelController!=null){
            settingsControlPanelController.setMainController(this);
        }
        sheetDesignController.columnWidthProperty().addListener((obs, oldVal, newVal) -> onColumnWidthChange(newVal.intValue()));
        sheetDesignController.rowHeightProperty().addListener((obs, oldVal, newVal) -> onRowHeightChange(newVal.intValue()));
        sheetDesignController.alignmentProperty().addListener((obs, oldVal, newVal) -> applyAlignment(newVal));
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("cssDesign/viewSheetMain_" + newStyle + ".css").toExternalForm();
        mainContinerScrollPane.getStylesheets().clear();
        mainContinerScrollPane.getStylesheets().add(styleSheet);
    }

    public void initializeSheet(SheetDTO sheetDTO) {
        sheetController = new sheetControllerImpl();
        sheetGrid = sheetController.createDynamicGrid(sheetDTO);
        flowPane.getChildren().clear();
        sheetGrid.setAlignment(Pos.CENTER);
        flowPane.getChildren().add(sheetGrid);
        sheetUIModel = sheetController.getSheetUIModel();
        sheetDesignController.setSheetUIModel(sheetUIModel);
        sheetGrid.visibleProperty().setValue(true);
        rangePanelController.populateRangeComboBox(sheetDTO.getRanges().keySet());
        VboxStyleMenu.setDisable(false);
        leftMenuVbox.setDisable(false);
        sheetUIModel.getSelectedCellProperty().addListener((obs, oldCellId, newCellId) -> {
            if (newCellId != null) {
                handleCellClick(newCellId);
            }
        });
    }

    public void updateToANewVersion(SheetDTO newSheetVersionDTO){
        updateAllCellContent(newSheetVersionDTO);
        rangePanelController.populateRangeComboBox(newSheetVersionDTO.getRanges().keySet());
    }


    private void handleCellClick(Label cellLabel) {
        String cellId = (String) cellLabel.getUserData();
        String finalUrl = Constants.CELL_OPERATION_URL + "?cellId=" + cellId;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to get cell value: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        CellDTO cellDTO = Constants.GSON_INSTANCE.fromJson(responseBody, CellDTO.class);
                        Platform.runLater(() -> {
                            updateHeaderUIModel(cellId);
                            sheetController.handleCellClick(cellDTO);
                            updateSheetDesignUI();
                            updateSelectedCellStyle();
                            if (cellDTO != null) {
                                updateCellInfoInHeader(cellDTO);
                            } else {
                                setEmptyCellInfoInHeader();
                            }
                            boolean isColorFillOn = sheetUIModel.getColorFillStatus(cellId);
                            sheetDesignController.updateToggleFillState(isColorFillOn);
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup(errorMessage));
                    }
                }
            }
        });
    }

    private void updateHeaderUIModel(String cellId) {
        headerUIModel.setSelectedCellID(cellId);
    }

    private void updateSheetDesignUI() {
        int columnWidth = sheetController.getSelectedCellColumnWidth();
        int rowHeight = sheetController.getSelectedCellRowHeight();
        sheetDesignController.updateSpinnersForSelectedCell(columnWidth, rowHeight);
        updateAlignmentForSelectedCell(sheetUIModel.getSelectedCellProperty().get().getAlignment());
    }

    private void updateSelectedCellStyle() {
        if (sheetUIModel.getSelectedCellProperty().get() != null) {
            Label selectedCell = sheetUIModel.getSelectedCellProperty().get();
            Paint backgroundFill = selectedCell.getBackground() != null ? selectedCell.getBackground().getFills().getFirst().getFill() : Color.WHITE;
            Color textColor = (Color) selectedCell.getTextFill();
            sheetDesignController.updateColorPickersForSelectedCell((Color) backgroundFill, textColor);
        }
    }

    private void updateCellInfoInHeader(CellDTO cellDTO) {
        headerUIModel.setOriginalCellValue(cellDTO.getOriginalValue());
        headerUIModel.setLastUpdate(Integer.toString(cellDTO.getLastModifiedVersion()));
        headerUIModel.setUpdateByUserName(cellDTO.getUpdatedByUser());
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

    public void onApplyColor(Color newBackgroundColor,Color newTextColor){
        if(sheetUIModel.getSelectedCellProperty().get()!= null) {
            sheetController.setSelectedCellTextColor(newTextColor);
            sheetController.setSelectedCellBackgroundColor(newBackgroundColor);
        }
    }


    public void onApplyTextColor(Color newTextColor) {
        if (sheetUIModel.getSelectedCellProperty().get() != null) {
            sheetController.setSelectedCellTextColor(newTextColor);
            String cellId = (String) sheetUIModel.getSelectedCellProperty().get().getUserData();
            String colorHex = ColorUtil.colorToHex(newTextColor);

            updateCellTextColor(cellId, colorHex, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to update text color: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to update text color: " + errorMessage));
                    }
                }
            });
        }
    }


    public void onApplyBackgroundColor(Color newBackgroundColor) {
        if (sheetUIModel.getSelectedCellProperty().get() != null) {
            sheetController.setSelectedCellBackgroundColor(newBackgroundColor);
            String cellId = (String) sheetUIModel.getSelectedCellProperty().get().getUserData();
            String colorHex = ColorUtil.colorToHex(newBackgroundColor);
            updateCellBackgroundColor(cellId, colorHex, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to update background color: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to update background color: " + errorMessage));
                    }
                }
            });
        }
    }

    public void updateCellTextColor(String cellId, String colorHex, Callback callback) {
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_CELL_STYLE_URL)
                .newBuilder()
                .addQueryParameter(Constants.UPDATE_TYPE, "text")
                .build()
                .toString();

        UpdateCellStyleRequestDTO updateCellStyleRequestDTO = new UpdateCellStyleRequestDTO(cellId, colorHex);
        String jsonBody = Constants.GSON_INSTANCE.toJson(updateCellStyleRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
    }

    public void updateCellBackgroundColor(String cellId, String colorHex, Callback callback) {
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_CELL_STYLE_URL)
                .newBuilder()
                .addQueryParameter(Constants.UPDATE_TYPE, "background")
                .build()
                .toString();
        UpdateCellStyleRequestDTO updateCellStyleRequestDTO = new UpdateCellStyleRequestDTO(cellId, colorHex);
        String jsonBody = Constants.GSON_INSTANCE.toJson(updateCellStyleRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
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
            }
    }

    public void updateCell(String newValue, Callback callback) {
        String cellID = (String) sheetUIModel.getSelectedCellProperty().get().getUserData();
        String finalUrl = Constants.CELL_OPERATION_URL;
        CellUpdateRequestDTO cellUpdateRequestDTO = new CellUpdateRequestDTO(cellID, newValue);
        String jsonBody = Constants.GSON_INSTANCE.toJson(cellUpdateRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
    }

    public void updateContentAfterCreateNewRange(SheetDTO sheetDTO, String rangeName){
        sheetController.updateAllCellContent(sheetDTO);
        rangePanelController.addNewRangeToComboBox(rangeName);
    }

    public void createNewRange(String rangeName, String from, String to, Callback callback) {
        String finalUrl = Constants.RANGE_OPERATION_URL;
        AddNewRangeRequestDTO addNewRangeRequestDTO = new AddNewRangeRequestDTO(rangeName, from, to);
        String jsonBody = Constants.GSON_INSTANCE.toJson(addNewRangeRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
    }

    public void showSelectRange(RangeDTO selectedRange){
        sheetController.showSelectRange(selectedRange);
    }

    public void viewRange(String rangeName, Callback callback) {
        String finalUrl = Constants.RANGE_OPERATION_URL + "?rangeName=" + rangeName;
        HttpClientUtil.runAsync(finalUrl, callback);
    }


    public void resetSelectRange(){
        sheetController.resetSelectedRange();
    }


    public void deleteRange(String rangeName, Callback callback) {
        String finalUrl = Constants.RANGE_OPERATION_URL + "?rangeName=" + rangeName;
        HttpClientUtil.runAsyncDelete(finalUrl, callback);
    }

    public void updateContentAfterDeleteRange(SheetDTO sheetDTO, String rangeName) {
        sheetController.updateAllCellContent(sheetDTO);
        rangePanelController.removeRangeFromComboBox(rangeName);
    }


    public void updateTemporaryValuesInSheet(Map<String, String> updatedCells, Callback callback) {
        String finalUrl = Constants.DYNAMIC_ANALYSIS;
        UpdateTemporaryValuesInSheetRequestDTO updateRequestDTO = new UpdateTemporaryValuesInSheetRequestDTO(updatedCells);

        String jsonBody = Constants.GSON_INSTANCE.toJson(updateRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
    }

    public  void updateAllCellContent(SheetDTO sheetDTO){
        sheetController.updateAllCellContent(sheetDTO);

    }

    public void isValidBoundaries(String from, String to, Callback callback) {
        String finalUrl = Constants.VALIDATE_BOUNDARIES_URL + "?from=" + from + "&to=" + to;
        HttpClientUtil.runAsync(finalUrl, callback);
    }


    public void sortRows(String fromRange, String toRange, List<Character> selectedColumns, Callback callback) {
        String finalUrl = Constants.SORT_ROWS_URL;
        SortRequestDTO sortRequestDTO = new SortRequestDTO(fromRange, toRange, selectedColumns);
        String jsonBody = Constants.GSON_INSTANCE.toJson(sortRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
    }


    public void getUniqueValuesForColumn(Character column, String fromCoordinate, String toCoordinate, Callback callback) {
        String finalUrl = Constants.FILTER_SHEET_URL + "?column=" + column + "&fromCoordinate=" + fromCoordinate + "&toCoordinate=" + toCoordinate;
        HttpClientUtil.runAsync(finalUrl, callback);
    }

    public void filterSheet(String from, String to, Map<Character, List<String>> selectedColumns2UniqueValues, Callback callback) {
        String finalUrl = Constants.FILTER_SHEET_URL;
        FilterRequestDTO filterRequestDTO = new FilterRequestDTO(from, to, selectedColumns2UniqueValues);
        String jsonBody = Constants.GSON_INSTANCE.toJson(filterRequestDTO);
        HttpClientUtil.runAsyncPost(finalUrl, jsonBody, callback);
    }

    public void getSheetByVersion(Integer versionNumber, Callback callback) {
        String finalUrl = Constants.GET_SHEET_BY_VERSION_URL + "?versionNumber=" + versionNumber;
        HttpClientUtil.runAsync(finalUrl, callback);
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

    public void hideSlider(Map<String, String> cellOriginalValues) {
        if (dynamicSlider != null) {
            topAppVbox.getChildren().remove(dynamicSlider);
            dynamicSlider.setVisible(false);
            dynamicSlider.setOpacity(0);
        }
        topAppVbox.getChildren().add(VboxStyleMenu);
        VboxStyleMenu.setOpacity(1);
        header.disableProperty().setValue(false);
        leftMenuVbox.setDisable(false);

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to reset cell value: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        SheetDTO updatedSheet = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                        Platform.runLater(() -> sheetController.updateAllCellContent(updatedSheet));
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to reset cell value: " + errorMessage));
                    }
                }
            }
        };
        updateTemporaryValuesInSheet(cellOriginalValues, callback);
    }

    public void setMainController(AppMainController mainController){
        this.mainController= mainController;
    }

    public void disableControlsForReader(){
        headerController.disableControlsForReader();
        rangePanelController.disableControlsForReader();
        sheetDesignController.disableControlsForReader();
    }
    public void enableControlsForWriter(){
        headerController.enableControlsForWriter();
        rangePanelController.enableControlsForWriter();
        sheetDesignController.enableControlsForWriter();
    }

    public boolean isCellSelected(){
        return !sheetUIModel.getSelectedCellProperty().isNull().get();
    }

    public void startRefreshers(){
        headerController.initializeAndStartRefreshers();
    }

    @FXML
    public void handleBackToDashboardAction(ActionEvent event) {
        headerController.stopRefreshers();

        String finalUrl = Constants.CLEAR_SESSION_URL;
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to clear session: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> mainController.switchToMainAppView());
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to clear session: " + errorMessage));
                    }
                }
            }
        });
    }

    public SheetDetailsDTO getSelectedSheet(){
        return mainController.getSelectedSheet();
    }

    public void updateHeaderInfo() {
        Label selectedCell = sheetUIModel.getSelectedCellProperty().get();

        if (selectedCell == null) {
            return;
        }
        String cellId =(String) sheetUIModel.getSelectedCellProperty().get().getUserData();
        String finalUrl = Constants.CELL_OPERATION_URL + "?cellId=" + cellId;

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to get cell value: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        CellDTO cellDTO = Constants.GSON_INSTANCE.fromJson(responseBody, CellDTO.class);
                        Platform.runLater(() -> updateCellInfoInHeader(cellDTO));
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to get cell value: " + errorMessage));
                    }
                }
            }
        });
    }

    public void setCurrentUserName(String userName){
        headerController.setCurrentUserName(userName);
    }

    @Override
    public void close() throws IOException {
        headerController.close();
    }
}



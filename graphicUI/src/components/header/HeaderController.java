package components.header;

import AnimationUtil.AnimationResolver;
import components.sheet.SheetController;
import components.sheet.sheetControllerImpl;
import components.viewSheetMain.ViewSheetMainController;
import engine.SheetDTO;
import engine.SheetDetailsDTO;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import util.http.HttpClientUtil;
import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import static util.Constants.REFRESH_RATE;

public class HeaderController implements Closeable {
    @FXML private Label LastUpdateCellVersionLabel;
    @FXML private Label OriginalCellValueLabel;
    @FXML private Label SelectedCellIDLabel;
    @FXML private Label updateByUserLabel;
    @FXML private Button UpdateValueButton;
    @FXML private ComboBox<Integer> VersionSelector;
    @FXML private GridPane mainContinaerGridPane;
    @FXML private Button latestVersionButton;
    @FXML private Label nameLabel;


    private HeaderUIModel headerUIModel;
    private ChangeListener<Integer> VersionSelectorListener;
    private ViewSheetMainController mainController;
    private Timer versionCheckTimer;
    private VersionComboBoxRefresher versionComboBoxRefresher;
    private IsVersionUpToDateRefresher isVersionUpToDateRefresher;
    private final BooleanProperty autoUpdate = new SimpleBooleanProperty(true);

    @FXML
    private void initialize() {
        initializeHeaderUIModel();
        initializeListeners();
        applyThemeListener();
        applyButtonEffects();
    }

    private void initializeHeaderUIModel() {
        headerUIModel = new HeaderUIModel(
                LastUpdateCellVersionLabel,
                OriginalCellValueLabel,
                SelectedCellIDLabel,
                VersionSelector,
                updateByUserLabel
        );
    }

    private void initializeListeners() {
        VersionSelectorListener = (observable, oldValue, newValue) -> handleVersionSelection();
        VersionSelector.getSelectionModel().selectedItemProperty().addListener(VersionSelectorListener);
    }

    private void applyThemeListener() {
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
    }

    private void applyButtonEffects() {
        AnimationResolver.assignHoverEffect(UpdateValueButton);
    }


    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("design/header_" + newStyle + ".css").toExternalForm();
        mainContinaerGridPane.getStylesheets().clear();
        mainContinaerGridPane.getStylesheets().add(styleSheet);
    }


    @FXML
    void UpdateValueButtonActionListener(ActionEvent event) {
        if (mainController.isCellSelected()) {
            mainController.handleUpdateCellButton();
        }
        else{
            PopupManager.showErrorPopup("Please select cell first.");
        }
    }

    public void updateVersionSelectorOptions(ObservableList<Integer> integerValues){
        VersionSelector.getItems().clear();
        VersionSelector.getItems().addAll(integerValues);
    }

    private void handleVersionSelection() {
        if (VersionSelector.getValue() != null) {
            Integer selectedVersion = VersionSelector.getValue();

            mainController.getSheetByVersion(selectedVersion, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to load version: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (response) {
                        if (response.isSuccessful()) {
                            String responseBody = response.body() != null ? response.body().string() : "";
                            SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                            Platform.runLater(() -> showSelectedVersionSheet(sheetDTO));
                        } else {
                            String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                            Platform.runLater(() -> PopupManager.showErrorPopup("Failed to load version: " + errorMessage));
                        }
                    }
                }
            });
        }
    }


    private void showSelectedVersionSheet(SheetDTO sheetDTO) {
        FlowPane flowPane = new FlowPane();
        GridPane sheetGrid = createSheetGrid(sheetDTO);
        flowPane.getChildren().add(sheetGrid);

        Stage versionStage = createVersionStage(sheetDTO, flowPane);
        configureVersionStageCloseEvent(versionStage);

        versionStage.show();
    }
    private GridPane createSheetGrid(SheetDTO sheetDTO) {
        SheetController sheetController = new sheetControllerImpl();
        return sheetController.createDynamicGrid(sheetDTO);
    }
    private Stage createVersionStage(SheetDTO sheetDTO, FlowPane flowPane) {
        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage stage = new Stage();
        stage.setTitle("Sheet Version - " + sheetDTO.getVersionNumber());
        stage.setScene(new Scene(scrollPane));

        return stage;
    }

    private void configureVersionStageCloseEvent(Stage stage) {
        stage.setOnCloseRequest(event -> resetVersionSelector());
    }

    private void resetVersionSelector() {
        VersionSelector.getSelectionModel().selectedItemProperty().removeListener(VersionSelectorListener);
        VersionSelector.setValue(null);
        resetVersionSelectorButtonCell();
        VersionSelector.getSelectionModel().selectedItemProperty().addListener(VersionSelectorListener);
    }

    private void resetVersionSelectorButtonCell() {
        VersionSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Version Selector" : item.toString());
            }
        });
    }

    public void setMainController(ViewSheetMainController mainController) {
        this.mainController = mainController;
    }

    public HeaderUIModel getHeaderUIModel() {
        return headerUIModel;
    }

    public void disableControlsForReader(){
        UpdateValueButton.disableProperty().setValue(true);
    }

    public void enableControlsForWriter(){
        UpdateValueButton.disableProperty().setValue(false);
    }

    @FXML
    void handleLatestVersionAction(ActionEvent event) {
        if (mainController != null) {
            SheetDetailsDTO selectedSheet = mainController.getSelectedSheet();
            if (selectedSheet == null) {
                PopupManager.showErrorPopup("Please select a sheet to view.");
                return;
            }
            String finalUrl = Constants.SHEET_OPERATION_URL + "?sheet_name=" + selectedSheet.getSheetName();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to load the latest version: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (response) {
                        if (response.isSuccessful()) {
                            String responseBody = response.body() != null ? response.body().string() : "";
                            SheetDTO sheetDTO = Constants.GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                            Platform.runLater(() -> {
                                mainController.updateToANewVersion(sheetDTO);
                                mainController.updateHeaderInfo();
                            });
                        } else {
                            String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                            Platform.runLater(() -> PopupManager.showErrorPopup("Failed to load the latest version: " + errorMessage));
                        }
                    }
                }
            });
        }
    }


    private void updateLatestVersionButtonStyle(boolean isUpToDate) {
        Platform.runLater(() -> {
            if (!isUpToDate) {
                latestVersionButton.getStyleClass().removeAll("latest-version-button-no-update");
                latestVersionButton.getStyleClass().add("latest-version-button-update");
                latestVersionButton.setText("Click for Latest Version");
                latestVersionButton.setDisable(false);
            } else {
                latestVersionButton.getStyleClass().removeAll("latest-version-button-update");
                latestVersionButton.getStyleClass().add("latest-version-button-no-update");
                latestVersionButton.setText("Up-to-Date");
                latestVersionButton.setDisable(true);
            }
        });
    }

    public void initializeAndStartRefreshers() {
        if (versionCheckTimer == null) {
            versionCheckTimer = new Timer();
        }

        startVersionRefresher();
        startVersionComboBoxRefresher();
    }

    public void startVersionRefresher() {
        isVersionUpToDateRefresher = new IsVersionUpToDateRefresher(autoUpdate, this::updateLatestVersionButtonStyle);
        versionCheckTimer.schedule(isVersionUpToDateRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void startVersionComboBoxRefresher() {
        versionComboBoxRefresher = new VersionComboBoxRefresher(autoUpdate, this::updateVersionSelectorOptions);
        versionCheckTimer.schedule(versionComboBoxRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void stopRefreshers() {
        if (versionCheckTimer != null) {
            versionCheckTimer.cancel();
            versionCheckTimer = null;
        }
    }

    public void setCurrentUserName(String userName){
        nameLabel.setText(userName);
    }

    @Override
    public void close() {
        if (versionCheckTimer != null) {
            if (isVersionUpToDateRefresher != null) {
                isVersionUpToDateRefresher.cancel();
            }
            if (versionComboBoxRefresher != null) {
                versionComboBoxRefresher.cancel();
            }
            versionCheckTimer.cancel();
        }
    }
}
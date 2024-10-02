package components.header;
import AnimationUtil.AnimationResolver;
import components.app.AppController;
import components.sheet.SheetController;
import components.sheet.sheetControllerImpl;
import engine.SheetDTO;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HeaderController {
    @FXML private Label FilePathLabel;
    @FXML private Label LastUpdateCellVersionLabel;
    @FXML private Button LoadFileButton;
    @FXML private Label OriginalCellValueLabel;
    @FXML private Label SelectedCellIDLabel;
    @FXML private Button UpdateValueButton;
    @FXML private ComboBox<Integer> VersionSelector;
    @FXML private GridPane mainContinaerGridPane;

    private final BooleanProperty updateValueButtonDisabled = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty isFileSelected;
    private HeaderUIModel headerUIModel;
    private ChangeListener<Integer> VersionSelectorListener;
    private AppController mainController;

    @FXML
    private void initialize() {
        isFileSelected = new SimpleBooleanProperty(false);
        VersionSelector.disableProperty().bind(isFileSelected.not());
        initializeHeaderUIModel();
        initializeListeners();
        applyThemeListener();

        applyButtonEffects();
        configureVersionSelector();
        bindUpdateValueButton();
    }

    private void initializeHeaderUIModel() {
        headerUIModel = new HeaderUIModel(
                FilePathLabel,
                LastUpdateCellVersionLabel,
                OriginalCellValueLabel,
                SelectedCellIDLabel,
                isFileSelected,
                VersionSelector
        );
    }

    private void initializeListeners() {
        VersionSelectorListener = (observable, oldValue, newValue) -> handleVersionSelection();
        VersionSelector.getSelectionModel().selectedItemProperty().addListener(VersionSelectorListener);
    }

    private void applyThemeListener() {
        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
    }

    private void applyButtonEffects() {
        AnimationResolver.assignHoverEffect(LoadFileButton);
        AnimationResolver.assignHoverEffect(UpdateValueButton);
    }

    private void configureVersionSelector() {
        VersionSelector.disableProperty().bind(isFileSelected.not());
    }

    private void bindUpdateValueButton() {
        UpdateValueButton.disableProperty().bind(updateValueButtonDisabled);
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("header_" + newStyle + ".css").toExternalForm();
        mainContinaerGridPane.getStylesheets().clear();
        mainContinaerGridPane.getStylesheets().add(styleSheet);
    }

    @FXML
    void LoadFileButtonActionListener(ActionEvent event) {
        File selectedFile = showFileChooser();
        if (selectedFile != null) {
            mainController.loadFile(selectedFile);
            initializeVersionSelectorWithDefault();
        }
    }

    private File showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        return fileChooser.showOpenDialog(mainController.getPrimaryStage());
    }

    private void initializeVersionSelectorWithDefault() {
        VersionSelector.getItems().addAll(1);
    }

    @FXML
    void UpdateValueButtonActionListener(ActionEvent event) {
        mainController.handleUpdateCellButton();
    }

    public void updateVersionSelectorOptions(ObservableList<Integer> integerValues){
        VersionSelector.getItems().clear();
        VersionSelector.getItems().addAll(integerValues);
    }

    private void handleVersionSelection() {
        if (VersionSelector.getValue() != null) {
            SheetDTO selectedVersion = mainController.getSheetByVersion(VersionSelector.getValue());
            showSelectedVersionSheet(selectedVersion);
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

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public BooleanProperty getUpdateValueButtonDisabled(){
        return updateValueButtonDisabled;
    }

    public HeaderUIModel getHeaderUIModel() {
        return headerUIModel;
    }
}
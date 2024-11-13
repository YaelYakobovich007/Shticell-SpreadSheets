package components.appMain;
import AnimationUtil.PopUpWindowAnimator;
import components.permissionAckDeny.PermissionAckDenyController;
import components.permissionsList.PermissionsListController;
import components.requestPermission.RequestPermissionController;
import components.sheetsList.SheetsListController;
import components.viewSheetMain.ViewSheetMainController;
import engine.SheetDTO;
import engine.SheetDetailsDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import util.http.HttpClientUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import static util.Constants.GSON_INSTANCE;
import static util.Constants.JHON_DOE;

public class AppMainController implements Closeable {
    @FXML private BorderPane mainPanel;
    @FXML private ScrollPane mainScrollPane;
    @FXML private Label userGreetingLabel;
    @FXML private VBox sheetsList;
    @FXML private SheetsListController sheetsListController;
    @FXML private VBox permissionsList;
    @FXML private PermissionsListController permissionsListController;
    private Parent viewSheetMainComponent;
    private ViewSheetMainController viewSheetMainComponentController;
    private final StringProperty currentUserName;
    private Stage primaryStage;

    public AppMainController() {
        currentUserName = new SimpleStringProperty(JHON_DOE);
    }

    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));

        if(sheetsListController!= null){
            sheetsListController.setMainController(this);
        }
        if(permissionsListController!= null){
            permissionsListController.setMainController(this);
        }
        initializeViewSheetMainComponent();
    }

    private void initializeViewSheetMainComponent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/viewSheetMain/viewSheetMain.fxml"));
            viewSheetMainComponent = loader.load();
            viewSheetMainComponentController = loader.getController();
            if (viewSheetMainComponentController != null) {
                viewSheetMainComponentController.setMainController(this);
            }
        } catch (IOException e) {
            Platform.runLater(() -> PopupManager.showErrorPopup(e.getMessage()));
        }
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
        viewSheetMainComponentController.setCurrentUserName(userName);
    }

    private void setMainScrollPaneContent(Parent pane) {
        mainScrollPane.setContent(pane);
    }

    public void switchToMainAppView() {
        mainPanel.setVisible(true);
        setMainScrollPaneContent(mainPanel);
    }

    private File showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        return fileChooser.showOpenDialog(primaryStage);
    }

    @FXML
    void handleLoadSheetFileAction(ActionEvent event) {
        File selectedFile = showFileChooser();
        if (selectedFile != null) {
            String finalUrl = HttpUrl
                    .parse(Constants.SHEET_OPERATION_URL)
                    .newBuilder()
                    .build()
                    .toString();
            HttpClientUtil.runAsyncWithFileUpload(finalUrl, selectedFile, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("File upload failed: " + e.getMessage()));
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (response) {
                        if (response.isSuccessful()) {
                            Platform.runLater(() -> PopupManager.showSuccessPopup("File uploaded successfully"));

                        } else {
                            System.out.println("File upload failed");
                            String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                            Platform.runLater(() -> PopupManager.showErrorPopup("File upload failed: " + errorMessage));
                        }
                    } catch (Exception e) {
                        System.out.println("Exception occurred while processing response: " + e.getMessage());
                    }
                }

            });
        }
    }

    @FXML
    void handleViewSheetAction(ActionEvent event) {
        SheetDetailsDTO selectedSheet = sheetsListController.getSelectedSheet();
        if (selectedSheet == null) {
            PopupManager.showErrorPopup("Please select a sheet to view.");
            return;
        }
        String finalUrl = HttpUrl.parse(Constants.SHEET_OPERATION_URL)
                .newBuilder()
                .addQueryParameter("sheet_name", selectedSheet.getSheetName())
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        SheetDTO sheetDTO = GSON_INSTANCE.fromJson(responseBody, SheetDTO.class);
                        Platform.runLater(() -> {
                            viewSheetMainComponentController.initializeSheet(sheetDTO);
                            viewSheetMainComponentController.startRefreshers();
                            handleUserPermission(sheetDTO.getUserPermission());
                            setMainScrollPaneContent(viewSheetMainComponent);
                            viewSheetMainComponentController.setCurrentUserName(currentUserName.getValue());
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup(errorMessage));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to process the response: " + e.getMessage()));
                }
            }
        });
    }

    public  void handleUserPermission(String userPermission){
        switch (userPermission.toUpperCase()) {
            case "WRITER" -> viewSheetMainComponentController.enableControlsForWriter();
            case "READER" -> viewSheetMainComponentController.disableControlsForReader();
            case "NONE" -> PopupManager.showErrorPopup("You do not have permission to view this sheet.");
        }
    }

    @FXML
    void handleRequestPermission(ActionEvent event){
        SheetDetailsDTO selectedSheet = sheetsListController.getSelectedSheet();
        if (selectedSheet == null) {
            PopupManager.showErrorPopup("Please select a sheet from the list first ");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/requestPermission/requestPermission.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Request Permission");
            stage.setScene(new javafx.scene.Scene(root));
            RequestPermissionController requestPermissionController = loader.getController();
            requestPermissionController.setMainController(this);
            requestPermissionController.setStage(stage);
            requestPermissionController.setSelectedSheetName(sheetsListController.getSelectedSheet().getSheetName());
            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);
            stage.show();

        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading Request Permission window.");
        }
    }

    @FXML
    void handleAckOrDenyPermissionRequest(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/permissionAckDeny/permissionAckDeny.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Approve or Deny Requests");
            stage.setScene(new javafx.scene.Scene(root));
            PermissionAckDenyController permissionAckDenyController = loader.getController();
            permissionAckDenyController.setMainController(this);
            permissionAckDenyController.setStage(stage);
            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);
            viewSheetMainComponentController.setCurrentUserName(currentUserName.getName());
            Platform.runLater(permissionAckDenyController::loadPendingRequests);

            stage.show();
        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading permission ack / deny  window.");
        }
    }

    public  void startListRefresher(){
        sheetsListController.startListRefresher();
    }

    public void updateSelectedSheet(SheetDetailsDTO selectedSheet) {
        if (selectedSheet != null && permissionsListController != null) {
            permissionsListController.updateSheetName(selectedSheet.getSheetName());
        }
    }

    public  SheetDetailsDTO  getSelectedSheet(){
       return sheetsListController.getSelectedSheet();
    }

    @Override
    public void close() throws IOException {
        if (viewSheetMainComponentController != null) {
            viewSheetMainComponentController.close();
        }
        if (sheetsListController != null) {
            sheetsListController.close();
        }
        if (permissionsListController != null) {
            permissionsListController.close();
        }
    }
}

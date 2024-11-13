package components.permissionAckDeny;

import com.google.gson.Gson;
import components.appMain.AppMainController;
import engine.PermissionRequestDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PermissionAckDenyController {
    @FXML private TableView<PermissionRequestDTO> permissionRequestsTable;
    @FXML private TableColumn<PermissionRequestDTO, String> usernameColumn;
    @FXML private TableColumn<PermissionRequestDTO, String> sheetNameColumn;
    @FXML private TableColumn<PermissionRequestDTO, String> requestTypeColumn;
    @FXML private TableColumn<PermissionRequestDTO, Void> actionColumn;

    private Timer timer;
    private final SimpleBooleanProperty autoUpdate;
    private static final Gson GSON = new Gson();
    private AppMainController mainController;
    private Stage stage;

    public PermissionAckDenyController() {
        autoUpdate = new SimpleBooleanProperty(true);
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        requestTypeColumn.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        actionColumn.setPrefWidth(200);
        addActionButtons();
    }

    public void startListRefresher() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (autoUpdate.get()) {
                    loadPendingRequests();
                }
            }
        }, 0, Constants.REFRESH_RATE);
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new javafx.scene.control.TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button denyButton = new Button("Deny");

            {
                approveButton.setOnAction(event -> {
                    PermissionRequestDTO request = getTableView().getItems().get(getIndex());
                    processRequest(request, "APPROVED");
                });

                denyButton.setOnAction(event -> {
                    PermissionRequestDTO request = getTableView().getItems().get(getIndex());
                    processRequest(request, "REJECTED");
                });

                approveButton.setStyle("-fx-background-radius: 20; -fx-padding: 5;");
                denyButton.setStyle("-fx-background-radius: 20; -fx-padding: 5;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox hBox = new javafx.scene.layout.HBox(15, approveButton, denyButton);
                    hBox.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(hBox);
                }
            }
        });
    }

    private void processRequest(PermissionRequestDTO requestDTO, String status) {
        String requestUrl = Constants.PERMISSIONS_OPERATION_URL + "?status=" + status;
        RequestBody body = RequestBody.create(GSON.toJson(requestDTO), MediaType.parse("application/json"));

        HttpClientUtil.runAsyncPut(requestUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> PopupManager.showErrorPopup("Failed to process request: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            permissionRequestsTable.getItems().remove(requestDTO);
                            PopupManager.showSuccessPopup("Request processed successfully");
                        });
                    } else {
                        String responseBody = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to process request: " + responseBody));
                    }
                }
            }
        });
    }

    public void loadPendingRequests() {
        HttpClientUtil.runAsync(Constants.GET_PENDING_REQUESTS_URL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Failed to refresh permission requests: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (response) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        PermissionRequestDTO[] requests = GSON.fromJson(responseBody, PermissionRequestDTO[].class);
                        Platform.runLater(() -> permissionRequestsTable.setItems(javafx.collections.FXCollections.observableArrayList(requests)));
                    } else {
                        System.out.println("Failed to refresh permission requests: " + response.message());
                    }
                }
            }
        });
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        if (timer != null) {
            timer.cancel();
        }
    }
}

package components.permissionsList;

import components.appMain.AppMainController;
import engine.PermissionRequestDTO;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import static util.Constants.REFRESH_RATE;

public class PermissionsListController implements Closeable {
    private Timer timer;
    private PermissionsListRefresher listRefresher;
    private final BooleanProperty autoUpdate;
    private AppMainController mainController;

    @FXML private TableView<PermissionRequestDTO> permissionTableView;
    @FXML private TableColumn<PermissionRequestDTO, String> usernameColumn;
    @FXML private TableColumn<PermissionRequestDTO, String> requestTypeColumn;
    @FXML private TableColumn<PermissionRequestDTO, String> statusColumn;
    @FXML private Label permissionDetailsLabel;

    public PermissionsListController() {
        autoUpdate = new SimpleBooleanProperty(true);
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        requestTypeColumn.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        listRefresher = new PermissionsListRefresher(autoUpdate, this::updatePermissionsList, "");
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void updateSheetName(String newSheetName) {
        if (listRefresher != null) {
            listRefresher.updateSheetName(newSheetName);
        }
    }

    private void updatePermissionsList(List<PermissionRequestDTO> permissions) {
        Platform.runLater(() -> {
            ObservableList<PermissionRequestDTO> items = FXCollections.observableArrayList(permissions);
            permissionTableView.setItems(items);
            permissionDetailsLabel.setText("Permissions List: (" + permissions.size() + ")");
        });
    }

    public void setMainController(AppMainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void close() {
        permissionTableView.getItems().clear();
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
}

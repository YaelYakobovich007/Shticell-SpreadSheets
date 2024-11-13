package components.sheetsList;

import components.appMain.AppMainController;
import engine.SheetDetailsDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.Closeable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.REFRESH_RATE;

public class SheetsListController  implements Closeable {
    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalSheets;
    private AppMainController mainController;

    @FXML private TableView<SheetDetailsDTO> sheetsTableView;
    @FXML private TableColumn<SheetDetailsDTO, String> userColumn;
    @FXML private TableColumn<SheetDetailsDTO, String> sheetNameColumn;
    @FXML private TableColumn<SheetDetailsDTO, String> sheetSizeColumn;
    @FXML private TableColumn<SheetDetailsDTO, String> permissionTypeColumn;
    @FXML private Label sheetDetailsLabel;

    public SheetsListController() {
        autoUpdate = new SimpleBooleanProperty(true);
        totalSheets = new SimpleIntegerProperty();
    }

    @FXML
    public void initialize() {
        sheetDetailsLabel.textProperty().bind(Bindings.concat("Sheets List: (", totalSheets.asString(), ")"));

        userColumn.setCellValueFactory(new PropertyValueFactory<>("uploadedByUser"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("sheetSize"));
        permissionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userPermission"));

        sheetsTableView.setRowFactory(tv -> {
            TableRow<SheetDetailsDTO> row = new TableRow<>() {
                @Override
                protected void updateItem(SheetDetailsDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (isSelected()) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else {
                        setStyle("");
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    SheetDetailsDTO clickedSheet = row.getItem();
                    sheetsTableView.getSelectionModel().select(row.getIndex());
                    mainController.updateSelectedSheet(clickedSheet);
                }
            });
            return row;
        });
    }



    private void updateSheetsList(List<SheetDetailsDTO> sheets) {
        Platform.runLater(() -> {
            SheetDetailsDTO selectedSheet = sheetsTableView.getSelectionModel().getSelectedItem();
            ObservableList<SheetDetailsDTO> items = FXCollections.observableArrayList(sheets);
            sheetsTableView.setItems(items);
            totalSheets.set(sheets.size());

            if (selectedSheet != null) {
                for (SheetDetailsDTO sheet : items) {
                    if (sheet.getSheetName().equals(selectedSheet.getSheetName())) {
                        sheetsTableView.getSelectionModel().select(sheet);
                        break;
                    }
                }
            }
        });
    }


    public void startListRefresher() {
        listRefresher = new SheetListRefresher(
                autoUpdate,
                this::updateSheetsList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        sheetsTableView.getItems().clear();
        totalSheets.set(0);
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
    public  void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }

    public SheetDetailsDTO getSelectedSheet() {
        return sheetsTableView.getSelectionModel().getSelectedItem();
    }



}

package components.rangePanel;
import AnimationUtil.AnimationResolver;
import AnimationUtil.PopUpWindowAnimator;
import components.NewRangePopup.NewRangePopupController;
import components.deleteRangePopUp.DeleteRangePopUpController;
import engine.RangeDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import components.viewSheetMain.ViewSheetMainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.PopupManager;
import java.io.IOException;
import java.util.Set;

public class RangePanelController {
    @FXML private Button AddNewRangeButton;
    @FXML private Button RemoveExistingRangeButton;
    @FXML private ComboBox<String> viewRangeComboBox;
    @FXML private VBox mainContianerVbox;
    private ViewSheetMainController mainController;

    @FXML
    private void initialize() {
        AnimationResolver.assignHoverEffect(AddNewRangeButton);
        AnimationResolver.assignHoverEffect(RemoveExistingRangeButton);
        ViewSheetMainController.themeProperty().addListener((obs, oldTheme, newTheme) -> updateStyleSheet(newTheme));
    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/leftMenuDesign/leftMenu_" + newStyle + ".css").toExternalForm();
        mainContianerVbox.getStylesheets().clear();
        mainContianerVbox.getStylesheets().add(styleSheet);
    }
    @FXML
    void handleAddNewRangeAction(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/NewRangePopup/newRangePopup.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Range");
            stage.setScene(new javafx.scene.Scene(root));
            NewRangePopupController newRangePopupController = loader.getController();
            newRangePopupController.setMainController(mainController);
            newRangePopupController.setStage(stage);

            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);

            stage.show();
        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading new Range window.");
        }
    }

    @FXML
    void handleRemoveExistingRangeAction(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/deleteRangePopUp/deleteRangePopUp.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Remove Existing Range");
            stage.setScene(new javafx.scene.Scene(root));
            DeleteRangePopUpController deleteRangePopUpController = loader.getController();
            deleteRangePopUpController.setMainController(mainController);
            deleteRangePopUpController.setStage(stage);
            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);
            stage.show();
        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading Remove Range window.");
        }
    }

    @FXML
    void handleViewRangeAction(ActionEvent event) {
        String selectedRangeName = viewRangeComboBox.getValue();
        if (selectedRangeName == null || "none".equals(selectedRangeName)) {
            Platform.runLater(() -> {
                mainController.resetSelectRange();
            });
        } else {
            mainController.viewRange(selectedRangeName, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> PopupManager.showErrorPopup("Failed to view range: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        RangeDTO selectedRange = Constants.GSON_INSTANCE.fromJson(responseBody, RangeDTO.class);
                        Platform.runLater(() -> mainController.showSelectRange(selectedRange));
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> PopupManager.showErrorPopup("Failed to view range: " + errorMessage));
                    }
                }
            });
        }
    }

    public void setMainController(ViewSheetMainController mainController) {
        this.mainController = mainController;
    }

    public void populateRangeComboBox(Set<String> ranges){

        viewRangeComboBox.getItems().clear();
        viewRangeComboBox.getItems().addAll(ranges);
        viewRangeComboBox.getItems().add("none");
    }

    public void addNewRangeToComboBox(String newRangeName) {
        viewRangeComboBox.getItems().addFirst(newRangeName);
    }
    public  void removeRangeFromComboBox(String rangeName) {
        viewRangeComboBox.getItems().remove(rangeName);
    }

    public  void disableControlsForReader(){
        AddNewRangeButton.disableProperty().setValue(true);
        RemoveExistingRangeButton.disableProperty().setValue(true);
    }
    public  void enableControlsForWriter(){
        AddNewRangeButton.disableProperty().setValue(false);
        RemoveExistingRangeButton.disableProperty().setValue(false);
    }
}

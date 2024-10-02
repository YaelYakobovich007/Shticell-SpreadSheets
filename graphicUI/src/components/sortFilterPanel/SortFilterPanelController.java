package components.sortFilterPanel;

import AnimationUtil.AnimationResolver;
import AnimationUtil.PopUpWindowAnimator;
import components.app.AppController;
import components.dynamicAnalysis.DynamicAnalysisController;
import components.dynamicAnalysis.DynamicSliderController;
import components.filter.FilterController;
import components.sort.SortController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.PopupManager;

import java.io.IOException;

public class SortFilterPanelController {

    @FXML private Button filterButton;
    @FXML private Button sortRowsButton;
    @FXML private Button dynamicAnalysisButton;

    @FXML private VBox mainContinerVbox;
    private AppController mainController;

    private DynamicSliderController dynamicSliderController;

    private DynamicAnalysisController dynamicAnalysisController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML private void initialize() {
        AnimationResolver.assignHoverEffect(filterButton);
        AnimationResolver.assignHoverEffect(sortRowsButton);
        AnimationResolver.assignHoverEffect(dynamicAnalysisButton);

        AppController.themeProperty().addListener((obs, oldTheme, newTheme) -> {
            updateStyleSheet(newTheme);
        });

    }

    private void updateStyleSheet(String newStyle) {
        String styleSheet = getClass().getResource("/util/leftMenu_" + newStyle + ".css").toExternalForm();
        mainContinerVbox.getStylesheets().clear();
        mainContinerVbox.getStylesheets().add(styleSheet);
    }

    @FXML void handleFilterAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/filter/filter.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Filtering");
            stage.setScene(new javafx.scene.Scene(root));

            FilterController filterController = loader.getController();
            filterController.setMainController(mainController);
            filterController.setStage(stage);

            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);

            stage.show();
        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading Filtering window.");
        }
    }

    @FXML
    void handleSortRowsAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/sort/sort.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Sort Rows");
            stage.setScene(new javafx.scene.Scene(root));
            SortController sortController = loader.getController();
            sortController.setMainController(mainController);
            sortController.setStage(stage);
            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);
            stage.show();

        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading sort rows window.");
        }
    }


    @FXML
    void handledynamicAnalysisAction(ActionEvent event) {
        try {
            // Load the slider
            FXMLLoader sliderLoader = new FXMLLoader(getClass().getResource("/components/dynamicAnalysis/slider.fxml"));
            Parent dynamicSlider = sliderLoader.load();
            dynamicSliderController = sliderLoader.getController();

            // Load the dynamic analysis window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/dynamicAnalysis/dynamicAnalysis.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Dynamic Analysis");
            stage.setScene(new javafx.scene.Scene(root));
            dynamicAnalysisController = loader.getController();

            // Set up the dynamic analysis controller with the slider controller and main controller
            dynamicAnalysisController.setMainController(mainController);
            dynamicAnalysisController.setStage(stage);
            dynamicAnalysisController.setSliderController(dynamicSliderController);
            dynamicAnalysisController.setDynamicSlider(dynamicSlider);

            // Pass the correct slider values
            dynamicSliderController.setMainAppController(mainController);

            PopUpWindowAnimator.setCloseRequestWithAnimation(stage);
            PopUpWindowAnimator.applyBounce(stage);
            stage.show();

        } catch (IOException e) {
            PopupManager.showErrorPopup("Error loading dynamic Analysis window.");
        }
    }





}

package components.settingsControlPanel;

import AnimationUtil.AnimatedToggleSwitch;
import AnimationUtil.AnimationResolver;
import AnimationUtil.PopUpWindowAnimator;
import AnimationUtil.ToggleSwitchManager;
import components.viewSheetMain.ViewSheetMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class SettingsControlPanelController {
    @FXML private ComboBox<String> styleComboBox;
    @FXML private HBox animationsOption;

    private final AnimatedToggleSwitch toggleSwitch = new AnimatedToggleSwitch();
    private ViewSheetMainController mainController;

    @FXML
    public void initialize() {
        animationsOption.getChildren().add(toggleSwitch);
        toggleSwitch.switchedOnProperty().addListener((observable, wasOn, isOn) -> toggleAnimations(isOn));
        styleComboBox.getItems().setAll("green", "red", "blue");
        styleComboBox.setValue("green");
    }

    private void toggleAnimations(boolean isOn) {
        if (isOn) {
            AnimationResolver.enableAnimations();
            ToggleSwitchManager.enableAnimations();
            PopUpWindowAnimator.setAnimationsEnabled(true);
        } else {
            AnimationResolver.disableAnimations();
            ToggleSwitchManager.disableAnimations();
            PopUpWindowAnimator.setAnimationsEnabled(false);
        }
    }

    @FXML
    void handleStyleSelectionAction(ActionEvent event) {
        ViewSheetMainController.themeProperty().setValue(styleComboBox.getValue());
    }

    public void setMainController(ViewSheetMainController mainController) {
        this.mainController = mainController;
    }
}

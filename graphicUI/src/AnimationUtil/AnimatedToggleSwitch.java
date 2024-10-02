package AnimationUtil;

import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class AnimatedToggleSwitch extends StackPane {
    private final TranslateTransition translateTransition;
    private final Circle toggleCircle;
    private final BooleanProperty switchedOn = new SimpleBooleanProperty(true);
    private boolean animationEnabled = true;

    public AnimatedToggleSwitch() {
        getStylesheets().add(getClass().getResource("ToggleSwitch.css").toExternalForm());
        this.getStyleClass().add("toggle-checkbox");

        Rectangle background = new Rectangle(50, 25);
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.getStyleClass().add("toggle-background");

        toggleCircle = new Circle(12.5);
        toggleCircle.getStyleClass().add("toggle-circle");

        getChildren().addAll(background, toggleCircle);

        translateTransition = new TranslateTransition(Duration.seconds(0.25), toggleCircle);
        toggleSwitch(switchedOn.get());

        switchedOn.addListener((obs, wasOn, isOn) -> toggleSwitch(isOn));

        this.setOnMouseClicked(event -> switchedOn.set(!switchedOn.get()));
    }

    private void toggleSwitch(boolean isOn) {
        if (isOn) {
            if (animationEnabled) {
                translateTransition.setToX(25);
            } else {
                toggleCircle.setTranslateX(25);
            }
            getStyleClass().add("toggle-switch-on");
        } else {
            if (animationEnabled) {
                translateTransition.setToX(-25);
            } else {
                toggleCircle.setTranslateX(-25);
            }
            getStyleClass().remove("toggle-switch-on");
        }

        if (animationEnabled) {
            translateTransition.play();
        }
    }

    public BooleanProperty switchedOnProperty() {
        return switchedOn;
    }

    public boolean isSwitchedOn() {
        return switchedOn.get();
    }

    public void removeAnimation() {
        animationEnabled = false;
    }

    public void enableAnimation() {
        animationEnabled = true;
    }
}

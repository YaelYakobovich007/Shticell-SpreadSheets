package AnimationUtil;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import javafx.util.Duration;
public class PopUpWindowAnimator {
    private static final BooleanProperty animationsEnabled = new SimpleBooleanProperty(true);

    public static void setAnimationsEnabled(boolean enabled) {
        animationsEnabled.set(enabled);
    }

    public static void applySlideOut(Stage stage) {
        if (!animationsEnabled.get()) {
            stage.close();
            return;
        }

        TranslateTransition slideOut = new TranslateTransition(Duration.seconds(0.5), stage.getScene().getRoot());
        slideOut.setFromX(0);
        slideOut.setToX(-stage.getWidth());
        slideOut.setOnFinished(event -> stage.close());
        slideOut.play();
    }

    public static void applyBounce(Stage stage) {
        if (!animationsEnabled.get()) {
            return;
        }
        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.3), stage.getScene().getRoot());
        scaleUp.setFromX(0.8);
        scaleUp.setFromY(0.8);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.2), stage.getScene().getRoot());
        scaleDown.setFromX(1.2);
        scaleDown.setFromY(1.2);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        scaleUp.setOnFinished(e -> scaleDown.play());
        scaleUp.play();
    }

    public static void setCloseRequestWithAnimation(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();
            applySlideOut(stage);
        });
    }
}

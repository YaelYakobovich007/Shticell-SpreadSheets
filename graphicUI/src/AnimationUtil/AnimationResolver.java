package AnimationUtil;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class AnimationResolver {
    private static boolean animationsEnabled = true;
    public static final List<Node> registeredNodesOnHoverEffect = new ArrayList<>();

    public static void assignHoverEffect(Node node) {
        registeredNodesOnHoverEffect.add(node);
        applyHoverEffect(node);
    }

    public static void removeHoverEffect(Node node) {
        node.setOnMouseEntered(null);
        node.setOnMouseExited(null);
    }

    public static void applyHoverEffect(Node node){
        if (animationsEnabled) {
            node.setOnMouseEntered(event -> grow(node));
            node.setOnMouseExited(event -> shrink(node));
        }
    }

    public static void enableAnimations() {
        animationsEnabled = true;
        applyToAllRegisteredNodes();
    }

    public static void disableAnimations() {
        animationsEnabled = false;
        removeFromAllRegisteredNodes();
    }

    private static void applyToAllRegisteredNodes() {
        for (Node node : registeredNodesOnHoverEffect) {
            applyHoverEffect(node);
        }
    }

    private static void removeFromAllRegisteredNodes() {
        for (Node node : registeredNodesOnHoverEffect) {
            removeHoverEffect(node);
        }
    }


    private static void grow(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.play();
    }

    private static void shrink(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }
}

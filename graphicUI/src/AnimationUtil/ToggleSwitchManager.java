package AnimationUtil;

import java.util.ArrayList;
import java.util.List;

public class ToggleSwitchManager {
    private static boolean animationsEnabled = true;
    private static final List<AnimatedToggleSwitch> toggleSwitches= new ArrayList<>();

    public static void registerToggleSwitch(AnimatedToggleSwitch toggleSwitch) {
        toggleSwitches.add(toggleSwitch);
        applyAnimationState(toggleSwitch);
    }

    public static void enableAnimations() {
        animationsEnabled = true;
        for (AnimatedToggleSwitch toggleSwitch : toggleSwitches) {
            toggleSwitch.enableAnimation();
        }
    }

    public static void disableAnimations() {
        animationsEnabled = false;
        for (AnimatedToggleSwitch toggleSwitch : toggleSwitches) {
            toggleSwitch.removeAnimation();
        }
    }

    private static void applyAnimationState(AnimatedToggleSwitch toggleSwitch) {
        if (animationsEnabled) {
            toggleSwitch.enableAnimation();
        } else {
            toggleSwitch.removeAnimation();
        }
    }
}

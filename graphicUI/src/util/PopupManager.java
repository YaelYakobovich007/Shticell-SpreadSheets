package util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PopupManager {
    public static void showSuccessPopup(String message) {
        showMessagePopup(message, "Success", "label-success");
    }

    public static void showErrorPopup(String message) {
        showMessagePopup(message, "Error", "label-error");
    }

    private static void showMessagePopup(String message, String title, String labelStyleClass) {
        Stage popupStage = new Stage();
        popupStage.setTitle(title);

        Label label = new Label(message);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.getStyleClass().add(labelStyleClass);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        closeButton.getStyleClass().add("button-primary");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 200);
        scene.getStylesheets().add(PopupManager.class.getResource("popUpWindowDesign/popupWindow_green.css").toExternalForm());
        popupStage.setScene(scene);
        popupStage.show();
    }

}

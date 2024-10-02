package mainStart;

import api.Engine;
import components.app.AppController;
import impl.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/app/app.fxml"));
        Parent load = loader.load();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        double windowWidth = screenWidth * 0.75;
        double windowHeight = screenHeight * 0.75;

        Scene scene = new Scene(load, 1200, 500);
        primaryStage.setScene(scene);
        Image logo = new Image("/resources/icons/logo.1.png");
        primaryStage.getIcons().add(logo);

        AppController mainController = loader.getController();
        Engine engine = new EngineImpl();
        mainController.setEngine(engine);
        mainController.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Shticell");

        primaryStage.show();
        primaryStage.setX((screenWidth - windowWidth) / 2);
        primaryStage.setY((screenHeight - windowHeight) / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
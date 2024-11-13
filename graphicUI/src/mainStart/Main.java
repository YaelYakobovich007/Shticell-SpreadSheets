package mainStart;

import components.appMain.AppMainController;
import components.login.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import util.http.HttpClientUtil;

public class Main extends Application {
    private AppMainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/components/appMain/appMain.fxml"));
        Parent mainRoot = mainLoader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        double windowWidth = screenWidth * 0.75;
        double windowHeight = screenHeight * 0.75;

        primaryStage.setX((screenWidth - windowWidth) / 2);
        primaryStage.setY((screenHeight - windowHeight) / 2);

        Scene mainScene = new Scene(mainRoot, 1200, 500);
        primaryStage.setScene(mainScene);
        Image logo = new Image("/resources/icons/logo.1.png");
        primaryStage.getIcons().add(logo);

        mainController = mainLoader.getController();
        mainController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Shticell");

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/components/login/login.fxml"));
        Parent loginRoot = loginLoader.load();
        Stage loginStage = new Stage();
        Scene loginScene = new Scene(loginRoot);
        loginStage.setScene(loginScene);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.initOwner(primaryStage);
        loginStage.setTitle("Login");
        LoginController loginController = loginLoader.getController();
        loginController.setAppMainController(mainController);
        loginController.setLoginStage(loginStage);
        loginController.setPrimaryStage(primaryStage);
        loginStage.setOnCloseRequest(event -> Platform.exit());
        loginStage.showAndWait();

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
        mainController.close();
    }

}
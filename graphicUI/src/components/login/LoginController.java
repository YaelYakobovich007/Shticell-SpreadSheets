package components.login;

import components.appMain.AppMainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;

public class LoginController {
    @FXML public TextField userNameTextField;
    @FXML public Label errorMessageLabel;
    private AppMainController appMainController;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private Stage loginStage;
    private  Stage primaryStage;

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    System.out.println(" the problem is"+ responseBody);
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        appMainController.updateUserName(userName);
                        appMainController.startListRefresher();
                        loginStage.close();
                        primaryStage.show();
                    });
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        loginStage.close();
        Platform.exit();
    }

    public void setAppMainController(AppMainController mainController) {
        this.appMainController = mainController;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void  setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}

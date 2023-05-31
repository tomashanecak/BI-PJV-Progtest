package thedrake.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    public ImageView logoImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Button playButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button exitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load and set the image
        Image logoImage = new Image(getClass().getResourceAsStream("assets/logo.png"));
        logoImageView.setImage(logoImage);
    }

    @FXML
    private void handleMultiplayerLocalButton() {
        // Handle play button action here
    }

    @FXML
    private void handleMultiplayerOnlineButton() {
        // Handle settings button action here
    }

    @FXML
    private void handleSinglePlayerButton() {
        // Handle settings button action here
    }

    @FXML
    private void handleExitButton() {
        // Handle exit button action here
        Platform.exit();
        System.exit(0);
    }
}

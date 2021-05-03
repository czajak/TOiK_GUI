package pwsztar.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Klasa MainController kontroluje główny widok który wyświetla się po uruchomieniu aplikacji

public class MainController implements Initializable {
    @FXML
    private AnchorPane rootPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    // Metoda onButtonPressedSql obsługuje działanie przycisku JDBC, który przełącza na widok Sql

    @FXML
    private void onButtonPressedSql(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../jdbc_credentials/credentials.fxml"));
        rootPane.getChildren().setAll(pane);
    }

    // Metoda onButtonPressedMail obsługuje działanie przycisku JavaMail, który przełącza na widok Mail.

    @FXML
    private void onButtonPressedMail(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../mail/mail.fxml"));
        rootPane.getChildren().setAll(pane);
    }

}

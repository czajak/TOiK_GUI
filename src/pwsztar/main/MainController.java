package pwsztar.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private AnchorPane rootPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    private void onButtonPressedSql(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../sql/sql.fxml"));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void onButtonPressedMail(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../mail/mail.fxml"));
        rootPane.getChildren().setAll(pane);
    }

}

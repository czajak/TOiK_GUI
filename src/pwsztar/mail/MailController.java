package pwsztar.mail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MailController implements Initializable {
    @FXML
    private AnchorPane mailPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}
    @FXML
    private void onBackButtonPressed(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../main/main.fxml"));
        mailPane.getChildren().setAll(pane);
    }


}

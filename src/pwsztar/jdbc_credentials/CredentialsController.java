package pwsztar.jdbc_credentials;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pwsztar.sql.SqlController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;

public class CredentialsController implements Initializable {
    @FXML private AnchorPane credentialsPane;

    private ArrayList<String> credentialsList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    // Metoda która pozwala na działanie przycisku wstecz (<). Wraca do widoku głównego.
    @FXML
    private void onBackButtonPressed(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../main/main.fxml"));
        credentialsPane.getChildren().setAll(pane);
    }

    @FXML private void onAddDataButtonPressed(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*.txt"));
        File f = fileChooser.showOpenDialog(null);
        if(f != null){
            Scanner fileReader = new Scanner(f);
            while(fileReader.hasNextLine()){
                credentialsList.add(fileReader.nextLine());
            }
            fileReader.close();
            openSqlWindow();
        }
    }

    @FXML private void onDefaultButtonPressed(ActionEvent event) {
        credentialsList.addAll(Arrays.asList(
                "localhost",
                "5432",
                "postgres",
                "postgres",
                "postgres"));
        openSqlWindow();
    }

    private void openSqlWindow(){
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/pwsztar/sql/sql.fxml")
            );
            Parent root = (Parent) loader.load();
            SqlController sqlController = loader.getController();
            sqlController.setData(credentialsList);

            credentialsPane.getChildren().setAll(root);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void insufficientDataInFile(){
        System.out.println("insufficient data in file");
    }

}

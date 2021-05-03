package pwsztar.sql;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.postgresql.util.PSQLException;
import pwsztar.jdbc_credentials.CredentialsController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

// Klasa SqlController obsługuje działanie widoku sql

public class SqlController implements Initializable {

    //private final CredentialsController credentialsController;
    private Stage thisStage;
    private ArrayList<String> credentialsList = new ArrayList<>();
    // Wszystkie niezbędne elementy layoutu

    @FXML private AnchorPane sqlPane;
    @FXML private TextField textField;
    @FXML private TableView<ObservableList> tableView;
    @FXML private Text errorMessage;
    @FXML private Text attachmentOnSuccessPrompt;
    @FXML private ListView<String> tablesListView;
    @FXML private TableColumn col;

    // Zmienne do połączenia się z bazą

    private ObservableList<ObservableList> data;
    private String driver = "org.postgresql.Driver";
    private String host = "localhost";
    private String port = "5432";
    private String dbname = "postgres";
    private String user = "postgres";
    private String url = "jdbc:postgresql://" + host+":"+port + "/" + dbname;
    private String password = "postgres";
    private static Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Metoda która pozwala na działanie przycisku wstecz (<). Wraca do widoku głównego.

    @FXML
    private void onBackButtonPressed(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../jdbc_credentials/credentials.fxml"));
        sqlPane.getChildren().setAll(pane);
    }


    // Metoda która obsługuje przycisk Wyślij, czyści ekran, łączy się z bazą i wyświetla wyniki zapytania

    @FXML private void sendRequest(ActionEvent event) throws FileNotFoundException, SQLException, ClassNotFoundException {
        cls();
        connect();
        buildData(textField.getText());
    }

    // Metoda czyszcząca ekran

    public void cls(){
        errorMessage.setVisible(false);
        tableView.getColumns().clear();
    }

    // Metoda przypisuje obiekt klasy Connection do zmiennej w klasie

    public void connect() throws SQLException, ClassNotFoundException {
        connection = makeConnection();
    }

    // Metoda która pozwala na połączenie się z bazą, lub rzuca wyjątkiem gdy połączenie się nie uda.

    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        try{
            Class.forName(driver);
            connection = DriverManager.getConnection(url,user,password);
            System.out.println("connected");
            initList();
            return connection;
        }catch (ClassNotFoundException cnfe){
            System.err.println("blad ladowania sterownika: " + cnfe);
        }catch (SQLException sqle){
            System.err.println("blad przy nawiazywaniu polaczenia: " + sqle);

        }
        return null;
    }

    // Metoda która wykorzystuje TableView aby wyświetlić odpowiedź na zapytanie, gdy jest ono poprawne lub wyświetla
    // błąd gdy jest ono błędne.

    public void buildData(String query) {
        tablesListView.setVisible(false);
        tableView.setVisible(true);

        data = FXCollections.observableArrayList();
        try {
            ResultSet rs = connection.createStatement().executeQuery(query);

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                System.out.println(rs.getMetaData().getColumnName(i + 1));
                col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty((param.getValue().get(j) == null) ? "null" : param.getValue().get(j).toString()));

                tableView.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            tableView.setVisible(true);
            tableView.setItems(data);
        } catch(Exception e){
            tableView.setVisible(false);
            errorMessage.setVisible(true);
            errorMessage.setText(e.getMessage());
        }

    }

    public void setData(ArrayList<String> data){
        if(data.size() == 5){
            host = data.get(0);
            port = data.get(1);
            dbname = data.get(2);
            user = data.get(3);
            password = data.get(4);
            url = "jdbc:postgresql://" + host+":"+port + "/" + dbname;
            System.out.println(host + " " + port + " " + dbname + " " + user + " " + password);
            try {
                connect();
                makeConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("złe dane!");
        }
    }


    public void initList() throws SQLException {
        tablesListView.setVisible(true);
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM pg_catalog.pg_tables");
        ObservableList<String> observableList = FXCollections.observableArrayList();
        while (rs.next()){
            observableList.add(rs.getString(1) + "." + rs.getString(2));
        }
        tablesListView.getItems().addAll(observableList);

        tablesListView.setOnMouseClicked(click -> {

            if (click.getClickCount() == 2) {
                buildData("SELECT * FROM " + tablesListView.getSelectionModel().getSelectedItem());
            }
        });

    }



}

package pwsztar.sql;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.postgresql.util.PSQLException;

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

    // Wszystkie niezbędne elementy layoutu

    @FXML private AnchorPane sqlPane;
    @FXML private TextField textField;
    @FXML private TableView<ObservableList> tableView;
    @FXML private Text errorMessage;
    @FXML private Text attachmentOnSuccessPrompt;

    // Zmienne do połączenia się z bazą

    private ObservableList<ObservableList> data;
    private String driver = "org.postgresql.Driver";
    private String host = "195.150.230.210";
    private String port = "5434";
    private String dbname = "2021_czajka_dariusz";
    private String user = "2021_czajka_dariusz";
    private String url = "jdbc:postgresql://" + host+":"+port + "/" + dbname;
    private String password = "XXXXXXXXXXXXXX";
    private static Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    // Metoda która pozwala na działanie przycisku wstecz (<). Wraca do widoku głównego.

    @FXML
    private void onBackButtonPressed(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../main/main.fxml"));
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
            Connection connection = DriverManager.getConnection(url,user,password);
            System.out.println("connected");
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

        data = FXCollections.observableArrayList();
        try {
            ResultSet rs = connection.createStatement().executeQuery(query);

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));

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

}

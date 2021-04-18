package pwsztar.mail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

// Klasa MailController - kontroluje zachowanie widoku JavaMail.

public class MailController implements Initializable {

    // Wszystkie niezbędne elementy layoutu

    @FXML private Text attachmentOnSuccessPrompt;
    @FXML private AnchorPane mailPane;
    @FXML private TextField emailTextField;
    @FXML private TextArea messageTextArea;
    @FXML private Text successText;
    @FXML private Text errorText;

    private Session session;

    private final String FROM = "toik.javamail@gmail.com";
    private final String PASSWORD = "XXXXXXXXXXXXXX";

    private List<File> attachmentsList;

    // Metoda initizalize która wykonuje się od razu po przełączeniu na widok JavaMail.
    // Wykorzystana w celu optymalizacji działania programu - od razu inicjalizuje sesję GMaila aby użytkownik mógł
    // szybciej wysyłać maile.

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeEmailAccount();
    }

    // Metoda która pozwala na działanie przycisku wstecz (<). Wraca do widoku głównego.
    @FXML
    private void onBackButtonPressed(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("../main/main.fxml"));
        mailPane.getChildren().setAll(pane);
    }

    // Metoda która obsługuje działanie przycisku Wyślij, uruchamia metodę sendEmail.

    @FXML private void onSendButtonPressed(ActionEvent event) throws IOException, InterruptedException {
        sendEmail(emailTextField.getText(), messageTextArea.getText());
    }

    // Metoda która obsługuje działanie przycisku "Załączniki..", wywołuje okno wyboru plików i jeżeli użytkownik
    // wybrał jakieś pliki zapisuje je w liście "attachmentsList"

    @FXML private void onAddAttachmentsButtonPressed(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files","*.pdf"));
        attachmentsList = fileChooser.showOpenMultipleDialog(null);
        if(attachmentsList != null && !attachmentsList.isEmpty()){
            attachmentOnSuccessPrompt.setVisible(true);
        }
    }

    // Metoda inicjalizacji GMaila, uruchamiana od razu po przełączeniu na widok JavaMail.

    private void initializeEmailAccount(){
        Properties properties = new Properties();
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");

        session = Session.getInstance(
                properties,
                new javax.mail.Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return new PasswordAuthentication(FROM,PASSWORD);
                    }
                });
    }

    // Metoda sendEmail - główna metoda klasy. Wysyła wiadomości e-mail wraz z załącznikami - jeżeli list attachmentsList
    // nie jest pusta.

    private void sendEmail(String recipient, String message){
        if(session==null){
            initializeEmailAccount();
        }
        try{
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM));
            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(recipient));
            msg.setSubject("Dariusz Czajka");
            msg.setText(message);

            if(attachmentsList != null && !attachmentsList.isEmpty()){
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(message);

                MimeBodyPart attachmentPart = new MimeBodyPart();
                for(File file: attachmentsList){
                    attachmentPart.attachFile(file.getAbsoluteFile());
                }

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(attachmentPart);
                msg.setContent(multipart);
            }
            Transport.send(msg);
            sendSuccessful();
        }catch(Exception e){
            sendUnsuccessful(e.getMessage());
        }
    }

    // Metoda sendSuccessful, czyści ekran oraz wyświetla prompt o pomyślnym wysłaniu wiadomości.

    private void sendSuccessful(){
        cls();
        successText.setVisible(true);
    }

    // Metoda sendUnsuccessful, wyświetla wiadomość na temat błędu wysyłania

    private void sendUnsuccessful(String error){
        successText.setVisible(false);
        errorText.setVisible(true);
        errorText.setText("Błąd: " + error);
    }

    // Metoda cls, czyści ekran z powiadomień oraz danych wpisanych przez użytkownika.

    private void cls(){
        emailTextField.setText("");
        messageTextArea.setText("");
        attachmentOnSuccessPrompt.setVisible(false);
        errorText.setVisible(false);
        successText.setVisible(false);
    }
}


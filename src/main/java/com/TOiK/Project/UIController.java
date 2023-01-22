package com.TOiK.Project;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class UIController {
    @Autowired
    private EmailSenderService senderService;

    private final HostServices hostServices;

    @FXML
    public TextField textFirstname;
    @FXML
    public TextField textLastname;
    @FXML
    public TextField textEmail;
    @FXML
    public Button cancelButton;
    @FXML
    private ChoiceBox timePicker;
    @FXML
    private ChoiceBox stolPicker;

    public UIController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize(){

    }

    @FXML
    public void Accept(ActionEvent event) {
        String message = ("Zarezerwowano " + stolPicker.getValue().toString() + " na godzinę: " +
                timePicker.getValue().toString()+ " przez: " + textFirstname.getText() + " " +
                textLastname.getText()    + " w dniu - do dorobienia"
                );

        senderService.sendSimpleEmail(textEmail.getText().toString(),
                "Rezerwacja",
                message);
    }



}

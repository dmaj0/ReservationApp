package com.TOiK.Project;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import java.security.cert.TrustAnchor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


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
    @FXML
    private ChoiceBox dayPicker;
    @FXML
    private ChoiceBox monthPicker;
    @FXML
    private ChoiceBox yearPicker;
    @FXML
    private TableView<ModelRes> tableView;
    @FXML
    private TableColumn<ModelRes, String> imieColumn;
    @FXML
    private TableColumn<ModelRes, String> nazwiskoColumn;
    @FXML
    private TableColumn<ModelRes, String> emailColumn;
    @FXML
    private TableColumn<ModelRes, String> stolColumn;
    @FXML
    private TableColumn<ModelRes, String> timepickerColumn;
    @FXML
    private TableColumn<ModelRes, String> daypickerColumn;
    @FXML
    private TableColumn<ModelRes, String> monthpickerColumn;
    @FXML
    private TableColumn<ModelRes, String> yearpickerColumn;
    @FXML
    private Label displayLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private ChoiceBox displayDayPicker;
    @FXML
    private Label addingLabel;


    public UIController(HostServices hostServices) {
        this.hostServices = hostServices;
    }
    @Autowired
    private JdbcTemplate jdbcTemplate;


    private LocalDate currentDate = LocalDate.now();

    private int selectedDay = currentDate.getDayOfMonth();
    private int selectedMonth = currentDate.getMonthValue();

    @FXML
    public void initialize(){
        System.out.println(selectedMonth);
        selectedMonth -= 1;
        System.out.println(selectedMonth);
        loadData();
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab.getText().equals("Your Tab Name")) {
                loadData();
            }
        });

    }

    @FXML
    private void loadData() {
        String month=  String.valueOf(selectedMonth+1);
        String displayData = "Rezerwacje dla dnia " + selectedDay + "-" + month;
        displayLabel.setText(displayData);
        imieColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nazwiskoColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        stolColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
        timepickerColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        daypickerColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        monthpickerColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        yearpickerColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        // Use the query method to retrieve the data from the database

        month = (String) monthPicker.getItems().get(selectedMonth);
        System.out.println(month);
        String sql = "SELECT * FROM roznosci.rezerwacja WHERE daypicker = '" + selectedDay + "' and monthpicker ='" + month + "'";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        // Create an ObservableList to store the data
        ObservableList<ModelRes> data = FXCollections.observableArrayList();

        // Iterate over the rows and use the data to create instances of your data class
        for (Map<String, Object> row : rows) {
            ModelRes myData = new ModelRes();
            myData.setName((String) row.get("imie"));
            myData.setSurname((String) row.get("nazwisko"));
            myData.setEmail((String) row.get("email"));
            myData.setTable((String) row.get("stol"));
            myData.setTime((String) row.get("timepicker"));
            myData.setDay((String) row.get("daypicker"));
            myData.setMonth((String) row.get("monthpicker"));
            myData.setYear((String) row.get("yearpicker"));
            data.add(myData);
        }
        // Set the items of the TableView to the ObservableList
        tableView.setItems(data);
    }



    @FXML
    public void Accept(ActionEvent event) {
        String message = ("Zarezerwowano " + stolPicker.getValue().toString() + " na godzinę: " +
                timePicker.getValue().toString()+ " przez: " + textFirstname.getText() + " " +
                textLastname.getText()    + " w dniu: " + dayPicker.getValue().toString() + "-" +
                monthPicker.getValue().toString() + "-" + yearPicker.getValue().toString()
                );

        String sql = "insert into roznosci.rezerwacja (imie,nazwisko,email,stol,timepicker,daypicker,monthpicker,yearpicker)"+
                "values ('"+ textFirstname.getText() +"','"+
                textLastname.getText()+"','"+
                textEmail.getText()+"','" +
                stolPicker.getValue().toString() +"','"+
                timePicker.getValue().toString()+"','"+
                dayPicker.getValue().toString() +"','"+
                monthPicker.getValue().toString() +"','"+
                yearPicker.getValue().toString() +"')";
        try{
            jdbcTemplate.update(sql);
            addingLabel.setVisible(true);
            System.out.println("Query was successful");
            addingLabel.setVisible(true);
            addingLabel.setText("Pomyślnie dodano rezerwacje");
            addingLabel.setTextFill(Color.GREEN);
            senderService.sendSimpleEmail(textEmail.getText(),"Rezerwacja",message);
        }catch (DataAccessException e) {
            System.out.println("Query was not successful: " + e.getMessage());
            addingLabel.setVisible(true);
            addingLabel.setText("Nie udało się zarezerwować");
            addingLabel.setTextFill(Color.RED);
        }


        loadData();


    }


    public void selectPreviousDay(ActionEvent event) {
        selectedDay -= 1;
        if(selectedDay < 1){
            selectedDay = 31;
        }
        loadData();
    }

    public void selectNextDay(ActionEvent event) {
        selectedDay += 1;
        if(selectedDay > 31){
            selectedDay = 1;
        }
        loadData();
    }

    public void selectPickedDay(ActionEvent event) {
        selectedDay = Integer.parseInt(displayDayPicker.getValue().toString());
        loadData();

    }

    public void selectPreviousMonth(ActionEvent event) {
        selectedMonth -=1;
        if(selectedMonth < 0){
            selectedMonth = 11;
        }
        loadData();
    }

    public void selectNextMonth(ActionEvent event) {
        selectedMonth += 1;
        if(selectedMonth > 11){
            selectedMonth = 0;
        }
        loadData();
    }
}

package com.example.brief3_mutuellecentralisee;

import com.example.brief3_mutuellecentralisee.dao.implimentation.ClientsImp;
import com.example.brief3_mutuellecentralisee.helpers.alertHelper;
import com.example.brief3_mutuellecentralisee.helpers.jsonHelper;
import com.example.brief3_mutuellecentralisee.helpers.validationHelper;
import com.example.brief3_mutuellecentralisee.models.ChartData;
import com.example.brief3_mutuellecentralisee.models.Client;
import com.example.brief3_mutuellecentralisee.models.CountryCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;




public class homeController implements Initializable {

    @FXML private TableColumn col_nameCompany;
    @FXML private TableColumn col_dateStartWork;
    @FXML private TableColumn col_firstName;
    @FXML private TableColumn col_lastName;
    @FXML private TableColumn col_cinpass;
    @FXML private TableColumn col_phone;
    @FXML private TableColumn col_email;
    @FXML private TableColumn col_adress;
    @FXML private TableColumn col_dateRegister;
    @FXML private TableColumn col_numBadgework;

    @FXML private TableView dataGrid;

    @FXML private TextField nameCompany;
    @FXML private DatePicker dateStartWork;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField cinpass;
    @FXML private RadioButton cin;
    @FXML private RadioButton pass;

    @FXML private ComboBox phoneCode;
    @FXML private TextField phone;
    @FXML private TextField email;
    @FXML private TextArea adress;
    @FXML private ToggleGroup identity;
    @FXML private TextField NumbadgeWork;
    @FXML private DatePicker dateRegister;

    @FXML private ComboBox company;
    @FXML private TextField filter;

    @FXML private LineChart chart;
    @FXML private CategoryAxis DateAxis;
    @FXML private NumberAxis CountAxis;

    private ObservableList<Client> clients;
    private List<CountryCode> countryCodes;

    private ClientsImp CIMP=new ClientsImp();
    //logger:
    static  final Logger log = Logger.getLogger(homeController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("You are now in home page ");
        try {
            cin.setSelected(true);

            countryCodes=jsonHelper.GetPhoneCountryCodes();
           for(CountryCode cc : countryCodes) {
                phoneCode.getItems().add(cc.getDial_code());
            }
        } catch (IOException e) {
            log.error("Error : "+ e.getMessage());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            log.error("Error : "+ e.getMessage());
            e.printStackTrace();
        }
        try {
        col_nameCompany.setCellValueFactory(new PropertyValueFactory<Client, String>("nameCompany"));
        col_dateStartWork.setCellValueFactory(new PropertyValueFactory<Client, String>("dateStartWork"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<Client, String>("firstName"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<Client, String>("lastName"));
        col_cinpass.setCellValueFactory(new PropertyValueFactory<Client, String>("cinpass"));
        col_phone.setCellValueFactory(new PropertyValueFactory<Client, String>("phone"));
        col_email.setCellValueFactory(new PropertyValueFactory<Client, String>("email"));
        col_adress.setCellValueFactory(new PropertyValueFactory<Client, String>("adress"));
        col_dateRegister.setCellValueFactory(new PropertyValueFactory<Client, String>("dateRegister"));
        col_numBadgework.setCellValueFactory(new PropertyValueFactory<Client, String>("numBadgework"));


        clients= FXCollections.<Client>observableArrayList(); // initialisation d'une liste observable de type clients


        dataGrid.setItems(clients); // definie les elements a afficher (liste clients) dans la tablevie

        clients.addAll(CIMP.getAllClients());
        log.debug("chargement des companies");
        log.debug("chargement des companies");
        loadCompanies();
        loadStatistiques();


        } catch (Exception e) {
            log.error("Error : "+ e.getMessage());
            e.printStackTrace();
        }

    }

    private void loadCompanies() {

        company.getItems().clear();
        //Companies List
        List<String> companies=CIMP.getAllCompanies();
        company.getItems().add("");
        for(String nc : companies) {
            company.getItems().add(nc);
        }
    }

    private void loadStatistiques(){

        XYChart.Series series = new XYChart.Series();
        series.setName("Statistiques - Clients par date");
        List<ChartData> listChartData=CIMP.getClientsByDateRegister();

        // Tri par date :
        for(int i =0;i<listChartData.size();i++){

            for(int j =i+1;j<listChartData.size();j++){
                if(listChartData.get(i).getDate().compareTo(listChartData.get(j).getDate())>0){
                    ChartData x=listChartData.get(i);
                    listChartData.set(i,listChartData.get(j));
                    listChartData.set(j,x);
                }
            }
        }
        //fin Tri

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int maxValue=1;
        for(ChartData cd:listChartData) {
            XYChart.Data data=new XYChart.Data(formatter.format(cd.getDate()),cd.getCount());
            if(cd.getCount()>maxValue){
                maxValue=cd.getCount();
            }
            series.getData().add(data);
        }


        CountAxis.setAutoRanging(false); // la valeur min et la valeur max par rapport au donnée
        CountAxis.setLowerBound(0); // limite inferieur charte
        CountAxis.setUpperBound(maxValue+1); // limite superieur charte
        CountAxis.setTickUnit(1); // l'unite de gradulation pour l'axe(difference d'une ligne et d'une autre)

        chart.getData().clear();
        chart.getData().add(series);

        chart.setPrefWidth(50* listChartData.size());
    }


    @FXML
    private void addClientClick(ActionEvent event) {

        RadioButton selectedRadioButton = (RadioButton) identity.getSelectedToggle();
        String toogleGroupValue = selectedRadioButton.getText();


        if(Validation()) {

            // L'ajout du client
            Client c = new Client(nameCompany.getText(), dateStartWork.getEditor().getText(), firstName.getText(), lastName.getText(), cinpass.getText(), phoneCode.getSelectionModel().getSelectedItem() +"-"+ phone.getText(), email.getText(), adress.getText(),dateRegister.getEditor().getText(),NumbadgeWork.getText());

            // tester cinpass si il existe deja ou non

            if(CIMP.getClientsByCinPass(cinpass.getText())==null){ //n'existe pas dans BD
            if(CIMP.insertClient(c)) {
                clients.add(c);
                sendMail();


                // Message d'ajout :
                alertHelper.ShowSuccess("OK", "Client ajouter", "Client ajouter avec success");
                // Reset les valeurs des controls apres l'ajout
                nameCompany.setText("");
                dateStartWork.setValue(null);
                firstName.setText("");
                lastName.setText("");
                cinpass.setText("");
                phoneCode.getEditor().setText("");
                phone.setText("");
                email.setText("");
                adress.setText("");
                dateRegister.setValue(null);
                NumbadgeWork.setText("");


                loadStatistiques();
            }
            else{
                alertHelper.ShowError("Error","erreur lors de l'ajout au DB.","");
                log.error("Erreur lors de l'ajout au DB");
            }

            }else{
                alertHelper.ShowError("Error","Client avec meme CIN ou PASSPORT exist déja..","");
                log.error("Error, Client avec le meme CIN ou PASSPORT ");

            }
        }

    }
    // --------- Validation ---------------------------------------------------------------------------
    private boolean Validation() {
        boolean valide=true;
        List<String> messages=new ArrayList<>();

        // dateRegister:
        if(dateRegister.getEditor().getText().equals("")){
            messages.add("Champ vide ('Date Register')");
            log.error("Erreur"+messages);
            valide=  false;
        }

        //dateStartWork:
        if(dateStartWork.getEditor().getText().equals("")){
            messages.add("Champ vide ('Date Start Work')");
            log.error("Erreur"+messages);
            valide=  false;
        }

        // firstname:
        if(!validationHelper.IsNotEmpty(firstName.getText())){
            messages.add("Champ vide ('First Name')");
            log.error("Champ vide "+messages);
            valide=  false;
        }
        if(!validationHelper.IsValidLength(firstName.getText(),50)){
            messages.add("Longeur incorrect ('First Name')");
            log.error("validation des champs"+messages);
            valide=  false;
        }


        // lastname:
        if(!validationHelper.IsNotEmpty(lastName.getText())){
            messages.add("Champ vide ('Last Name')");
            log.error("Erreur"+messages);
            valide=  false;
        }
        if(!validationHelper.IsValidLength(lastName.getText(),50)){
            messages.add("Longeur incorrect ('Last Name')");
            log.error("validation des champs"+messages);
            valide=  false;
        }

        //cin passport:
        if(cin.isSelected()){
            if(!validationHelper.IsValidCIN(cinpass.getText())){
                messages.add("Format incorrect ('CIN') - ex : AA000000.");
                log.error("validation des champs"+messages);
                valide=  false;
            }
        }else{
            if(!validationHelper.IsValidPASSPORT(cinpass.getText())){
                messages.add("Format incorrect ('PASSPORT') - ex : AA0000000.");
                log.error("validation des champs"+messages);
                valide=  false;
            }
        }


        //phone:
        if(!validationHelper.IsValidPhone(phone.getText())){
            messages.add("Format incorrect ('Phone') - ex : +000-60000000.");
            log.error("validation des champs"+messages);
            valide=  false;
        }

        //email :
        if(!validationHelper.IsValidEmail(email.getText())){
            messages.add("Format incorrect ('Email') - ex : email@domain.com");
            log.error("validation des champs"+messages);
            valide=  false;
        }

        // adresse:
        if(!validationHelper.IsNotEmpty(adress.getText())){
            messages.add("Champ vide ('Adresse')");
            log.error("Erreur"+messages);
            valide=  false;
        }

        //N badge:
        if(!validationHelper.IsNotEmptyNumber(NumbadgeWork.getText())){
            messages.add("Champ vide ('NumBadgeWork')");
            log.error("Erreur"+messages);
            valide=  false;
        }

        if(!validationHelper.IsValidLength(NumbadgeWork.getText(),10)){
            messages.add("Longeur incorrect ('N Badge')");
            log.error("validation des champs"+messages);
            valide=  false;
        }


        //Name Company:

        if(!validationHelper.IsNotEmpty(nameCompany.getText())){
            messages.add("Champ vide ('Name Company')");
            log.error("Erreur"+messages);
            valide=  false;
        }

        if(!validationHelper.IsValidLength(nameCompany.getText(),50)){
           messages.add("Longeur incorrect ('Name Company')");
            log.error("validation des champs"+messages);
            valide=  false;
        }

    if(!valide){
         alertHelper.ShowError("Erreur de validation","",String.join("\n",messages));
        log.error("Erreur au niveau du validation des champs "+messages);

    }
    return valide;
    }

    //--------------------------------------------------------------------------------------------------


    // -------------------------     FILTRAGE  ----------------------------------------------------------

    private ObservableList<Client> getByCompany(String company,String searchKeyword){
        ObservableList<Client> clients = FXCollections.observableArrayList(CIMP.getByCompany(company,searchKeyword));
        return clients;
    }

    @FXML
    private void companyHandler(ActionEvent event){

        Filter();
    }

    @FXML
    private void filterHandler(KeyEvent event) {

        Filter();
    }

    void Filter(){
   if(company.getValue()!=null){
       // filtre by company & search :
        if(!company.getValue().toString().equals("")) {
            dataGrid.getItems().clear();
            dataGrid.setItems(getByCompany(company.getValue().toString(),filter.getText()));
        }
        // all & search
        else{
            dataGrid.getItems().clear();
            clients.clear();
            clients.addAll(CIMP.getAllClients(filter.getText()));
            dataGrid.setItems(clients);
        }

   } else{

            dataGrid.getItems().clear();
            clients.clear();
            clients.addAll(CIMP.getAllClients(filter.getText()));
            dataGrid.setItems(clients);
        }
    }


//------------------------------------------------------------------------------------------------------

  // envoie mail SMTP:

    public void sendMail() {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        String username = "brief5mutuellecentralisee@gmail.com";
        String password = "zjszhomjrvskouny";

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("brief5mutuellecentralisee@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email.getText())
            );
            message.setSubject("Register Done");
            String htmlCode="<h2> Hey Dear , <h2/>\n <h3> Your register is done with success !</h3> \n  <h3> Thanks And have a good Day ! </h3>";
            message.setContent(htmlCode,"text/html");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            log.fatal("Error : "+ e.getMessage());
            e.printStackTrace();
        }
    }

}






package com.example.brief3_mutuellecentralisee;

import com.example.brief3_mutuellecentralisee.dao.implimentation.UsersImp;
import com.example.brief3_mutuellecentralisee.helpers.alertHelper;
import com.example.brief3_mutuellecentralisee.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    private Stage parentStage;
    @FXML private TextField username;
    @FXML private TextField password;
    public static  User user_Ath ;


    private UsersImp UIMP=new UsersImp();

    //logger:
    static  final Logger log = Logger.getLogger(loginController.class.getName());

    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        log.info("Hello , this is a login start ! ");



    }

    @FXML
    private void loginClick(ActionEvent event)
    {
        try {
            String user=username.getText();
            String pass=password.getText();

            boolean connected=false;

            User u =UIMP.Connect(user,pass);
            if(u!=null){
                user_Ath = u;
                FXMLLoader fxmlLoader = new FXMLLoader(application.class.getResource("home-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 900, 600);
                this.parentStage.setScene(scene);
                this.parentStage.setTitle("Home");
                log.info(" Your now in register page Email : "+ user_Ath.getEmail()+" Name : "+user_Ath.getLastname()+" "+user_Ath.getFirstname());
            }
            else{
                System.out.println("Erreur Connexion");
                alertHelper.ShowError("Erreur","Erreur de connexion","login ou mot de passe incorrect");
                log.error("Erreur de connexion , Login ou password incorrecte");
            }


        } catch (Exception e) {
            log.fatal("Error : "+ e.getMessage());
            e.printStackTrace();
        }
    }

}
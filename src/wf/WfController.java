/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author wadeshu
 */
public class WfController implements Initializable{

    @FXML
    GridPane gpInfo;
    @FXML
    ImageView imgTitle;
    @FXML
    TableView tableUserInfo;

    private Wf application;

    
    public void setApp(Wf application) {
        this.application = application;
        System.out.println("bind");
        imgTitle.fitWidthProperty().bind(((BorderPane)imgTitle.getParent()).widthProperty()); 
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void fillInfoItem(String[] infos){
//        FXMLLoader loader = new FXMLLoader();
//        InputStream in = Wf.class.getResourceAsStream("itemInfo.fxml");
//        loader.setBuilderFactory(new JavaFXBuilderFactory());
        try{
//          HBox hbox = loader.load(in); 
          HBox node = FXMLLoader.load(getClass().getResource("itemInfo.fxml"));
          
          gpInfo.getChildren().add(node);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}

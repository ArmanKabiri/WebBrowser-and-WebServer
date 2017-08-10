/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author arman
 */
public class ClientCellFXMLController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Client clinet;
    @FXML
    private AnchorPane parentAnchorPane;
    @FXML
    private Label label_ip;
    @FXML
    private Label label_port;

    public ClientCellFXMLController(Client clinet) {
        this.clinet = clinet;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientCellFXML.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label_ip.setText(clinet.ip);
        label_port.setText(clinet.port + "");
    }

    public Node getMainView() {
        return parentAnchorPane;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

/**
 *
 * @author arman
 */
public class FXMLDocumentController implements Initializable {

    private enum WebServerStatus {
        on, off;
    }

    private WebServerStatus webServerStatus = WebServerStatus.off;
    private NetworkMainTread network;

    private Label label;
    @FXML
    private ListView<Client> listView_clients;
    @FXML
    private Button btn_startStop;
    @FXML
    private ListView<String> listView_logs;

//    private void handleButtonAction(ActionEvent event) {
//        System.out.println("You clicked me!");
//        label.setText("Hello World!");
//    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listView_clients.setItems(FXCollections.observableArrayList());
        listView_clients.setCellFactory((ListView<Client> param) -> new ClientListCell());

        listView_logs.setItems(FXCollections.observableArrayList());
        listView_logs.setCellFactory((ListView<String> param) -> new TextFieldListCell<>(new StringConverter<String>() {

            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));

    }

    @FXML
    private void onClick_btn_startStop(ActionEvent event) {
        if (webServerStatus == WebServerStatus.off) {
            try {
                webServerStatus = WebServerStatus.on;
                btn_startStop.setText("Stop");
                network = new NetworkMainTread(this);
                network.start();
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            webServerStatus = WebServerStatus.off;
            btn_startStop.setText("Start");
            network.stop();
            network.destroy();
        }
    }

    public void addClientToList(Client client) {
        listView_clients.getItems().add(listView_clients.getItems().size(), client);
        listView_clients.scrollTo(client);

    }

    public void removeClientFromList(Client client) {
        int a = listView_clients.getItems().indexOf(client);
        listView_clients.getItems().remove(client);
    }

    public void putLog(String log) {
        listView_logs.getItems().add(listView_logs.getItems().size(), log);
        listView_logs.scrollTo(log);
    }
}

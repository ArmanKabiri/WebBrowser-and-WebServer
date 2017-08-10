/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import javafx.scene.control.ListCell;

/**
 *
 * @author arman
 */
public class ClientListCell extends ListCell<Client> {

    @Override
    public void updateItem(Client client, boolean empty) {
        super.updateItem(client, empty);
        if (empty) {
            setGraphic(null);
        } else if (client != null) {
            ClientCellFXMLController clientControl = new ClientCellFXMLController(client);
            setGraphic(clientControl.getMainView());
        }
    }
}

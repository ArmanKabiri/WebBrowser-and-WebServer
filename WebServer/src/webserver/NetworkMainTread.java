/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;

/**
 *
 * @author arman
 */
public class NetworkMainTread extends Thread {

    private final ServerSocket dispatcherSocket;
    private boolean isOn = true;
    private final Initializable uiControler;

    public NetworkMainTread(Initializable ui) throws IOException {
        uiControler = ui;
        this.dispatcherSocket = new ServerSocket(8000); //try some other port
    }

    @Override
    public void run() {
        try {
            while (isOn) {
                Socket acceptSocket = dispatcherSocket.accept();
                ResponserThread responserThread = new ResponserThread(acceptSocket,uiControler);
                responserThread.start();
            }

        } catch (IOException ex) {
            Logger.getLogger(NetworkMainTread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void destroy() {
        try {
            dispatcherSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkMainTread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

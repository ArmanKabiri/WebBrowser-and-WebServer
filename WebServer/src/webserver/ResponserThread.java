/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author arman
 */
public class ResponserThread extends Thread {

    Socket socket;
    Initializable uiControler;

    public ResponserThread(Socket socket, Initializable ui) {
        uiControler = ui;
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader bufferReader = null;
        try {
            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = "";
            String temp = "";
            StringBuilder sb = new StringBuilder();
            int endlCount = 0;
            while ((temp = bufferReader.readLine()) != null) {
                if (temp.equals("")) {
                    break;
                }
                sb.append(temp + "\n");
                if (temp.equals("")) {
                    endlCount++;
                } else {
                    endlCount = 0;
                }
                if (endlCount == 2) {
                    break;
                }
            }
            request = sb.toString();

            responseRequest(request);

        } catch (IOException ex) {
            Logger.getLogger(ResponserThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferReader.close();
                socket.close();
                sleep(3000);
                Platform.runLater(() -> {
                    Client client = new Client(socket.getInetAddress().getHostAddress(), socket.getPort());
                    ((FXMLDocumentController) uiControler).removeClientFromList(client);
                    String log = "";
                    log += client + " disconnected.";
                    ((FXMLDocumentController) uiControler).putLog(log);
                });
            } catch (IOException ex) {
                Logger.getLogger(ResponserThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ResponserThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String responseRequest(String request) {
        String response = "";
        int index_get = request.indexOf("GET");
        int index_HTTP = request.indexOf("HTTP");

        String filePath = request.substring(index_get + 4, index_HTTP - 1);
        Platform.runLater(() -> {
            String log = "";
            Client client = new Client(socket.getInetAddress().getHostAddress(), socket.getPort());
            log += client + " request for get file : " + filePath;
            ((FXMLDocumentController) uiControler).putLog(log);
        });

        File file = new File("www" + filePath);
        try {
            if (filePath.contains(".jpg") || filePath.contains(".png")) {
                response = "HTTP/1.1 200 OK\n"
                        + "Date: Tue, 21 Jun 2016 21:22:30 GMT\n"
                        + "Content-Type: image\n"
                        + "Last-Modified: Tue, 21 Jun 2016 21:17:24 GMT\n"
                        + "Connection: close" + "\n\n";
                OutputStream out = socket.getOutputStream();
                Path path = Paths.get("www" + filePath);
                byte[] data = Files.readAllBytes(path);
                out.write(response.getBytes());
                out.write(data);
            } else {
                String html = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                response = "HTTP/1.1 200 OK\n"
                        + "Date: Tue, 21 Jun 2016 21:22:30 GMT\n"
                        //                    + "Content-Type: text/html; charset=utf-8\n"
                        + "Last-Modified: Tue, 21 Jun 2016 21:17:24 GMT\n"
                        + "Connection: close" + "\n\n" + html;
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.println(response);
                pw.flush();
            }

        } catch (IOException ex) {
            file = new File("www/not_found.html");
            String notFoundHtml = "";
            try {
                notFoundHtml = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            } catch (IOException ex1) {
                Logger.getLogger(ResponserThread.class.getName()).log(Level.SEVERE, null, ex1);
            }
            response = "HTTP/1.1 404 Not Found\n"
                    + "Content-Type: text/html\n"
                    + "Connection: close" + "\n\n" + notFoundHtml;
        }
        return response;
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        Platform.runLater(() -> {
            String log = "";
            Client client = new Client(socket.getInetAddress().getHostAddress(), socket.getPort());
            log += client + " connected.";
            ((FXMLDocumentController) uiControler).putLog(log);
            ((FXMLDocumentController) uiControler).addClientToList(client);
        });
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import BLL.CacheManager;
import BLL.NetworkManager;
import Network.HtmlUtil;
import Network.NoDNSRespondException;
import Network.PageNotFoundException;
import Network.PageRedirectEXception;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author arman
 */
public class FXML_MainController implements Initializable {

    CacheManager cacheManager;
    NetworkManager networkManager=new NetworkManager();
    @FXML
    private TextField txt_url;
    @FXML
    private Button btn_go;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button btn_addTab;

    private CloseTabEventHandler closeTabEventHandler;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                
//            }
//        }).start();
        cacheManager = new CacheManager();
        
        closeTabEventHandler = new CloseTabEventHandler(tabPane);
        tabPane.getTabs().get(0).setClosable(true);
        tabPane.tabClosingPolicyProperty().setValue(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab old, Tab newTab) {
                        txt_url.setText((String) newTab.getProperties().get("url"));
                    }
                }
        );
        tabPane.getTabs().get(0).setOnCloseRequest(closeTabEventHandler);
    }

    @FXML
    private void onCLick_btn_go(ActionEvent event) {
        try {
            URL url = new URL(txt_url.getText());
            int activeTabIndex = tabPane.getSelectionModel().getSelectedIndex();
            tabPane.getTabs().get(activeTabIndex).getProperties().put("url", url.toString());
            startLoadPageTask(url, activeTabIndex);
        } catch (MalformedURLException ex) {
            showAlert("Error", "URL", "URL is not Correct", AlertType.ERROR);
        }
    }

    @FXML
    private void onClick_btn_addTab(ActionEvent event) {
        Tab newTab = new Tab("Tab");
        AnchorPane pane = new AnchorPane();
        WebView webV = new WebView();
        AnchorPane.setTopAnchor(webV, 0d);
        AnchorPane.setBottomAnchor(webV, 0d);
        AnchorPane.setLeftAnchor(webV, 0d);
        AnchorPane.setRightAnchor(webV, 0d);
        pane.getChildren().add(webV);
        newTab.setContent(pane);
        tabPane.getTabs().add(newTab);
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(newTab);
        newTab.setClosable(true);
        newTab.setOnCloseRequest(closeTabEventHandler);
    }

    private void startLoadPageTask(final URL url, final int tabIndex) {

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new Runnable() {

            @Override
            public void run() {
                try {
                    final String htmlContent = networkManager.getHtmlFile(url);
                    if (htmlContent != null) {
                        final String header = HtmlUtil.getTitle(htmlContent);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                tabPane.getTabs().get(tabIndex).setText(header);
                                printHtmlOnAlert(htmlContent, url.getHost());
                                loadHtmlOnBrowserTab(htmlContent, tabIndex);
                            }
                        });
                    }

                } catch (NoDNSRespondException ex) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            showAlert("Error", "DNS Respond", "No Respond Received From DNS", AlertType.ERROR);
                        }
                    });
                } catch (final PageNotFoundException ex) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (ex.html != null) {
                                showAlert("Error", "Page Not Found", "Page Not Fond on " + ex.host, AlertType.ERROR);
                                loadHtmlOnBrowserTab(ex.html, tabIndex);
                            } else {
                                showAlert("Error", "Page Not Found", "Page Not Fond on " + ex.host, AlertType.ERROR);
                            }
                        }
                    });

                } catch (final PageRedirectEXception ex) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            printHtmlOnAlert(ex.html, ex.location);
                            loadHtmlOnBrowserTab(ex.html, tabIndex);
                        }
                    });
                    try {
                        startLoadPageTask(new URL(ex.location), tabIndex);
                    } catch (MalformedURLException ex1) {
                        showAlert("Error", "Redirect", "Redirected Location URL was Wrong", AlertType.ERROR);
                    }
                } catch (MalformedURLException ex) {
                    showAlert("Error", "URL", "URL is not Correct", AlertType.ERROR);
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    private void printHtmlOnAlert(final String html, final String host) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("HTML");
        alert.setHeaderText("Html File Received from " + host);

        Label label = new Label("html file is:");
        TextArea textArea = new TextArea(html);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        GridPane expContent = new GridPane();
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.show();

    }

    private void showAlert(String title, String header, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private void loadHtmlOnBrowserTab(String html, int tabIndex) {
        AnchorPane pane = (AnchorPane) tabPane.getTabs().get(tabIndex).getContent();
        WebView webV = (WebView) pane.getChildren().get(0);
        WebEngine webEngine = webV.getEngine();
        webEngine.loadContent(html);
    }

    @FXML
    private void onKeyPressed_txt_url(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onCLick_btn_go(null);
        }
    }
}

class CloseTabEventHandler implements EventHandler<Event> {

    TabPane tabPane;

    public CloseTabEventHandler(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    @Override
    public void handle(Event event) {
        if (tabPane.getTabs().size() == 1) {
            event.consume();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import BLL.CacheManager;
import Entities.CacheWebPage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author arman
 */
public class Http_s_TcpConnection {

    URL url;
    int port;
    String protocol;
    CacheManager cacheManager = new CacheManager();

    public Http_s_TcpConnection(URL url) {
        setUrl(url);
    }

    public Http_s_TcpConnection() {
    }

    public void setUrl(URL url) {
        this.url = url;
        
        if (url.getProtocol().equalsIgnoreCase("https")) {
            protocol = "https";
            port = 443;
        } else if (url.getProtocol().equalsIgnoreCase("http")) {
            port = 80;
            protocol = "http";
        }
        
        int port = url.getPort();
        if(port!=(-1)){
            this.port=port;
        }
    }

    public CacheWebPage getHtmlFile(String hostIP, String lastDownloadedFileDate) throws NoDNSRespondException, PageNotFoundException, PageRedirectEXception, PageNotModifiedException {
        String htmlStr = null;
        String htmlDate = null;
        String ipAddress = hostIP;

        Socket client = null;
        try {
            System.out.println("Connecting to " + url.getHost() + " on port " + port);
            if (protocol.equals("http")) {
                client = new Socket(InetAddress.getByName(ipAddress), port);
            } else if (protocol.equals("https")) {
                client = SSLSocketFactory.getDefault().createSocket(url.getHost(), port);
            }
//            client.setSoTimeout(30000);
            System.out.println("connected to " + client.getRemoteSocketAddress() + "on protocol " + protocol);

            String req = (protocol.equals("http")) ? generateHttpGetQuery(lastDownloadedFileDate) : generateHttpsGetQuery(lastDownloadedFileDate);

            PrintWriter pw = new PrintWriter(client.getOutputStream());
            pw.println(req);
            pw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String res = "";
            String t = "";
            StringBuilder sb = new StringBuilder();
            System.out.println("try to geting file");

            while ((t = br.readLine()) != null) {
//                System.out.println(t);
                sb.append(t + "\n");
                if (t.indexOf("</html>") != (-1) || t.indexOf("</HTML>") != (-1)) {
                    break;
                }
            }
            res = sb.toString();
            System.out.println("file received " + (res.length() / 1024) + " KB");
            htmlStr = getHtmlPart(res);
            if (getRespondStatus(res) == HttpRespond.OK) {
                htmlDate = getDownloadedPageDate(res);
            } else if (getRespondStatus(res) == HttpRespond.Redirect) {
                String redirectUrl = getRedirectLocation(res);
                throw new PageRedirectEXception(htmlStr, redirectUrl);
            } else if (getRespondStatus(res) == HttpRespond.NotModified) {
                throw new PageNotModifiedException(url);
            } else if (getRespondStatus(res) == HttpRespond.NotFound) {
                throw new PageNotFoundException(htmlStr, url.getHost());
            }
        } catch (SocketTimeoutException ex) {
            throw new PageNotFoundException(null, url.getHost());
        } catch (SocketException e) {
            System.out.println("Netwotk is unreachable");
            throw new PageNotFoundException(null, url.getHost());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(Http_s_TcpConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new CacheWebPage(url.getHost(), url.getFile(), htmlStr, htmlDate);
    }

    private String getDownloadedPageDate(String msg) {
        String date = null;
        try {
            int indexDate = msg.toLowerCase().indexOf("date:");
            indexDate += 6;
            int endLineIndex = msg.indexOf('\n', indexDate);
            date = msg.substring(indexDate, endLineIndex);
        } catch (Exception x) {
        }
        return date;
    }

    private String getHtmlPart(String message) {
        int index = message.indexOf("<!DOCTYPE html");
        if (index == -1) {
            index = message.indexOf("<html");
        }
        if (index == -1) {
            index = message.indexOf("<HTML");
        }
        if (index == -1) {
            index = message.indexOf("<!DOCTYPE html");
        }
        if (index == -1) {
            index = message.indexOf("<!doctype html");
        }
        if (index == -1) {
            index = message.indexOf("<!doctype HTML");
        }
        if (index != -1) {
            return message.substring(index);
        } else {
            return null;
        }
    }

    private HttpRespond getRespondStatus(String msg) {
        int index = msg.indexOf("\n");
        int okIndex = msg.substring(0, index).indexOf("200 OK");
        int movedIndex = msg.substring(0, index).indexOf("301");//moved permanently
        int movedIndex2 = msg.substring(0, index).indexOf("302");//moved permanently
        int notModified = msg.substring(0, index).indexOf("304");//moved permanently
        int notFoundIndex = msg.substring(0, index).indexOf("404");//moved permanently
        if (okIndex != (-1)) {
            return HttpRespond.OK;
        } else if (movedIndex != (-1) || movedIndex2 != (-1)) {
            return HttpRespond.Redirect;
        } else if (notModified != (-1)) {
            return HttpRespond.NotModified;
        } else if (notFoundIndex != (-1)) {
            return HttpRespond.NotFound;
        }
        return HttpRespond.NotFound;
    }

    private String generateHttpGetQuery(String lastDownloadedFileDate) {
        String req = "GET " + (url.getFile() != "" ? url.getFile() : "/") + " HTTP/1.1\r\n"
                + "Host: " + (url.getHost().startsWith("www.") ? url.getHost() : "" + url.getHost()) + "\r\n"
                + "Connection: Keep-Alive\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml\r\n";
        //                    + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n"
        //                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) "
        //                + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.86 "
        //                + "Safari/537.36\r\n"
        //                    + "Accept-Encoding: gzip, deflate, sdch"
        if (lastDownloadedFileDate != null) {
            req += ("If-Modified-Since: " + lastDownloadedFileDate + "\r\n");
        }
        req += "Accept-Language: en-US\r\n\r\n";
        return req;
    }

    private String generateHttpsGetQuery(String lastDownloadedFileDate) {
        String req = "GET " + (url.getFile() != "" ? url.getFile() : "/") + " HTTP/1.1\r\n"
                + "Host: " + (url.getHost().startsWith("www.") ? url.getHost() : "" + url.getHost()) + ":" + port + "\r\n"
                + "Connection: Keep-Alive\r\n"
                + "Accept: text/html,application/xhtml+xml,application/xml\r\n"
                //                    + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n"
                + "Agent: SSL-TEST\r\n";
        //                + "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) "
        //                + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.86 "
        //                + "Safari/537.36\r\n"
        //                    + "Accept-Encoding: gzip, deflate, sdch"
        if (lastDownloadedFileDate != null) {
            req += ("If-Modified-Since: " + lastDownloadedFileDate + "\r\n");
        }
        req += "Accept-Language: en-US\r\n\r\n";
        return req;
    }

    private String getRedirectLocation(String msg) {

        int locationIndex = msg.toLowerCase().indexOf("location:");
        int urlIndex = locationIndex + 10;
        int endOfLineIndex = msg.indexOf((int) '\n', urlIndex);
        String host = msg.substring(urlIndex, endOfLineIndex);

        return host;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

/**
 *
 * @author arman
 */
public class PageNotFoundException extends Exception {

    public String html;
    public String host;

    public PageNotFoundException(String html, String host) {
        super("page " + host + " not found");
        this.html = html;
        this.host = host;
    }
}

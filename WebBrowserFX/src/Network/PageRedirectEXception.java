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
public class PageRedirectEXception extends Exception{
    public String html;
    public String location;

    public PageRedirectEXception(String html, String location) {
        super("page redirected to "+location);
        this.html = html;
        this.location = location;
    }
    
}

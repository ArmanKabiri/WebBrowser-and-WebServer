/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Network;

import java.net.URL;

/**
 *
 * @author arman
 */
public class PageNotModifiedException extends Exception{
    URL url;

    public PageNotModifiedException(URL url) {
        super("page not modified");
        this.url = url;
    }
}

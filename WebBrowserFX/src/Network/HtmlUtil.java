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
public class HtmlUtil {

    public static String getTitle(String html) {
        String title = "no name";
        int indexStart = html.indexOf("<title>");
        int indexEnd = html.indexOf("</title>");
        if (indexStart != (-1) && indexEnd != (-1)) {
            title = html.substring(indexStart + 7, indexEnd);

            title = title.replace('\n', ' ');
            title = title.replace('\t', ' ');
            if(title.length()>30)
                title=title.substring(0, 30)+"...";
        }
        return title;
    }
}

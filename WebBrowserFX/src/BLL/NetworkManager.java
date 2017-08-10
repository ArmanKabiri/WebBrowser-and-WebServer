/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import Entities.CacheDNS;
import Entities.CacheWebPage;
import Network.DNS;
import Network.Http_s_TcpConnection;
import Network.NoDNSRespondException;
import Network.PageNotFoundException;
import Network.PageNotModifiedException;
import Network.PageRedirectEXception;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.validator.routines.InetAddressValidator;

/**
 *
 * @author arman
 */
public class NetworkManager {

    CacheManager cacheManager = new CacheManager();

    public String getHtmlFile(URL url) throws MalformedURLException, NoDNSRespondException, PageNotFoundException, PageRedirectEXception {
        String ip = url.getHost();
        String html = null;
        InetAddressValidator ipValidator = new InetAddressValidator();
        if (!ipValidator.isValidInet4Address(url.getHost())) {  //if host was not ip
            ip = cacheManager.getIP(url.getHost());
            if (ip == null) {
                DNS dns = new DNS();
                ip = dns.resolveHostIpAddress(url.getHost());
                if (ip != null) {
                    cacheManager.addToCacheDNS(new CacheDNS(url.getHost(), ip));
                } else {
                    throw new NoDNSRespondException("network is unreachable\nor dns server not response");
                }
            }
        }
        
        Http_s_TcpConnection httpConnection = new Http_s_TcpConnection(url);
        CacheWebPage cachedWeb;

        try {
            cachedWeb = httpConnection.getHtmlFile(ip, cacheManager.getLastDownloadedFielDate(url));
            if (cachedWeb.getDateTime() != null) {
                cacheManager.deleteWebCache(url);
                cacheManager.addToCacheWebPage(cachedWeb);
            }
        } catch (PageNotModifiedException ex) {
            cachedWeb = cacheManager.getCachedWebPage(url);
        } catch (PageNotFoundException ex) {
            cachedWeb = cacheManager.getCachedWebPage(url);
            if (cachedWeb == null) {
                throw ex;
            }
        }
        if (cachedWeb != null) {
            html = cachedWeb.getHtmlContent();
        }

        return html;
    }
}

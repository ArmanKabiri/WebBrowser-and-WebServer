/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import Entities.CacheDNS;
import Entities.CacheWebPage;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author arman
 */
public class CacheManager {

    private static SessionFactory factory;

    public CacheManager() {
        try {
//            factory = new Configuration().addAnnotatedClass(CacheDNS.class).addAnnotatedClass(CacheWebPage.class).configure().buildSessionFactory();
            factory = new Configuration().configure("/hibernate.cfg.xml").buildSessionFactory();
//          entityManagerFactory = Persistence.createEntityManagerFactory("WebBrowserFX");

//            factory = new AnnotationConfiguration().configure().addAnnotatedClass(CacheDNS.class).buildSessionFactory();
        } catch (Exception x) {
            x.printStackTrace();
        }

    }

    public int addToCacheDNS(CacheDNS obj) {
        Session session = factory.openSession();
        Transaction tx = null;
        int id = -1;
        try {
            tx = session.beginTransaction();
            id = (int) session.save(obj);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return id;
    }

    public int addToCacheWebPage(CacheWebPage obj) {
        Session session = factory.openSession();
        Transaction tx = null;
        int id = -1;
        try {
            tx = session.beginTransaction();
            id = (int) session.save(obj);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return id;
    }
//

    public String getIP(String host) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = session.createCriteria(CacheDNS.class);
        String ip = null;

        List caches = criteria.list();
        for (Iterator<CacheDNS> iterator = caches.iterator(); iterator.hasNext();) {
            CacheDNS dns = (CacheDNS) iterator.next();
            if (dns.getHost().equalsIgnoreCase(host)) {
                ip = dns.getIp();
                break;
            }
        }
        tx.commit();
        session.close();
        if (ip != null) {
            System.out.println("ip " + ip + " for " + host + " was in cache");
        }
        return ip;

    }

    public CacheWebPage getCachedWebPage(URL url) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = session.createCriteria(CacheWebPage.class);
        CacheWebPage result = null;
        List caches = criteria.list();
        for (Iterator<CacheWebPage> iterator = caches.iterator(); iterator.hasNext();) {
            CacheWebPage web = (CacheWebPage) iterator.next();
            if (web.getHost().equalsIgnoreCase(url.getHost()) && web.getFile().equalsIgnoreCase(url.getFile())) {
                result = web;
                break;
            }
        }
        tx.commit();
        session.close();
        if (result != null) {
            System.out.println(url.toString() + "Not Modified , Received from cache");
        }
        return result;
    }

    public String getLastDownloadedFielDate(URL url) {
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        Criteria criteria = session.createCriteria(CacheWebPage.class);
        String result = null;
        List caches = criteria.list();
        for (Iterator<CacheWebPage> iterator = caches.iterator(); iterator.hasNext();) {
            CacheWebPage web = (CacheWebPage) iterator.next();
            if (web.getHost().equalsIgnoreCase(url.getHost()) && web.getFile().equalsIgnoreCase(url.getFile())) {
                result = web.getDateTime();
                break;
            }
        }
        tx.commit();
        session.close();
        return result;
    }

    public void deleteWebCache(URL url) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            CacheWebPage w = getCachedWebPage(url);
            if (w != null) {
                session.delete(getCachedWebPage(url));
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}

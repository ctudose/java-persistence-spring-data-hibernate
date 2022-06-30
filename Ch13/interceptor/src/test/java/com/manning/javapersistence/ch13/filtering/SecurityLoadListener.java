package com.manning.javapersistence.ch13.filtering;

import org.hibernate.HibernateException;
import org.hibernate.event.internal.DefaultLoadEventListener;
import org.hibernate.event.spi.LoadEvent;

import java.io.Serializable;

public class SecurityLoadListener extends DefaultLoadEventListener {

  @Override
    public void onLoad(LoadEvent event, LoadType loadType)
        throws HibernateException {

        boolean authorized =
            MySecurity.isAuthorized(
                event.getEntityClassName(), event.getEntityId()
            );

        if (!authorized) {
            throw new MySecurityException("Unauthorized access");
        }

        super.onLoad(event, loadType);
    }

    public static class MySecurity {
        static boolean isAuthorized(String entityName, Serializable entityId) {
           return true;
        }
    }

    public static class MySecurityException extends RuntimeException {
        public MySecurityException(String message) {
            super(message);
        }
    }
}


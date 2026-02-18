package com.library.integration.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.config.HibernateConfig;
import com.library.integration.IntegrationTestSupport;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

class HibernateConfigIntegrationTest extends IntegrationTestSupport {

    @Test
    void sessionFactoryIsAvailableAndCanOpenSession() {
        assertNotNull(HibernateConfig.getSessionFactory());
        assertTrue(HibernateConfig.getSessionFactory().isOpen());

        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            assertTrue(session.isOpen());
        }
    }
}

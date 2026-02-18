package com.library.integration;

import com.library.config.HibernateConfig;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class IntegrationTestSupport {
    @BeforeAll
    static void ensureTestDbDirectory() throws Exception {
        Files.createDirectories(Paths.get("target/test-db"));
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from Loan").executeUpdate();
            session.createMutationQuery("delete from BookStock").executeUpdate();
            session.createMutationQuery("delete from Member").executeUpdate();
            session.createMutationQuery("delete from Book").executeUpdate();
            tx.commit();
        }
    }
}

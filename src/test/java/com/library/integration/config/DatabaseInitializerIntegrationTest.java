package com.library.integration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.config.DatabaseInitializer;
import com.library.dao.BookDAO;
import com.library.integration.IntegrationTestSupport;
import com.library.model.Book;
import java.util.List;
import org.junit.jupiter.api.Test;

class DatabaseInitializerIntegrationTest extends IntegrationTestSupport {
    private final BookDAO bookDAO = new BookDAO();

    @Test
    void seedCreatesDefaultBookWhenDatabaseIsEmpty() {
        DatabaseInitializer.seed();

        List<Book> books = bookDAO.findAll();
        assertEquals(1, books.size());
        assertEquals("Effective Java", books.get(0).getTitle());
    }

    @Test
    void seedIsIdempotentWhenDataAlreadyExists() {
        DatabaseInitializer.seed();
        DatabaseInitializer.seed();

        List<Book> books = bookDAO.findAll();
        assertEquals(1, books.size());
        assertTrue(books.stream().anyMatch(b -> "Effective Java".equals(b.getTitle())));
    }
}

package com.library.integration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.library.dao.BookDAO;
import com.library.integration.IntegrationTestSupport;
import com.library.model.Book;
import com.library.model.enums.Genre;
import org.junit.jupiter.api.Test;

class BookDAOIntegrationTest extends IntegrationTestSupport {
    private final BookDAO bookDAO = new BookDAO();

    @Test
    void saveAndFindByIdRoundTrip() {
        Book book = new Book();
        book.setTitle("Domain-Driven Design");
        book.setAuthor("Eric Evans");
        book.setIsbn("978-0321125217");
        book.setGenre(Genre.TECHNOLOGY);

        bookDAO.save(book);

        assertNotNull(book.getId());
        Book loaded = bookDAO.findById(book.getId());
        assertNotNull(loaded);
        assertEquals("Domain-Driven Design", loaded.getTitle());
        assertEquals("Eric Evans", loaded.getAuthor());
    }

    @Test
    void updatePersistsChanges() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("978-0132350884");
        book.setGenre(Genre.TECHNOLOGY);
        bookDAO.save(book);

        book.setTitle("Clean Code 2nd Edition");
        bookDAO.update(book);

        Book loaded = bookDAO.findById(book.getId());
        assertEquals("Clean Code 2nd Edition", loaded.getTitle());
    }

    @Test
    void deleteRemovesEntity() {
        Book book = new Book();
        book.setTitle("Refactoring");
        book.setAuthor("Martin Fowler");
        book.setIsbn("978-0201485677");
        book.setGenre(Genre.TECHNOLOGY);
        bookDAO.save(book);

        bookDAO.delete(book.getId());

        assertNull(bookDAO.findById(book.getId()));
    }
}

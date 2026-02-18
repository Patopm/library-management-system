package com.library.integration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.library.dao.BookDAO;
import com.library.dao.BookStockDAO;
import com.library.integration.IntegrationTestSupport;
import com.library.model.Book;
import com.library.model.BookStock;
import com.library.model.enums.Genre;
import org.junit.jupiter.api.Test;

class BookStockDAOIntegrationTest extends IntegrationTestSupport {
    private final BookDAO bookDAO = new BookDAO();
    private final BookStockDAO bookStockDAO = new BookStockDAO();

    @Test
    void findByBookIdReturnsMatchingStock() {
        Book book = new Book();
        book.setTitle("Neuromancer");
        book.setAuthor("William Gibson");
        book.setIsbn("978-0441569595");
        book.setGenre(Genre.SCIENCE_FICTION);
        bookDAO.save(book);

        BookStock stock = new BookStock();
        stock.setBook(book);
        stock.setTotalQuantity(6);
        stock.setAvailableQuantity(6);
        bookStockDAO.save(stock);

        BookStock loaded = bookStockDAO.findByBookId(book.getId());
        assertNotNull(loaded);
        assertEquals(stock.getId(), loaded.getId());
        assertEquals(6, loaded.getAvailableQuantity());
    }

    @Test
    void findByBookIdReturnsNullWhenNotFound() {
        assertNull(bookStockDAO.findByBookId(999L));
    }
}

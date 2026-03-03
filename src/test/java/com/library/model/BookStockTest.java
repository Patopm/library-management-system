package com.library.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.model.enums.Genre;
import com.library.model.strategy.FictionStockStrategy;
import com.library.model.strategy.MysteryStockStrategy;
import com.library.model.strategy.SciFiStockStrategy;
import org.junit.jupiter.api.Test;

class BookStockTest {

    @Test
    void canBeLentUsesSciFiStrategyWhenGenreIsScienceFiction() {
        Book book = new Book();
        book.setGenre(Genre.SCIENCE_FICTION);

        BookStock stock = new BookStock();
        stock.setBook(book);
        stock.setAvailableQuantity(1);

        assertFalse(stock.canBeLent());
        assertInstanceOf(SciFiStockStrategy.class, stock.getStrategy());
    }

    @Test
    void canBeLentUsesMysteryStrategyWhenGenreIsMystery() {
        Book book = new Book();
        book.setGenre(Genre.MYSTERY);

        BookStock stock = new BookStock();
        stock.setBook(book);
        stock.setAvailableQuantity(6);

        assertTrue(stock.canBeLent()); // Mystery strategy requires >5 copies
        assertInstanceOf(MysteryStockStrategy.class, stock.getStrategy());
    }

    @Test
    void canBeLentUsesFictionStrategyAsDefaultForOtherGenres() {
        Book book = new Book();
        book.setGenre(Genre.BIOGRAPHY);

        BookStock stock = new BookStock();
        stock.setBook(book);
        stock.setAvailableQuantity(1);

        assertTrue(stock.canBeLent());
        assertInstanceOf(FictionStockStrategy.class, stock.getStrategy());
    }
}

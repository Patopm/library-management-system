package main.java.com.library.dao;

import main.java.com.library.model.BookStock;

public class BookStockDAO extends GenericDAO<BookStock> {
    public BookStockDAO() {
        super(BookStock.class);
    }
}
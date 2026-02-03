package com.library.dao;

import com.library.model.BookStock;

public class BookStockDAO extends GenericDAO<BookStock> {
    public BookStockDAO() {
        super(BookStock.class);
    }
}
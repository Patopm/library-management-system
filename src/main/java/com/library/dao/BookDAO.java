package main.java.com.library.dao;

import main.java.com.library.model.Book;

public class BookDAO extends GenericDAO<Book> {
    public BookDAO() {
        super(Book.class);
    }
}
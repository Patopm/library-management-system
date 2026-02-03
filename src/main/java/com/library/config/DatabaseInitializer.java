package com.library.config;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.enums.Genre;
import org.hibernate.Session;

public class DatabaseInitializer {
    public static void seed() {
        BookDAO bookDAO = new BookDAO();
        if (bookDAO.findAll().isEmpty()) {
            Book sample = new Book();
            sample.setTitle("Effective Java");
            sample.setAuthor("Joshua Bloch");
            sample.setIsbn("978-0134685991");
            sample.setGenre(Genre.TECHNOLOGY);
            bookDAO.save(sample);
            System.out.println("Sample data seeded.");
        }
    }
}
package com.library.dao;

import com.library.config.HibernateConfig;
import com.library.model.BookStock;
import org.hibernate.Session;

public class BookStockDAO extends GenericDAO<BookStock> {
    public BookStockDAO() {
        super(BookStock.class);
    }

    public BookStock findByBookId(Long bookId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from BookStock bs where bs.book.id = :bookId",
                    BookStock.class
            ).setParameter("bookId", bookId)
             .uniqueResult();
        }
    }
}

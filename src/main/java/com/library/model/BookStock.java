package com.library.model;

import com.library.model.strategy.*;
import jakarta.persistence.*;

@Entity
@Table(name = "book_stock")
public class BookStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private int totalQuantity;
    private int availableQuantity;

    @Transient
    private BookStockStrategy strategy;

    public BookStock() {}

    @PostLoad
    private void resolveStrategy() {
        if (book == null) return;
        this.strategy = switch (book.getGenre()) {
            case SCIENCE_FICTION -> new SciFiStockStrategy();
            default -> new FictionStockStrategy();
        };
    }

    public boolean canBeLent() {
        if (strategy == null) resolveStrategy();
        return strategy.validateLoan(availableQuantity);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int qty) { this.availableQuantity = qty; }
    public BookStockStrategy getStrategy() { return strategy; }
}
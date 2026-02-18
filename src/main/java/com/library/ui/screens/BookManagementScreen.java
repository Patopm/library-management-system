package com.library.ui.screens;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.library.dao.BookDAO;
import com.library.dao.BookStockDAO;
import com.library.model.Book;
import com.library.model.BookStock;
import com.library.model.enums.Genre;
import com.library.ui.components.FormBuilder;
import com.library.ui.components.TableBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookManagementScreen extends AbstractScreenWindow {
    private final BookDAO bookDAO = new BookDAO();
    private final BookStockDAO stockDAO = new BookStockDAO();
    private final Table<String> table;

    public BookManagementScreen(com.googlecode.lanterna.gui2.MultiWindowTextGUI gui) {
        super("Book Management", gui);
        Panel content = createVerticalContent();

        this.table = TableBuilder.createEntityTable("Books", "ID", "Title", "Author", "Genre", "Available", "Total");
        refreshTable();

        content.addComponent(table);
        content.addComponent(createActionRow(
                new Button("Add New Book", this::openAddBookForm),
                new Button("Back", this::close)
        ));
        content.addComponent(createHintLabel("Press ESC to return to main menu."));

        setComponent(content);
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<Book> books = bookDAO.findAll();
        Map<Long, BookStock> stocksByBookId = stockDAO.findAll().stream()
                .filter(stock -> stock.getBook() != null)
                .collect(Collectors.toMap(stock -> stock.getBook().getId(), stock -> stock, (a, b) -> a));

        for (Book b : books) {
            BookStock stock = stocksByBookId.get(b.getId());
            table.getTableModel().addRow(
                b.getId().toString(),
                b.getTitle(),
                b.getAuthor(),
                b.getGenre().name(),
                stock != null ? String.valueOf(stock.getAvailableQuantity()) : "-",
                stock != null ? String.valueOf(stock.getTotalQuantity()) : "-"
            );
        }
    }

    private void openAddBookForm() {
        final BasicWindow formWindow = new BasicWindow("Add New Book");
        formWindow.setHints(List.of(Window.Hint.CENTERED));

        FormBuilder fb = new FormBuilder();
        fb.addField("Title:", "title");
        fb.addField("Author:", "author");
        fb.addField("ISBN:", "isbn");
        fb.addField("Genre:", "genre");
        fb.addField("Initial Stock:", "initialStock");

        Panel panel = fb.getPanel();
        panel.addComponent(createHintLabel("Genres: " + allowedGenres()));
        panel.addComponent(createHintLabel("Use Manage Existing Book to update stock for an existing title."));
        panel.addComponent(createActionRow(
                new Button("Save New Book", () -> saveBook(fb, formWindow)),
                new Button("Manage Existing Book", this::openManageBookForm),
                new Button("Cancel", formWindow::close)
        ));

        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }

    private void saveBook(FormBuilder fb, BasicWindow formWindow) {
        String title = fb.getValue("title");
        String author = fb.getValue("author");
        String isbn = fb.getValue("isbn");
        String genreInput = fb.getValue("genre");
        String initialStockInput = fb.getValue("initialStock");

        if (title.isBlank() || author.isBlank() || genreInput.isBlank()) {
            MessageDialog.showMessageDialog(gui, "Validation", "Title, Author, and Genre are required.");
            return;
        }

        try {
            Genre genre;
            try {
                genre = Genre.valueOf(genreInput.toUpperCase().replace(' ', '_'));
            } catch (IllegalArgumentException ex) {
                MessageDialog.showMessageDialog(
                        gui,
                        "Validation",
                        "Invalid genre. Use one of: " + allowedGenres()
                );
                return;
            }
            int initialStock = parseNonNegativeInt(initialStockInput, "Initial Stock");

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setGenre(genre);
            bookDAO.save(book);

            BookStock stock = new BookStock();
            stock.setBook(book);
            stock.setTotalQuantity(initialStock);
            stock.setAvailableQuantity(initialStock);
            stockDAO.save(stock);

            refreshTable();
            formWindow.close();
        } catch (NumberFormatException ex) {
            MessageDialog.showMessageDialog(gui, "Validation", "Initial Stock must be a valid number.");
        } catch (IllegalArgumentException ex) {
            MessageDialog.showMessageDialog(gui, "Validation", ex.getMessage());
        } catch (Exception ex) {
            MessageDialog.showMessageDialog(gui, "Error", ex.getMessage());
        }
    }

    private void openManageBookForm() {
        final BasicWindow formWindow = new BasicWindow("Manage Book Stock");
        formWindow.setHints(List.of(Window.Hint.CENTERED));

        FormBuilder fb = new FormBuilder();
        fb.addField("Book ID:", "bookId");
        fb.addField("Total Quantity:", "totalQuantity");
        fb.addField("Available Quantity:", "availableQuantity");

        Panel panel = fb.getPanel();
        panel.addComponent(createHintLabel("If stock does not exist yet, it will be created."));
        panel.addComponent(createActionRow(
                new Button("Save Stock", () -> saveManagedBook(fb, formWindow)),
                new Button("Cancel", formWindow::close)
        ));

        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }

    private void saveManagedBook(FormBuilder fb, BasicWindow formWindow) {
        try {
            Long bookId = Long.parseLong(fb.getValue("bookId"));
            int totalQuantity = parseNonNegativeInt(fb.getValue("totalQuantity"), "Total Quantity");
            int availableQuantity = parseNonNegativeInt(fb.getValue("availableQuantity"), "Available Quantity");

            if (availableQuantity > totalQuantity) {
                MessageDialog.showMessageDialog(
                        gui,
                        "Validation",
                        "Available quantity cannot be greater than total quantity."
                );
                return;
            }

            Book book = bookDAO.findById(bookId);
            if (book == null) {
                MessageDialog.showMessageDialog(gui, "Validation", "Book ID not found.");
                return;
            }

            BookStock stock = stockDAO.findByBookId(bookId);
            if (stock == null) {
                stock = new BookStock();
                stock.setBook(book);
                stock.setTotalQuantity(totalQuantity);
                stock.setAvailableQuantity(availableQuantity);
                stockDAO.save(stock);
            } else {
                stock.setTotalQuantity(totalQuantity);
                stock.setAvailableQuantity(availableQuantity);
                stockDAO.update(stock);
            }

            refreshTable();
            formWindow.close();
        } catch (NumberFormatException ex) {
            MessageDialog.showMessageDialog(
                    gui,
                    "Validation",
                    "Book ID, Total Quantity, and Available Quantity must be valid numbers."
            );
        } catch (IllegalArgumentException ex) {
            MessageDialog.showMessageDialog(gui, "Validation", ex.getMessage());
        } catch (Exception ex) {
            MessageDialog.showMessageDialog(gui, "Error", ex.getMessage());
        }
    }

    private int parseNonNegativeInt(String value, String fieldName) {
        if (value == null || value.isBlank()) return 0;
        int parsed = Integer.parseInt(value);
        if (parsed < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
        return parsed;
    }

    private String allowedGenres() {
        return java.util.Arrays.stream(Genre.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}

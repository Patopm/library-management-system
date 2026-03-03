package com.library.ui.screens;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.library.dao.BookDAO;
import com.library.dao.BookStockDAO;
import com.library.dao.LoanDAO;
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
    private final LoanDAO loanDAO = new LoanDAO();
    private final Label summaryLabel = new Label("");
    private final Table<String> table;

    public BookManagementScreen(com.googlecode.lanterna.gui2.MultiWindowTextGUI gui) {
        super("Book Management", gui);
        Panel content = createVerticalContent();

        content.addComponent(createScreenHeader(
                "Book Catalog",
                "Browse inventory, open a row with ENTER, and keep stock aligned with active loans."
        ));
        content.addComponent(createVerticalGap());

        content.addComponent(summaryLabel.addStyle(SGR.BOLD));
        content.addComponent(createVerticalGap());

        this.table = TableBuilder.createEntityTable("Books", "ID", "Title", "Author", "Genre", "Available", "Total");
        this.table.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        refreshTable();
        this.table.setSelectAction(this::openManageBookFormFromSelection);

        content.addComponent(table);
        content.addComponent(createVerticalGap());
        content.addComponent(createActionRow(
                new Button("Add New Book", this::openAddBookForm),
                new Button("Back", this::close)
        ));
        content.addComponent(createHintLabel("List controls: UP/DOWN navigate, ENTER opens selected book, ESC closes this screen."));

        setComponent(content);
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<Book> books = bookDAO.findAll();
        Map<Long, BookStock> stocksByBookId = stockDAO.findAll().stream()
                .filter(stock -> stock.getBook() != null)
                .collect(Collectors.toMap(stock -> stock.getBook().getId(), stock -> stock, (a, b) -> a));
        int totalAvailable = 0;

        for (Book b : books) {
            BookStock stock = stocksByBookId.get(b.getId());
            if (stock != null) {
                totalAvailable += stock.getAvailableQuantity();
            }
            table.getTableModel().addRow(
                b.getId().toString(),
                b.getTitle(),
                b.getAuthor(),
                b.getGenre().name(),
                stock != null ? String.valueOf(stock.getAvailableQuantity()) : "-",
                stock != null ? String.valueOf(stock.getTotalQuantity()) : "-"
            );
        }

        summaryLabel.setText(
                "Books: " + books.size()
                        + "  |  Available copies: " + totalAvailable
                        + "  |  Press ENTER on a row to manage details"
        );
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
        panel.addComponent(createHintLabel("Use ENTER on a selected book row to update stock."));
        panel.addComponent(createActionRow(
                new Button("Save New Book", () -> saveBook(fb, formWindow)),
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

    private void openManageBookFormFromSelection() {
        if (table.getTableModel().getRowCount() == 0) {
            MessageDialog.showMessageDialog(gui, "Book Management", "No books available to manage.");
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= table.getTableModel().getRowCount()) {
            MessageDialog.showMessageDialog(gui, "Book Management", "Select a book row first.");
            return;
        }

        String bookIdCell = table.getTableModel().getRow(selectedRow).get(0);
        try {
            Long bookId = Long.parseLong(bookIdCell);
            Book book = bookDAO.findById(bookId);
            if (book == null) {
                MessageDialog.showMessageDialog(gui, "Book Management", "Selected book no longer exists.");
                return;
            }
            openManageBookForm(book);
        } catch (NumberFormatException ex) {
            MessageDialog.showMessageDialog(gui, "Book Management", "Selected row has an invalid Book ID.");
        }
    }

    private void openManageBookForm(Book book) {
        final BasicWindow formWindow = new BasicWindow("Manage Book Stock");
        formWindow.setHints(List.of(Window.Hint.CENTERED));

        BookStock existingStock = stockDAO.findByBookId(book.getId());
        long activeLoans = existingStock == null ? 0 : loanDAO.countActiveByStock(existingStock.getId());
        int currentTotal = existingStock == null ? 0 : existingStock.getTotalQuantity();
        int currentAvailable = Math.max(0, currentTotal - (int) activeLoans);

        Panel details = new Panel(new GridLayout(2));
        details.addComponent(new Label("Book ID:"));
        details.addComponent(new Label(String.valueOf(book.getId())));
        details.addComponent(new Label("Active Loans:"));
        details.addComponent(new Label(String.valueOf(activeLoans)));
        details.addComponent(new Label("Available (calculated):"));
        details.addComponent(new Label(String.valueOf(currentAvailable)));

        FormBuilder fb = new FormBuilder();
        fb.addField("Title:", "title");
        fb.addField("Author:", "author");
        fb.addField("ISBN:", "isbn");
        fb.addField("Genre:", "genre");
        fb.addField("Total Quantity:", "totalQuantity");
        fb.setValue("title", book.getTitle());
        fb.setValue("author", book.getAuthor());
        fb.setValue("isbn", book.getIsbn());
        fb.setValue("genre", book.getGenre().name());
        fb.setValue("totalQuantity", String.valueOf(currentTotal));

        Panel editFields = fb.getPanel();

        Panel panel = createVerticalContent();
        panel.addComponent(details);
        panel.addComponent(editFields);
        panel.addComponent(createHintLabel("Editable for Book: title, author, isbn, genre."));
        panel.addComponent(createHintLabel("Editable for Stock: total quantity only."));
        panel.addComponent(createHintLabel("Available quantity is derived from total stock minus active loans."));
        panel.addComponent(createActionRow(
                new Button("Save", () -> saveManagedBook(book, fb, formWindow)),
                new Button("Cancel", formWindow::close)
        ));

        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }

    private void saveManagedBook(Book book, FormBuilder fb, BasicWindow formWindow) {
        try {
            String title = fb.getValue("title");
            String author = fb.getValue("author");
            String isbn = fb.getValue("isbn");
            String genreInput = fb.getValue("genre");
            int totalQuantity = parseNonNegativeInt(fb.getValue("totalQuantity"), "Total Quantity");

            if (title.isBlank() || author.isBlank() || genreInput.isBlank()) {
                MessageDialog.showMessageDialog(gui, "Validation", "Title, Author, and Genre are required.");
                return;
            }

            Genre genre;
            try {
                genre = Genre.valueOf(genreInput.toUpperCase().replace(' ', '_'));
            } catch (IllegalArgumentException ex) {
                MessageDialog.showMessageDialog(gui, "Validation", "Invalid genre. Use one of: " + allowedGenres());
                return;
            }

            BookStock stock = stockDAO.findByBookId(book.getId());
            long activeLoans = stock == null ? 0 : loanDAO.countActiveByStock(stock.getId());
            if (totalQuantity < activeLoans) {
                MessageDialog.showMessageDialog(
                    gui,
                    "Validation",
                    "Total quantity cannot be less than active loans (" + activeLoans + ")."
                );
                return;
            }

            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setGenre(genre);
            bookDAO.update(book);

            int availableQuantity = Math.max(0, totalQuantity - (int) activeLoans);
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
                    "Total Quantity must be a valid number."
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

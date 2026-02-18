package com.library.ui.screens;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.enums.Genre;
import com.library.ui.components.FormBuilder;
import com.library.ui.components.TableBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class BookManagementScreen extends AbstractScreenWindow {
    private final BookDAO bookDAO = new BookDAO();
    private final Table<String> table;

    public BookManagementScreen(com.googlecode.lanterna.gui2.MultiWindowTextGUI gui) {
        super("Book Management", gui);
        Panel content = createVerticalContent();

        this.table = TableBuilder.createEntityTable("Books", "ID", "Title", "Author", "Genre");
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
        for (Book b : books) {
            table.getTableModel().addRow(
                b.getId().toString(),
                b.getTitle(),
                b.getAuthor(),
                b.getGenre().name()
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

        Panel panel = fb.getPanel();
        panel.addComponent(createHintLabel("Genres: " + allowedGenres()));
        panel.addComponent(createActionRow(
                new Button("Save", () -> saveBook(fb, formWindow)),
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

        if (title.isBlank() || author.isBlank() || genreInput.isBlank()) {
            MessageDialog.showMessageDialog(gui, "Validation", "Title, Author, and Genre are required.");
            return;
        }

        try {
            Genre genre = Genre.valueOf(genreInput.toUpperCase().replace(' ', '_'));

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setGenre(genre);

            bookDAO.save(book);
            refreshTable();
            formWindow.close();
        } catch (IllegalArgumentException ex) {
            MessageDialog.showMessageDialog(
                    gui,
                    "Validation",
                    "Invalid genre. Use one of: " + allowedGenres()
            );
        } catch (Exception ex) {
            MessageDialog.showMessageDialog(gui, "Error", ex.getMessage());
        }
    }

    private String allowedGenres() {
        return java.util.Arrays.stream(Genre.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}

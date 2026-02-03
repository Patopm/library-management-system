package com.library.ui.screens;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import java.util.concurrent.atomic.AtomicBoolean;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.table.Table;
import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.ui.components.TableBuilder;

import java.util.List;

public class BookManagementScreen extends BasicWindow {
    private final BookDAO bookDAO = new BookDAO();
    private final MultiWindowTextGUI gui;

    public BookManagementScreen(MultiWindowTextGUI gui) {
        super("Book Management");
        this.gui = gui;
        // Inside the Screen Constructor:
        this.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onInput(Window window, KeyStroke keyStroke, AtomicBoolean deliverEvent) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    window.close();
                }
            }
        });

        Panel content = new Panel(new LinearLayout());
        
        Table<String> table = TableBuilder.createEntityTable("Books", "ID", "Title", "Author", "Genre");
        refreshTable(table);

        content.addComponent(table);
        content.addComponent(new Button("Add New Book", this::openAddBookForm));
        content.addComponent(new Button("Back", this::close));

        setComponent(content);
    }

    private void refreshTable(Table<String> table) {
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
        MessageDialog.showMessageDialog(gui, "Action", "Form integration is next step!");
    }
}
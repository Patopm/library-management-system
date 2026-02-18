package com.library.ui.screens;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.library.dao.LoanDAO;
import com.library.model.Loan;
import com.library.service.LibraryService;
import com.library.ui.components.TableBuilder;
import com.library.ui.components.FormBuilder;

import java.util.List;

public class LoanManagementScreen extends AbstractScreenWindow {
    private final LoanDAO loanDAO = new LoanDAO();
    private final LibraryService libraryService = new LibraryService();
    private final Table<String> table;

    public LoanManagementScreen(MultiWindowTextGUI gui) {
        super("Loan Operations", gui);
        Panel content = createVerticalContent();
        
        this.table = TableBuilder.createEntityTable("Active Loans", "ID", "Member", "Book", "Due Date", "Status");
        refreshTable();

        content.addComponent(table);
        content.addComponent(createActionRow(
                new Button("New Loan", this::openLoanForm),
                new Button("Return Book", this::handleReturn),
                new Button("Back", this::close)
        ));
        content.addComponent(createHintLabel("Press ESC to return to main menu."));

        setComponent(content);
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<Loan> loans = loanDAO.findAll();
        for (Loan l : loans) {
            table.getTableModel().addRow(
                l.getId().toString(),
                l.getMember().getFullName(),
                l.getBookStock().getBook().getTitle(),
                l.getDueDate().toString(),
                l.getStatus().name()
            );
        }
    }

    private void openLoanForm() {
        final BasicWindow formWindow = new BasicWindow("Create New Loan");
        formWindow.setHints(List.of(Window.Hint.CENTERED));
        FormBuilder fb = new FormBuilder();
        fb.addField("Member ID:", "memberId");
        fb.addField("Stock ID:", "stockId");

        Panel panel = fb.getPanel();
        panel.addComponent(new Button("Confirm Loan", () -> {
            try {
                Long mId = Long.parseLong(fb.getValue("memberId"));
                Long sId = Long.parseLong(fb.getValue("stockId"));
                libraryService.createLoan(mId, sId);
                refreshTable();
                formWindow.close();
            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Error", e.getMessage());
            }
        }));

        panel.addComponent(new Button("Cancel", formWindow::close));
        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }

    private void handleReturn() {
        String input = TextInputDialog.showDialog(gui, "Return Book", "Enter Loan ID:", "");
        if (input != null && !input.isEmpty()) {
            try {
                libraryService.processReturn(Long.parseLong(input));
                refreshTable();
                MessageDialog.showMessageDialog(gui, "Success", "Book returned and stock updated.");
            } catch (Exception e) {
                MessageDialog.showMessageDialog(gui, "Error", e.getMessage());
            }
        }
    }
}

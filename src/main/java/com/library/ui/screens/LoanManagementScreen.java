package com.library.ui.screens;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.library.dao.BookStockDAO;
import com.library.dao.LoanDAO;
import com.library.dao.MemberDAO;
import com.library.model.BookStock;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.enums.LoanStatus;
import com.library.service.LibraryService;
import com.library.ui.components.TableBuilder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LoanManagementScreen extends AbstractScreenWindow {
    private final LoanDAO loanDAO = new LoanDAO();
    private final BookStockDAO stockDAO = new BookStockDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final LibraryService libraryService = new LibraryService();
    private final Label summaryLabel = new Label("");
    private final Table<String> table;

    public LoanManagementScreen(MultiWindowTextGUI gui) {
        super("Loan Operations", gui);
        Panel content = createVerticalContent();

        content.addComponent(createScreenHeader(
                "Loan Operations",
                "Manage checkouts and statuses. Edit a row with ENTER to update loan status."
        ));
        content.addComponent(createVerticalGap());

        content.addComponent(summaryLabel.addStyle(SGR.BOLD));
        content.addComponent(createVerticalGap());

        this.table = TableBuilder.createEntityTable("Active Loans", "ID", "Member", "Book", "Due Date", "Status");
        this.table.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        refreshTable();
        this.table.setSelectAction(this::openEditLoanFromSelection);

        content.addComponent(table);
        content.addComponent(createVerticalGap());
        content.addComponent(createActionRow(
                new Button("New Loan", this::openLoanForm),
                new Button("Back", this::close)
        ));
        content.addComponent(createHintLabel("List controls: UP/DOWN navigate, ENTER edits selected loan, ESC closes this screen."));

        setComponent(content);
    }

    private void refreshTable() {
        table.getTableModel().clear();
        List<Loan> loans = loanDAO.findAll();
        long activeCount = 0;
        long overdueCount = 0;
        long returnedCount = 0;
        for (Loan l : loans) {
            if (l.getStatus() == LoanStatus.ACTIVE) {
                activeCount++;
                if (l.getDueDate().isBefore(LocalDate.now())) {
                    overdueCount++;
                }
            } else if (l.getStatus() == LoanStatus.RETURNED) {
                returnedCount++;
            }
            table.getTableModel().addRow(
                l.getId().toString(),
                l.getMember().getFullName(),
                l.getBookStock().getBook().getTitle(),
                l.getDueDate().toString(),
                l.getStatus().name()
            );
        }
        summaryLabel.setText(
                "Total loans: " + loans.size()
                        + "  |  Active: " + activeCount
                        + "  |  Overdue: " + overdueCount
                        + "  |  Returned: " + returnedCount
        );
    }

    private void openLoanForm() {
        List<BookStock> availableStocks = stockDAO.findAll().stream()
                .filter(stock -> stock.getBook() != null && stock.getAvailableQuantity() > 0 && stock.canBeLent())
                .sorted(Comparator.comparing(stock -> stock.getBook().getTitle(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (availableStocks.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Create Loan", "No books with available stock.");
            return;
        }

        BookStock selectedStock = selectStockForLoan(availableStocks);
        if (selectedStock == null) {
            return;
        }

        List<Member> members = memberDAO.findAll().stream()
                .sorted(Comparator.comparing(Member::getFullName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        if (members.isEmpty()) {
            MessageDialog.showMessageDialog(gui, "Create Loan", "No members found. Register a member first.");
            return;
        }

        Member selectedMember = selectMemberForLoan(members);
        if (selectedMember == null) {
            return;
        }

        openConfirmLoanCreation(selectedMember, selectedStock);
    }

    private BookStock selectStockForLoan(List<BookStock> availableStocks) {
        final BasicWindow stockWindow = new BasicWindow("Step 1/3 - Select Book");
        stockWindow.setHints(List.of(Window.Hint.CENTERED));
        final BookStock[] selection = new BookStock[1];

        Panel panel = createVerticalContent();
        panel.addComponent(createHintLabel("Choose a book with ENTER. Use Cancel to exit."));

        Table<String> stockTable = TableBuilder.createEntityTable(
                "Available Stock",
                "Stock ID",
                "Title",
                "Author",
                "Available"
        );
        stockTable.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        for (BookStock stock : availableStocks) {
            stockTable.getTableModel().addRow(
                    String.valueOf(stock.getId()),
                    stock.getBook().getTitle(),
                    stock.getBook().getAuthor(),
                    String.valueOf(stock.getAvailableQuantity())
            );
        }

        Runnable selectAndClose = () -> {
            int selectedRow = stockTable.getSelectedRow();
            if (selectedRow < 0 || selectedRow >= availableStocks.size()) {
                MessageDialog.showMessageDialog(gui, "Select Book", "Select a book row first.");
                return;
            }
            selection[0] = availableStocks.get(selectedRow);
            stockWindow.close();
        };
        stockTable.setSelectAction(selectAndClose);

        panel.addComponent(stockTable);
        panel.addComponent(createActionRow(
                new Button("Cancel", stockWindow::close)
        ));

        stockWindow.setComponent(panel);
        gui.addWindowAndWait(stockWindow);
        return selection[0];
    }

    private Member selectMemberForLoan(List<Member> members) {
        final BasicWindow memberWindow = new BasicWindow("Step 2/3 - Select Member");
        memberWindow.setHints(List.of(Window.Hint.CENTERED));
        final Member[] selection = new Member[1];

        Panel panel = createVerticalContent();
        panel.addComponent(createHintLabel("Choose a member with ENTER. Use Cancel to exit."));

        Table<String> memberTable = TableBuilder.createEntityTable(
                "Members",
                "Member ID",
                "Full Name",
                "Email"
        );
        memberTable.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        for (Member member : members) {
            memberTable.getTableModel().addRow(
                    String.valueOf(member.getId()),
                    member.getFullName(),
                    member.getEmail()
            );
        }

        Runnable selectAndClose = () -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow < 0 || selectedRow >= members.size()) {
                MessageDialog.showMessageDialog(gui, "Select Member", "Select a member row first.");
                return;
            }
            selection[0] = members.get(selectedRow);
            memberWindow.close();
        };
        memberTable.setSelectAction(selectAndClose);

        panel.addComponent(memberTable);
        panel.addComponent(createActionRow(
                new Button("Cancel", memberWindow::close)
        ));

        memberWindow.setComponent(panel);
        gui.addWindowAndWait(memberWindow);
        return selection[0];
    }

    private void openConfirmLoanCreation(Member member, BookStock stock) {
        final BasicWindow confirmWindow = new BasicWindow("Step 3/3 - Confirm Loan");
        confirmWindow.setHints(List.of(Window.Hint.CENTERED));

        if (stock.getStrategy() == null) {
            stock.canBeLent();
        }
        LocalDate dueDate = LocalDate.now().plusDays(stock.getStrategy().getMaxLoanDays());

        Panel panel = createVerticalContent();
        panel.addComponent(createHintLabel("Review details and confirm loan creation."));
        panel.addComponent(createHintLabel("Member: " + member.getFullName() + " (ID: " + member.getId() + ")"));
        panel.addComponent(createHintLabel("Book: " + stock.getBook().getTitle() + " (Stock ID: " + stock.getId() + ")"));
        panel.addComponent(createHintLabel("Available now: " + stock.getAvailableQuantity()));
        panel.addComponent(createHintLabel("Due date (calculated): " + dueDate));
        panel.addComponent(createActionRow(
                new Button("Create Loan", () -> {
                    try {
                        libraryService.createLoan(member.getId(), stock.getId());
                        refreshTable();
                        confirmWindow.close();
                        MessageDialog.showMessageDialog(gui, "Loan Created", "Loan created successfully.");
                    } catch (Exception ex) {
                        MessageDialog.showMessageDialog(gui, "Error", ex.getMessage());
                    }
                }),
                new Button("Cancel", confirmWindow::close)
        ));

        confirmWindow.setComponent(panel);
        gui.addWindowAndWait(confirmWindow);
    }

    private void openEditLoanFromSelection() {
        if (table.getTableModel().getRowCount() == 0) {
            MessageDialog.showMessageDialog(gui, "Loan Operations", "No loans available to edit.");
            return;
        }

        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= table.getTableModel().getRowCount()) {
            MessageDialog.showMessageDialog(gui, "Loan Operations", "Select a loan row first.");
            return;
        }

        String loanIdCell = table.getTableModel().getRow(selectedRow).get(0);
        try {
            Long loanId = Long.parseLong(loanIdCell);
            Loan loan = loanDAO.findById(loanId);
            if (loan == null) {
                MessageDialog.showMessageDialog(gui, "Loan Operations", "Selected loan no longer exists.");
                return;
            }
            openEditLoanForm(loan);
        } catch (NumberFormatException ex) {
            MessageDialog.showMessageDialog(gui, "Loan Operations", "Selected row has an invalid Loan ID.");
        }
    }

    private void openEditLoanForm(Loan loan) {
        final BasicWindow formWindow = new BasicWindow("Edit Loan");
        formWindow.setHints(List.of(Window.Hint.CENTERED));
        Panel panel = createVerticalContent();
        panel.addComponent(createHintLabel("Loan ID: " + loan.getId()));
        panel.addComponent(createHintLabel("Current Status: " + loan.getStatus().name()));
        panel.addComponent(createHintLabel("Member: " + loan.getMember().getFullName()));
        panel.addComponent(createHintLabel("Book: " + loan.getBookStock().getBook().getTitle()));
        panel.addComponent(createHintLabel("Due Date: " + loan.getDueDate()));
        panel.addComponent(createActionRow(
                new Button("Set as Active", () -> updateLoanStatus(loan, LoanStatus.ACTIVE, formWindow)),
                new Button("Set as Returned", () -> updateLoanStatus(loan, LoanStatus.RETURNED, formWindow)),
                new Button("Set as Overdue", () -> updateLoanStatus(loan, LoanStatus.OVERDUE, formWindow)),
                new Button("Cancel", formWindow::close)
        ));

        formWindow.setComponent(panel);
        gui.addWindowAndWait(formWindow);
    }

    private void updateLoanStatus(Loan loan, LoanStatus status, BasicWindow formWindow) {
        try {
            loan.setStatus(status);
            loanDAO.update(loan);
            refreshTable();
            formWindow.close();
        } catch (Exception ex) {
            MessageDialog.showMessageDialog(gui, "Error", ex.getMessage());
        }
    }
}

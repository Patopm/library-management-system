package main.java.com.library.service;

import main.java.com.library.dao.BookStockDAO;
import main.java.com.library.dao.LoanDAO;
import main.java.com.library.model.BookStock;
import main.java.com.library.model.Loan;
import main.java.com.library.util.ExportUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {
    private final LoanDAO loanDAO = new LoanDAO();
    private final BookStockDAO stockDAO = new BookStockDAO();

    public String generateInventoryReport() throws IOException {
        String fileName = "inventory_report.csv";
        List<BookStock> stocks = stockDAO.findAll();
        List<String> lines = new ArrayList<>();

        // Header
        lines.add("ID,Title,Genre,Total,Available,Policy");

        for (BookStock s : stocks) {
            String line = String.format(
                "%d,%s,%s,%d,%d,%s",
                s.getId(),
                s.getBook().getTitle(),
                s.getBook().getGenre(),
                s.getAvailableQuantity(), // Simplified for example
                s.getAvailableQuantity(),
                s.getStrategy().getStrategyName()
            );
            lines.add(line);
        }

        ExportUtil.saveToCSV(fileName, lines);
        return fileName;
    }

    public String generateLoanReport() throws IOException {
        String fileName = "loans_report.csv";
        List<Loan> loans = loanDAO.findAll();
        List<String> lines = new ArrayList<>();

        // Header
        lines.add("LoanID,Member,Book,DueDate,Status");

        for (Loan l : loans) {
            String line = String.format(
                "%d,%s,%s,%s,%s",
                l.getId(),
                l.getMember().getFullName(),
                l.getBookStock().getBook().getTitle(),
                l.getDueDate().toString(),
                l.getStatus().toString()
            );
            lines.add(line);
        }

        ExportUtil.saveToCSV(fileName, lines);
        return fileName;
    }
}
package main.java.com.library.service;

import main.java.com.library.dao.*;
import main.java.com.library.model.*;
import main.java.com.library.model.enums.LoanStatus;
import java.time.LocalDate;

public class LibraryService {
    private final MemberDAO memberDAO = new MemberDAO();
    private final LoanDAO loanDAO = new LoanDAO();
    private final BookStockDAO stockDAO = new BookStockDAO();
    private final ValidationService validator = new ValidationService();

    public void createLoan(Long memberId, Long stockId) throws Exception {
        Member member = memberDAO.findById(memberId);
        BookStock stock = stockDAO.findById(stockId);

        validator.validateLoanEligibility(member, stock);

        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookStock(stock);
        loan.setStatus(LoanStatus.ACTIVE);
        
        LocalDate now = LocalDate.now();
        loan.setDueDate(now.plusDays(stock.getStrategy().getMaxLoanDays()));

        // Transaction: Decrease stock and save loan
        stock.setAvailableQuantity(stock.getAvailableQuantity() - 1);
        stockDAO.update(stock);
        loanDAO.save(loan);
    }

    public void processReturn(Long loanId) {
        Loan loan = loanDAO.findById(loanId);
        if (loan != null && loan.getStatus() == LoanStatus.ACTIVE) {
            loan.setStatus(LoanStatus.RETURNED);
            
            BookStock stock = loan.getBookStock();
            stock.setAvailableQuantity(stock.getAvailableQuantity() + 1);
            
            stockDAO.update(stock);
            loanDAO.update(loan);
        }
    }
}
package com.library.integration.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.library.dao.BookDAO;
import com.library.dao.BookStockDAO;
import com.library.dao.LoanDAO;
import com.library.dao.MemberDAO;
import com.library.integration.IntegrationTestSupport;
import com.library.model.Book;
import com.library.model.BookStock;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.model.enums.Genre;
import com.library.model.enums.LoanStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoanDAOIntegrationTest extends IntegrationTestSupport {
    private final MemberDAO memberDAO = new MemberDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final BookStockDAO stockDAO = new BookStockDAO();
    private final LoanDAO loanDAO = new LoanDAO();

    @Test
    void findActiveByMemberReturnsOnlyActiveLoansForThatMember() {
        Member member1 = new Member();
        member1.setFullName("Alice Reader");
        member1.setEmail("alice@example.com");
        memberDAO.save(member1);

        Member member2 = new Member();
        member2.setFullName("Bob Reader");
        member2.setEmail("bob@example.com");
        memberDAO.save(member2);

        Book book = new Book();
        book.setTitle("Dune");
        book.setAuthor("Frank Herbert");
        book.setIsbn("978-0441172719");
        book.setGenre(Genre.SCIENCE_FICTION);
        bookDAO.save(book);

        BookStock stock = new BookStock();
        stock.setBook(book);
        stock.setTotalQuantity(10);
        stock.setAvailableQuantity(10);
        stockDAO.save(stock);

        Loan activeLoan = new Loan();
        activeLoan.setMember(member1);
        activeLoan.setBookStock(stock);
        activeLoan.setDueDate(LocalDate.now().plusDays(7));
        activeLoan.setStatus(LoanStatus.ACTIVE);
        loanDAO.save(activeLoan);

        Loan returnedLoan = new Loan();
        returnedLoan.setMember(member1);
        returnedLoan.setBookStock(stock);
        returnedLoan.setDueDate(LocalDate.now().plusDays(7));
        returnedLoan.setStatus(LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        Loan otherMemberLoan = new Loan();
        otherMemberLoan.setMember(member2);
        otherMemberLoan.setBookStock(stock);
        otherMemberLoan.setDueDate(LocalDate.now().plusDays(7));
        otherMemberLoan.setStatus(LoanStatus.ACTIVE);
        loanDAO.save(otherMemberLoan);

        List<Loan> activeForMember1 = loanDAO.findActiveByMember(member1.getId());

        assertEquals(1, activeForMember1.size());
        assertEquals(activeLoan.getId(), activeForMember1.get(0).getId());
    }
}

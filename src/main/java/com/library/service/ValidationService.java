package com.library.service;

import com.library.dao.LoanDAO;
import com.library.model.BookStock;
import com.library.model.Member;

public class ValidationService {
    private final LoanDAO loanDAO = new LoanDAO();

    public void validateLoanEligibility(Member member, BookStock stock) throws Exception {
        if (loanDAO.findActiveByMember(member.getId()).size() >= 5) {
            throw new Exception("Member has reached the maximum limit of 5 active loans.");
        }
        if (!stock.canBeLent()) {
            throw new Exception("Stock strategy prevents lending this item (low inventory).");
        }
    }

    public void validateMemberDeletion(Long memberId) throws Exception {
        if (!loanDAO.findActiveByMember(memberId).isEmpty()) {
            throw new Exception("Cannot delete member with active loans.");
        }
    }
}
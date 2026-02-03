package com.library.dao;

import com.library.model.Loan;
import com.library.model.enums.LoanStatus;
import com.library.config.HibernateConfig;
import org.hibernate.Session;
import java.util.List;

public class LoanDAO extends GenericDAO<Loan> {
    public LoanDAO() {
        super(Loan.class);
    }

    public List<Loan> findActiveByMember(Long memberId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery(
                "from Loan where member.id = :mId and status = :status", Loan.class)
                .setParameter("mId", memberId)
                .setParameter("status", LoanStatus.ACTIVE)
                .list();
        }
    }
}
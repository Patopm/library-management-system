package main.java.com.library.dao;

import main.java.com.library.model.Member;

public class MemberDAO extends GenericDAO<Member> {
    public MemberDAO() {
        super(Member.class);
    }
}
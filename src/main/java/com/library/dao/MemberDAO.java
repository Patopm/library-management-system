package com.library.dao;

import com.library.model.Member;

public class MemberDAO extends GenericDAO<Member> {
    public MemberDAO() {
        super(Member.class);
    }
}
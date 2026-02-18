package com.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void newMemberHasDefaultJoinDate() {
        Member member = new Member();
        assertNotNull(member.getJoinDate());
    }

    @Test
    void settersAndGettersWorkForCoreFields() {
        Member member = new Member();
        LocalDate joinDate = LocalDate.of(2026, 2, 18);

        member.setFullName("Test User");
        member.setEmail("test@example.com");
        member.setJoinDate(joinDate);

        assertEquals("Test User", member.getFullName());
        assertEquals("test@example.com", member.getEmail());
        assertEquals(joinDate, member.getJoinDate());
    }
}

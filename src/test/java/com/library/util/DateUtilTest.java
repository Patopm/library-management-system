package com.library.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;

class DateUtilTest {

    @Test
    void formatReturnsFormattedDate() {
        assertEquals("2026-02-18", DateUtil.format(LocalDate.of(2026, 2, 18)));
    }

    @Test
    void formatReturnsNAForNullDate() {
        assertEquals("N/A", DateUtil.format(null));
    }

    @Test
    void parseReturnsLocalDate() {
        assertEquals(LocalDate.of(2026, 2, 18), DateUtil.parse("2026-02-18"));
    }

    @Test
    void parseThrowsForInvalidInput() {
        assertThrows(DateTimeParseException.class, () -> DateUtil.parse("18-02-2026"));
    }
}

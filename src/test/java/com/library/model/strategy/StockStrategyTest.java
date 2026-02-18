package com.library.model.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StockStrategyTest {

    @Test
    void fictionStrategyRules() {
        FictionStockStrategy strategy = new FictionStockStrategy();

        assertFalse(strategy.validateLoan(0));
        assertTrue(strategy.validateLoan(1));
        assertEquals(14, strategy.getMaxLoanDays());
        assertEquals("Standard Fiction Policy", strategy.getStrategyName());
    }

    @Test
    void sciFiStrategyRules() {
        SciFiStockStrategy strategy = new SciFiStockStrategy();

        assertFalse(strategy.validateLoan(0));
        assertFalse(strategy.validateLoan(1));
        assertTrue(strategy.validateLoan(2));
        assertEquals(7, strategy.getMaxLoanDays());
        assertEquals("Sci-Fi Restricted Policy", strategy.getStrategyName());
    }

    @Test
    void mysteryStrategyRules() {
        MysteryStockStrategy strategy = new MysteryStockStrategy();

        assertFalse(strategy.validateLoan(5));
        assertTrue(strategy.validateLoan(6));
        assertEquals(10, strategy.getMaxLoanDays());
        assertEquals("Standard Mystery Policy", strategy.getStrategyName());
    }
}

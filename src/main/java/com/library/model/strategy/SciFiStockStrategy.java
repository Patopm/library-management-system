package com.library.model.strategy;

public class SciFiStockStrategy implements BookStockStrategy {
    @Override
    public boolean validateLoan(int available) {
        return available > 1; // Always keep at least 1 copy for in-library use
    }

    @Override
    public int getMaxLoanDays() {
        return 7;
    }

    @Override
    public String getStrategyName() {
        return "Sci-Fi Restricted Policy";
    }
}
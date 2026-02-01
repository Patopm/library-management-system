package main.java.com.library.model.strategy;

public class MysteryStockStrategy implements BookStockStrategy {
    @Override
    public boolean validateLoan(int available) {
        return available > 5; // Always keep at least 5 copies for in-library reading
    }

    @Override
    public int getMaxLoanDays() {
        return 10;
    }

    @Override
    public String getStrategyName() {
        return "Standard Mystery Policy";
    }
}
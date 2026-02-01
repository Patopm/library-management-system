package main.java.com.library.model.strategy;

public class FictionStockStrategy implements BookStockStrategy {
    @Override
    public boolean validateLoan(int available) {
        return available > 0;
    }

    @Override
    public int getMaxLoanDays() {
        return 14;
    }

    @Override
    public String getStrategyName() {
        return "Standard Fiction Policy";
    }
}
package main.java.com.library.model.strategy;

public interface BookStockStrategy {
    boolean validateLoan(int availableQuantity);

    int getMaxLoanDays();

    String getStrategyName();
}

package service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import model.Expense;
import repository.ExpenseRepository;
import repository.ExpenseRepositoryImpl;

public class ExpenseService {
    private static final double MONTHLY_EXPENSE_LIMIT = 5000.0;
    private ExpenseRepository repository;

    public ExpenseService() {
        this.repository = new ExpenseRepositoryImpl();
    }

    public void registerExpense(Expense expense) {
        if (expense.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto del gasto debe ser mayor que cero");
        }

        LocalDate expenseMonth = expense.getExpenseDate();
        double totalMonthlyExpenses = repository.getTotalMonthlyExpenses(expenseMonth);
        
        if (totalMonthlyExpenses + expense.getAmount() > MONTHLY_EXPENSE_LIMIT) {
            throw new IllegalStateException("El gasto excede el límite mensual de S/5000");
        }

        repository.save(expense);
    }

    public List<Expense> listAllExpenses() {
        return repository.findAll();
    }

    public List<Expense> listExpensesByCategory(Expense.ExpenseCategory category) {
        return repository.findByCategory(category);
    }

    public void updateExpense(Expense expense) {
        repository.update(expense);
    }

    public void deleteExpense(int id) {
        repository.delete(id);
    }

    public ExpenseSummary getExpenseSummary(LocalDate month) {
        List<Expense> monthExpenses = repository.findAll().stream()
            .filter(e -> e.getExpenseDate().getYear() == month.getYear() &&
                         e.getExpenseDate().getMonthValue() == month.getMonthValue())
            .collect(Collectors.toList());

        double totalAmount = monthExpenses.stream()
            .mapToDouble(Expense::getAmount)
            .sum();

        var categoryCounts = monthExpenses.stream()
            .collect(Collectors.groupingBy(
                Expense::getCategory, 
                Collectors.counting()
            ));

        return new ExpenseSummary(totalAmount, categoryCounts);
    }

    public static class ExpenseSummary {
        private double totalAmount;
        private java.util.Map<Expense.ExpenseCategory, Long> categoryCounts;

        public ExpenseSummary(double totalAmount, java.util.Map<Expense.ExpenseCategory, Long> categoryCounts) {
            this.totalAmount = totalAmount;
            this.categoryCounts = categoryCounts;
        }

        public double getTotalAmount() { return totalAmount; }
        public java.util.Map<Expense.ExpenseCategory, Long> getCategoryCounts() { return categoryCounts; }
    }
}

package repository;


import java.time.LocalDate;
import java.util.List;
import model.Expense;

public interface ExpenseRepository {
    void save(Expense expense);
    void update(Expense expense);
    void delete(int id);
    List<Expense> findAll();
    List<Expense> findByCategory(Expense.ExpenseCategory category);
    double getTotalMonthlyExpenses(LocalDate month);
}
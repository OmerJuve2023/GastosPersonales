package model;

import core.Model;
import core.View;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Expense implements Model {

    public static boolean ExpenseCategory(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Integer id;
    private String description;
    private ExpenseCategory category;
    private double amount;
    private LocalDate expenseDate;

    private List<View> observers = new ArrayList<>();

    public enum ExpenseCategory {
        ALIMENTACION, TRANSPORTE, ENTRETENIMIENTO, SALUD, OTROS
    }

    private Expense() {
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    @Override
    public void attach(View view) {
        if (!observers.contains(view)) {
            observers.add(view);
        }
    }

    @Override
    public void detach(View view) {
        observers.remove(view);
    }

    @Override
    public void notifyViews() {
        for (View observer : observers) {
            observer.update(this, this);
        }
    }

    public static class Builder {

        private Expense expense;

        public Builder() {
            expense = new Expense();
        }

        public Builder id(Integer id) {
            expense.id = id;
            return this;
        }

        public Builder description(String description) {
            expense.description = description;
            return this;
        }

        public Builder category(ExpenseCategory category) {
            expense.category = category;
            return this;
        }

        public Builder amount(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor que cero");
            }
            expense.amount = amount;
            return this;
        }

        public Builder expenseDate(LocalDate expenseDate) {
            expense.expenseDate = expenseDate;
            return this;
        }

        public Expense build() {
            return expense;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Expense expense = (Expense) o;
        return Double.compare(expense.amount, amount) == 0
                && Objects.equals(id, expense.id)
                && Objects.equals(description, expense.description)
                && category == expense.category
                && Objects.equals(expenseDate, expense.expenseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, category, amount, expenseDate);
    }

    @Override
    public String toString() {
        return "Expense{"
                + "id=" + id
                + ", description='" + description + '\''
                + ", category=" + category
                + ", amount=" + amount
                + ", expenseDate=" + expenseDate
                + '}';
    }
    private Map<String, Object> customAttributes = new HashMap<>();

    public void setCustomAttribute(String key, Object value) {
        customAttributes.put(key, value);
    }

    public Object getCustomAttribute(String key) {
        return customAttributes.get(key);
    }
}


package repository;

import config.Mysql;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Expense;

public class ExpenseRepositoryImpl implements ExpenseRepository {
    private Mysql mysqlConnection;

    public ExpenseRepositoryImpl() {
        this.mysqlConnection = new Mysql();
    }

    @Override
    public void save(Expense expense) {
        String sql = "INSERT INTO gastos (descripcion, categoria, monto, fecha_gasto) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = mysqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, expense.getDescription());
            pstmt.setString(2, expense.getCategory().name());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Expense expense) {
        String sql = "UPDATE gastos SET descripcion = ?, categoria = ?, monto = ?, fecha_gasto = ? WHERE id = ?";
        
        try (Connection conn = mysqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, expense.getDescription());
            pstmt.setString(2, expense.getCategory().name());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
            pstmt.setInt(5, expense.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM gastos WHERE id = ?";
        
        try (Connection conn = mysqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM gastos";
        
        try (Connection conn = mysqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                expenses.add(new Expense.Builder()
                    .id(rs.getInt("id"))
                    .description(rs.getString("descripcion"))
                    .category(Expense.ExpenseCategory.valueOf(rs.getString("categoria")))
                    .amount(rs.getDouble("monto"))
                    .expenseDate(rs.getDate("fecha_gasto").toLocalDate())
                    .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return expenses;
    }

    @Override
    public List<Expense> findByCategory(Expense.ExpenseCategory category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM gastos WHERE categoria = ?";
        
        try (Connection conn = mysqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(new Expense.Builder()
                        .id(rs.getInt("id"))
                        .description(rs.getString("descripcion"))
                        .category(Expense.ExpenseCategory.valueOf(rs.getString("categoria")))
                        .amount(rs.getDouble("monto"))
                        .expenseDate(rs.getDate("fecha_gasto").toLocalDate())
                        .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return expenses;
    }

    @Override
    public double getTotalMonthlyExpenses(LocalDate month) {
        String sql = "SELECT SUM(monto) AS total FROM gastos WHERE YEAR(fecha_gasto) = ? AND MONTH(fecha_gasto) = ?";
        
        try (Connection conn = mysqlConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month.getYear());
            pstmt.setInt(2, month.getMonthValue());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
}
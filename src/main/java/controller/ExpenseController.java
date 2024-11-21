package controller;
import core.Controller;
import java.time.LocalDate;
import java.util.List;
import model.Expense;
import service.ExpenseService;
import view.ExpenseView;

public class ExpenseController extends Controller {
    private ExpenseService expenseService;
    private Expense expenseModel;
    private ExpenseView expenseView;

    public ExpenseController() {
        this.expenseService = new ExpenseService();
        this.expenseModel = createInitialExpenseModel();
        this.expenseView = new ExpenseView(this, expenseModel);
    }

    private Expense createInitialExpenseModel() {
        Expense expense = new Expense.Builder()
                .description("Initial Model")
                .category(Expense.ExpenseCategory.OTROS)
                .amount(0.01)
                .expenseDate(LocalDate.now())
                .build();
        return expense;
    }

    @Override
    public void run() {
        mainFrame.setContentPane(expenseView);
        mainFrame.setTitle("Sistema de Gestión de Gastos");
        mainFrame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setVisible(true);

        refreshExpenseList();
    }

    public void registerExpense(String description,
            Expense.ExpenseCategory category,
            double amount,
            LocalDate expenseDate) {
        try {
            Expense expense = new Expense.Builder()
                    .description(description)
                    .category(category)
                    .amount(amount)
                    .expenseDate(expenseDate)
                    .build();
            expenseService.registerExpense(expense);
            refreshExpenseList();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            expenseView.showError(ex.getMessage());
        }
    }

    public void refreshExpenseList() {
        List<Expense> expenses = expenseService.listAllExpenses();
        ExpenseService.ExpenseSummary summary = expenseService.getExpenseSummary(LocalDate.now());
        
        updateExpenseModel(expenses, summary);
    }

    private void updateExpenseModel(List<Expense> expenses, ExpenseService.ExpenseSummary summary) {
        Expense expenseModel = new Expense.Builder()
                .description("Expense List Model")
                .category(Expense.ExpenseCategory.OTROS)
                .amount(summary.getTotalAmount())
                .expenseDate(LocalDate.now())
                .build();

        expenseModel.attach(expenseView);

        this.expenseModel = expenseModel;

        this.expenseModel.setCustomAttribute("expenses", expenses);
        this.expenseModel.setCustomAttribute("summary", summary);

        this.expenseModel.notifyViews();
    }

    public List<Expense> listAllExpenses() {
        return expenseService.listAllExpenses();
    }

    public List<Expense> listExpensesByCategory(Expense.ExpenseCategory category) {
        return expenseService.listExpensesByCategory(category);
    }

    public void updateExpense(int id,
            String description,
            Expense.ExpenseCategory category,
            double amount,
            LocalDate expenseDate) {
        Expense expense = new Expense.Builder()
                .id(id)
                .description(description)
                .category(category)
                .amount(amount)
                .expenseDate(expenseDate)
                .build();
        expenseService.updateExpense(expense);
        refreshExpenseList();
    }

    public void deleteExpense(int id) {
        expenseService.deleteExpense(id);
        refreshExpenseList();
    }

    public double getTotalMonthlyExpenses(LocalDate month) {
        return expenseService.getExpenseSummary(month).getTotalAmount();
    }
}
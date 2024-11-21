package view;

import controller.ExpenseController;
import core.Model;
import core.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import model.Expense;
import service.ExpenseService;

public class ExpenseView extends JPanel implements View {

    private ExpenseController controller;
    private Expense model;

    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JLabel totalExpenseLabel;
    private JTextArea summaryArea;

    private JTextField descriptionField;
    private JComboBox<Expense.ExpenseCategory> categoryCombo;
    private JTextField amountField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTextField idField;

    public ExpenseView(ExpenseController controller, Expense model) {
        this.controller = controller;
        this.model = model;

        model.attach(this);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Description", "Category", "Amount", "Date"}, 0);
        expenseTable = new JTable(tableModel);

        expenseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && expenseTable.getSelectedRow() != -1) {
                int selectedRow = expenseTable.getSelectedRow();
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                descriptionField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                categoryCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
                amountField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(expenseTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        panel.add(new JLabel("ID:"));
        idField = new JTextField(10);
        idField.setEditable(false);
        panel.add(idField);

        panel.add(new JLabel("Description:"));
        descriptionField = new JTextField(15);
        panel.add(descriptionField);

        panel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(Expense.ExpenseCategory.values());
        panel.add(categoryCombo);

        panel.add(new JLabel("Amount:"));
        amountField = new JTextField(10);
        panel.add(amountField);

        JPanel buttonsPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Add Expense");
        addButton.addActionListener(e -> {
            try {
                String description = descriptionField.getText();
                Expense.ExpenseCategory category = (Expense.ExpenseCategory) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                controller.registerExpense(description, category, amount, LocalDate.now());

                clearInputFields();
            } catch (NumberFormatException ex) {
                showError("Invalid amount");
            }
        });
        buttonsPanel.add(addButton);

        updateButton = new JButton("Update Expense");
        updateButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String description = descriptionField.getText();
                Expense.ExpenseCategory category = (Expense.ExpenseCategory) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                controller.updateExpense(id, description, category, amount, LocalDate.now());

                clearInputFields();
            } catch (NumberFormatException ex) {
                showError("Invalid input");
            }
        });
        buttonsPanel.add(updateButton);

        deleteButton = new JButton("Delete Expense");
        deleteButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                controller.deleteExpense(id);

                clearInputFields();
            } catch (NumberFormatException ex) {
                showError("Invalid ID");
            }
        });
        buttonsPanel.add(deleteButton);

        panel.add(buttonsPanel);

        return panel;
    }

    private void clearInputFields() {
        idField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        categoryCombo.setSelectedIndex(0);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        totalExpenseLabel = new JLabel("Total Gasto: $0.00");
        panel.add(totalExpenseLabel, BorderLayout.NORTH);

        summaryArea = new JTextArea(5, 40);
        summaryArea.setEditable(false);
        panel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        return panel;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void update(Model model, Object data) {
        if (model instanceof Expense) {
            Expense expense = (Expense) model;

            List<Expense> expenses = (List<Expense>) expense.getCustomAttribute("expenses");
            ExpenseService.ExpenseSummary summary
                    = (ExpenseService.ExpenseSummary) expense.getCustomAttribute("summary");

            tableModel.setRowCount(0);
            if (expenses != null) {
                for (Expense exp : expenses) {
                    tableModel.addRow(new Object[]{
                        exp.getId(),
                        exp.getDescription(),
                        exp.getCategory(),
                        exp.getAmount(),
                        exp.getExpenseDate()
                    });
                }
            }

            if (summary != null) {
                totalExpenseLabel.setText(String.format("Total gastos: $%.2f", summary.getTotalAmount()));

                StringBuilder summaryText = new StringBuilder("Categoria:\n");
                summary.getCategoryCounts().forEach((category, count) -> {
                    summaryText.append(String.format("%s: %d gastos\n", category, count));
                });
                summaryArea.setText(summaryText.toString());
            }
        }
    }
}

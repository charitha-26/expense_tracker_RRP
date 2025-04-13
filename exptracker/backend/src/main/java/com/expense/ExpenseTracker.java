package com.expense;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ExpenseTracker extends JFrame {

    private final JTextField expenseField;
    private final JTextField dateField;
    private final JTextField depositField;
    private final JComboBox<String> categoryBox;
    private final JTable table;
    private final DefaultTableModel model;
    private final JLabel balanceLabel;
    private final JLabel totalLabel;
    private double bankBalance = 10000.0;

    public ExpenseTracker() {
        setTitle("💰 Expense Tracker");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with Royal Purple to Indigo gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color purple = new Color(102, 51, 153); // Royal Purple
                Color indigo = new Color(75, 0, 130);   // Indigo
                GradientPaint gradient = new GradientPaint(0, 0, purple, getWidth(), getHeight(), indigo);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(null, "➕ Add Transaction", 0, 0, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        inputPanel.setOpaque(false);

        expenseField = new JTextField();
        dateField = new JTextField();
        depositField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Food", "Transport", "Shopping", "Bills", "Others"});

        styleTextField(expenseField);
        styleTextField(dateField);
        styleTextField(depositField);
        styleComboBox(categoryBox);

        inputPanel.add(createStyledLabel("💲 Expense Amount:"));
        inputPanel.add(createStyledLabel("📅 Date (YYYY-MM-DD):"));
        inputPanel.add(createStyledLabel("📌 Category:"));
        inputPanel.add(expenseField);
        inputPanel.add(dateField);
        inputPanel.add(categoryBox);
        inputPanel.add(createStyledLabel("💰 Deposit Money:"));
        inputPanel.add(depositField);
        inputPanel.add(new JLabel("")); // Filler

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton addExpenseButton = createStyledButton("➖ Add Expense", Color.RED);
        JButton addDepositButton = createStyledButton("➕ Add Deposit", Color.GREEN.darker());
        JButton clearButton = createStyledButton("🗑 Clear All", new Color(255, 140, 0));
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(addDepositButton);
        buttonPanel.add(clearButton);

        // Table
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Type", "Amount", "Category", "Date"});
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(50, 0, 100));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Footer
        balanceLabel = new JLabel("Bank Balance: ₹" + bankBalance, SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        balanceLabel.setForeground(Color.CYAN);

        totalLabel = new JLabel("Total Expenses: ₹0.0", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setForeground(Color.YELLOW);

        JPanel footerPanel = new JPanel(new GridLayout(1, 2));
        footerPanel.add(balanceLabel);
        footerPanel.add(totalLabel);
        footerPanel.setOpaque(false);

        // Layouting panels
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(footerPanel, BorderLayout.WEST);

        add(mainPanel);

        // Action Listeners
        addExpenseButton.addActionListener(e -> addExpense());
        addDepositButton.addActionListener(e -> addDeposit());
        clearButton.addActionListener(e -> clearTransactions());
    }

    private void addExpense() {
        String amountText = expenseField.getText();
        String date = dateField.getText();
        String category = (String) categoryBox.getSelectedItem();

        if (amountText.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Please enter all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount > bankBalance) {
                JOptionPane.showMessageDialog(this, "⚠ Insufficient Balance!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            bankBalance -= amount;
            model.addRow(new Object[]{"Expense", amount, category, date});
            updateBalanceAndTotal();

            expenseField.setText("");
            dateField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠ Invalid amount entered!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDeposit() {
        String depositText = depositField.getText();

        if (depositText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Please enter deposit amount!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(depositText);
            bankBalance += amount;
            model.addRow(new Object[]{"Deposit", amount, "-", "-"});
            updateBalanceAndTotal();

            depositField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠ Invalid deposit amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTransactions() {
        model.setRowCount(0);
        bankBalance = 10000.0;
        updateBalanceAndTotal();
    }

    private void updateBalanceAndTotal() {
        double totalExpenses = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if ("Expense".equals(model.getValueAt(i, 0))) {
                totalExpenses += Double.parseDouble(model.getValueAt(i, 1).toString());
            }
        }
        totalLabel.setText("Total Expenses: ₹" + totalExpenses);
        balanceLabel.setText("Bank Balance: ₹" + bankBalance);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.BOLD, 14));
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        field.setBackground(Color.WHITE);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.BOLD, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTracker().setVisible(true));
    }
}
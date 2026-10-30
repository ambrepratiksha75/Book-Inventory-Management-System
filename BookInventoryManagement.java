
package BookInventory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class BookInventory extends JFrame {

    private JTextField idField, titleField, authorField;
    private JTextField priceField, quantityField, searchField;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public BookInventory() {
        createGUI();
    }

    private void createGUI() {
        setTitle("Book Inventory Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel(
            "BOOK INVENTORY MANAGEMENT SYSTEM",
            JLabel.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 23));
        main.add(heading, BorderLayout.NORTH);

        // Book entry form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));

        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();

        form.add(new JLabel("Book ID:"));
        form.add(idField);

        form.add(new JLabel("Book Name:"));
        form.add(titleField);

        form.add(new JLabel("Author Name:"));
        form.add(authorField);

        form.add(new JLabel("Price:"));
        form.add(priceField);

        form.add(new JLabel("Quantity:"));
        form.add(quantityField);

        JButton addBtn = new JButton("Add Book");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewBtn = new JButton("View All");
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(viewBtn);
        buttons.add(searchBtn);
        buttons.add(clearBtn);

        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.add(form, BorderLayout.CENTER);
        formPanel.add(buttons, BorderLayout.SOUTH);

        // Search panel
        searchField = new JTextField(25);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Book Name or ID:"));
        searchPanel.add(searchField);

        // Table
        model = new DefaultTableModel(
            new String[]{"ID", "Book Name", "Author", "Price", "Quantity"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(formPanel, BorderLayout.NORTH);
        center.add(tablePanel, BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);
        setContentPane(main);

        // Button actions
        addBtn.addActionListener(e -> addBook());
        updateBtn.addActionListener(e -> updateBook());
        deleteBtn.addActionListener(e -> deleteBook());
        viewBtn.addActionListener(e -> viewAllBooks());
        searchBtn.addActionListener(e -> searchBook());
        clearBtn.addActionListener(e -> clearFields());

        searchField.addActionListener(e -> searchBook());

        // Fill form when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() &&
                table.getSelectedRow() >= 0) {

                int row = table.convertRowIndexToModel(
                    table.getSelectedRow()
                );

                idField.setText(model.getValueAt(row, 0).toString());
                titleField.setText(model.getValueAt(row, 1).toString());
                authorField.setText(model.getValueAt(row, 2).toString());
                priceField.setText(model.getValueAt(row, 3).toString());
                quantityField.setText(model.getValueAt(row, 4).toString());
            }
        });

        setVisible(true);
    }

    // Validate form
    private boolean validateFields() {
        if (idField.getText().trim().isEmpty()
                || titleField.getText().trim().isEmpty()
                || authorField.getText().trim().isEmpty()
                || priceField.getText().trim().isEmpty()
                || quantityField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return false;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (id <= 0 || price < 0 || quantity < 0) {
                JOptionPane.showMessageDialog(
                    this, "ID must be positive. Price and quantity cannot be negative."
                );
                return false;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Enter valid numbers for ID, price and quantity."
            );
            return false;
        }

        return true;
    }

    // Add book
    private void addBook() {
        if (!validateFields()) return;

        int id = Integer.parseInt(idField.getText().trim());

        for (int i = 0; i < model.getRowCount(); i++) {
            int existingId = Integer.parseInt(
                model.getValueAt(i, 0).toString()
            );

            if (existingId == id) {
                JOptionPane.showMessageDialog(
                    this, "This Book ID already exists."
                );
                return;
            }
        }

        model.addRow(new Object[]{
            id,
            titleField.getText().trim(),
            authorField.getText().trim(),
            Double.parseDouble(priceField.getText().trim()),
            Integer.parseInt(quantityField.getText().trim())
        });

        JOptionPane.showMessageDialog(this, "Book added successfully!");
        clearFields();
    }

    // Search by book name or ID
    private void searchBook() {
        String text = searchField.getText().trim();

        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(
            RowFilter.regexFilter(
                "(?i)" + java.util.regex.Pattern.quote(text), 0, 1
            )
        );

        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No matching books found.");
        }
    }

    // Update selected book
    private void updateBook() {
        int selected = table.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                this, "Select a book from the table first."
            );
            return;
        }

        if (!validateFields()) return;

        int row = table.convertRowIndexToModel(selected);
        int newId = Integer.parseInt(idField.getText().trim());

        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != row &&
                Integer.parseInt(model.getValueAt(i, 0).toString()) == newId) {

                JOptionPane.showMessageDialog(
                    this, "Another book already uses this ID."
                );
                return;
            }
        }

        model.setValueAt(newId, row, 0);
        model.setValueAt(titleField.getText().trim(), row, 1);
        model.setValueAt(authorField.getText().trim(), row, 2);
        model.setValueAt(
            Double.parseDouble(priceField.getText().trim()), row, 3
        );
        model.setValueAt(
            Integer.parseInt(quantityField.getText().trim()), row, 4
        );

        JOptionPane.showMessageDialog(this, "Book updated successfully!");
        clearFields();
    }

    // Delete selected book
    private void deleteBook() {
        int selected = table.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                this, "Select a book from the table first."
            );
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this, "Do you want to delete this book?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            int row = table.convertRowIndexToModel(selected);
            model.removeRow(row);

            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
            clearFields();
        }
    }

    // Display all books
    private void viewAllBooks() {
        searchField.setText("");
        sorter.setRowFilter(null);
        table.clearSelection();
    }

    // Clear fields
    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        priceField.setText("");
        quantityField.setText("");
        searchField.setText("");

        sorter.setRowFilter(null);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookInventory());
    }
}

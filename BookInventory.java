
package BookInventory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookInventory extends JFrame {

    private JTextField idField, titleField, authorField;
    private JTextField priceField, quantityField, searchField;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private final File dataFile = new File("books.csv");

    public BookInventory() {
        createGUI();
        loadBooks();
        setVisible(true);
    }

    private void createGUI() {
        setTitle("Book Inventory Management System");
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        main.setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel(
            "BOOK INVENTORY MANAGEMENT SYSTEM",
            SwingConstants.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(31, 58, 95));
        heading.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5));
        main.add(heading, BorderLayout.NORTH);

        // Book entry form
        JPanel form = new JPanel(new GridLayout(5, 2, 12, 12));
        form.setBorder(BorderFactory.createTitledBorder("Book Details"));
        form.setBackground(Color.WHITE);

        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();

        addFormRow(form, "Book ID:", idField);
        addFormRow(form, "Book Name:", titleField);
        addFormRow(form, "Author Name:", authorField);
        addFormRow(form, "Price:", priceField);
        addFormRow(form, "Quantity:", quantityField);

        JButton addBtn = new JButton("Add Book");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewBtn = new JButton("View All");
        JButton clearBtn = new JButton("Clear");

        styleButton(addBtn, new Color(35, 135, 85));
        styleButton(updateBtn, new Color(45, 105, 180));
        styleButton(deleteBtn, new Color(190, 65, 65));
        styleButton(viewBtn, new Color(90, 90, 110));
        styleButton(clearBtn, new Color(100, 110, 125));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttons.setBackground(new Color(245, 247, 250));
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(viewBtn);
        buttons.add(clearBtn);

        JPanel formPanel = new JPanel(new BorderLayout(8, 8));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.add(form, BorderLayout.CENTER);
        formPanel.add(buttons, BorderLayout.SOUTH);

        // Search panel
        searchField = new JTextField(25);
        JButton searchBtn = new JButton("Search");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(
            BorderFactory.createTitledBorder("Find a Book")
        );
        searchPanel.add(new JLabel("Enter book name or ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

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
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 14)
        );
        table.setAutoCreateRowSorter(true);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(
            BorderFactory.createTitledBorder("Available Books")
        );
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(new Color(245, 247, 250));
        center.add(formPanel, BorderLayout.NORTH);
        center.add(tablePanel, BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);
        setContentPane(main);

        // Actions
        addBtn.addActionListener(e -> addBook());
        updateBtn.addActionListener(e -> updateBook());
        deleteBtn.addActionListener(e -> deleteBook());
        viewBtn.addActionListener(e -> viewAllBooks());
        searchBtn.addActionListener(e -> searchBook());
        searchField.addActionListener(e -> searchBook());
        clearBtn.addActionListener(e -> clearFields());

        // Fill fields when selecting a book
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() >= 0) {
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
    }

    private void addFormRow(JPanel panel, String label, JTextField field) {
        JLabel text = new JLabel(label);
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(text);
        panel.add(field);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(105, 34));
    }

    private boolean validateFields() {
        if (idField.getText().trim().isEmpty()
                || titleField.getText().trim().isEmpty()
                || authorField.getText().trim().isEmpty()
                || priceField.getText().trim().isEmpty()
                || quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Please fill in all book details."
            );
            return false;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (id <= 0 || price < 0 || quantity < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Book ID must be positive. Price and quantity cannot be negative."
                );
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid ID, price and quantity."
            );
            return false;
        }

        return true;
    }

    private void addBook() {
        if (!validateFields()) return;

        int id = Integer.parseInt(idField.getText().trim());

        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == id) {
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

        if (saveBooks()) {
            JOptionPane.showMessageDialog(
                this, "Book added successfully!"
            );
            clearFields();
        } else {
            model.removeRow(model.getRowCount() - 1);
        }
    }

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

        Object[] oldData = new Object[5];
        for (int i = 0; i < 5; i++) {
            oldData[i] = model.getValueAt(row, i);
        }

        model.setValueAt(newId, row, 0);
        model.setValueAt(titleField.getText().trim(), row, 1);
        model.setValueAt(authorField.getText().trim(), row, 2);
        model.setValueAt(Double.parseDouble(priceField.getText().trim()), row, 3);
        model.setValueAt(Integer.parseInt(quantityField.getText().trim()), row, 4);

        if (saveBooks()) {
            JOptionPane.showMessageDialog(
                this, "Book updated successfully!"
            );
            clearFields();
        } else {
            for (int i = 0; i < 5; i++) {
                model.setValueAt(oldData[i], row, i);
            }
        }
    }

    private void deleteBook() {
        int selected = table.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                this, "Select a book from the table first."
            );
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to delete this book?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) return;

        int row = table.convertRowIndexToModel(selected);
        Object[] oldData = new Object[5];

        for (int i = 0; i < 5; i++) {
            oldData[i] = model.getValueAt(row, i);
        }

        model.removeRow(row);

        if (saveBooks()) {
            JOptionPane.showMessageDialog(
                this, "Book deleted successfully!"
            );
            clearFields();
        } else {
            model.addRow(oldData);
        }
    }

    private void searchBook() {
        String text = searchField.getText().trim();

        try {
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(
                    RowFilter.regexFilter(
                        "(?i)" + java.util.regex.Pattern.quote(text), 0, 1
                    )
                );
            }

            if (!text.isEmpty() && table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                    this, "No matching books found."
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this, "Search failed: " + e.getMessage()
            );
        }
    }

    private void viewAllBooks() {
        searchField.setText("");
        sorter.setRowFilter(null);
        table.clearSelection();
    }

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

    // Save all records to a local CSV file
    private boolean saveBooks() {
        File tempFile = new File(dataFile.getPath() + ".tmp");

        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(tempFile)))) {

            for (int i = 0; i < model.getRowCount(); i++) {
                List<String> values = new ArrayList<>();

                for (int j = 0; j < 5; j++) {
                    String value = model.getValueAt(i, j).toString();
                    values.add("\"" + value.replace("\"", "\"\"") + "\"");
                }

                writer.println(String.join(",", values));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this, "Unable to save books: " + e.getMessage()
            );
            return false;
        }

        try {
            java.nio.file.Files.move(
                tempFile.toPath(),
                dataFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            return true;
        } catch (IOException e) {
            tempFile.delete();
            JOptionPane.showMessageDialog(
                this, "Unable to save book file: " + e.getMessage()
            );
            return false;
        }
    }

    // Load saved records at startup
    private void loadBooks() {
        if (!dataFile.exists()) return;

        try (BufferedReader reader =
                 new BufferedReader(new FileReader(dataFile))) {

            String line;

            while ((line = reader.readLine()) != null) {
                List<String> values = parseCSV(line);

                if (values.size() != 5) continue;

                try {
                    int id = Integer.parseInt(values.get(0));
                    String title = values.get(1);
                    String author = values.get(2);
                    double price = Double.parseDouble(values.get(3));
                    int quantity = Integer.parseInt(values.get(4));

                    boolean duplicate = false;

                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (Integer.parseInt(
                                model.getValueAt(i, 0).toString()) == id) {
                            duplicate = true;
                            break;
                        }
                    }

                    if (!duplicate) {
                        model.addRow(new Object[]{
                            id, title, author, price, quantity
                        });
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid records
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this, "Unable to load saved books: " + e.getMessage()
            );
        }
    }

    private List<String> parseCSV(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (quoted && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(c);
            }
        }

        values.add(value.toString());
        return values;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookInventory::new);
    }
}

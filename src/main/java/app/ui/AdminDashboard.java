package app.ui;

import app.model.Issue;
import app.service.IssueService;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private final IssueService issueService = new IssueService();
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private JTextField locationFilterField;
    private JComboBox<String> statusFilterCombo;
    private JTextField categoryFilterField;
    private JPanel totalLabel, pendingLabel, resolvedLabel, highPriorityLabel;
    private JLabel totalValueLabel, pendingValueLabel, resolvedValueLabel, highPriorityValueLabel;
    private JFrame parentFrame;

    private final String[] statuses = {"", "Pending", "In Progress", "Resolved"};

    public AdminDashboard(JFrame parent) {
        this.parentFrame = parent;
        initializeUI();
        loadIssueTable();
        refreshStats();
    }

    private void initializeUI() {
        setTitle("Smart Civic Issue Reporting System - Admin Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        // Dark theme colors
        Color bgColor = new Color(45, 45, 45);
        Color fgColor = new Color(220, 220, 220);
        Color accentColor = new Color(70, 130, 180);
        Color cardColor = new Color(60, 60, 60);

        getContentPane().setBackground(bgColor);

        // Header
        JPanel headerPanel = createHeaderPanel(fgColor, accentColor);
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Stats panel
        JPanel statsPanel = createStatsPanel(cardColor, fgColor, accentColor);
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(bgColor);

        // Filters panel
        JPanel filtersPanel = createFiltersPanel(cardColor, fgColor, accentColor);
        contentPanel.add(filtersPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createTablePanel(cardColor, fgColor, accentColor);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createHeaderPanel(Color fgColor, Color accentColor) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Admin Dashboard - Issue Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(accentColor);

        JButton backBtn = new JButton("← Back to Citizen Portal");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(80, 80, 80));
        backBtn.setForeground(fgColor);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            parentFrame.setVisible(true);
        });

        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsPanel(Color cardColor, Color fgColor, Color accentColor) {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(new Color(45, 45, 45));
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        totalLabel = createStatCard("Total Issues", "0", cardColor, fgColor, accentColor);
        totalValueLabel = getValueLabelFromCard(totalLabel);
        pendingLabel = createStatCard("Pending", "0", cardColor, fgColor, accentColor);
        pendingValueLabel = getValueLabelFromCard(pendingLabel);
        resolvedLabel = createStatCard("Resolved", "0", cardColor, fgColor, accentColor);
        resolvedValueLabel = getValueLabelFromCard(resolvedLabel);
        highPriorityLabel = createStatCard("High Priority", "0", cardColor, fgColor, accentColor);
        highPriorityValueLabel = getValueLabelFromCard(highPriorityLabel);

        statsPanel.add(totalLabel);
        statsPanel.add(pendingLabel);
        statsPanel.add(resolvedLabel);
        statsPanel.add(highPriorityLabel);

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color cardColor, Color fgColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setName("valueLabel"); // Add name for identification

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(fgColor);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JLabel getValueLabelFromCard(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JLabel && "valueLabel".equals(((JLabel) comp).getName())) {
                return (JLabel) comp;
            }
        }
        return null;
    }

    private JPanel createFiltersPanel(Color cardColor, Color fgColor, Color accentColor) {
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filtersPanel.setBackground(cardColor);
        filtersPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Location filter
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setForeground(fgColor);
        locationFilterField = new JTextField(15);
        locationFilterField.setBackground(new Color(70, 70, 70));
        locationFilterField.setForeground(fgColor);
        locationFilterField.setCaretColor(fgColor);

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setForeground(fgColor);
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setBackground(new Color(70, 70, 70));
        statusFilterCombo.setForeground(fgColor);

        // Category filter
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(fgColor);
        categoryFilterField = new JTextField(15);
        categoryFilterField.setBackground(new Color(70, 70, 70));
        categoryFilterField.setForeground(fgColor);
        categoryFilterField.setCaretColor(fgColor);

        // Filter button
        JButton filterBtn = new JButton("Apply Filters");
        filterBtn.setBackground(accentColor);
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFocusPainted(false);
        filterBtn.addActionListener(e -> applyFilters());

        // Clear button
        JButton clearBtn = new JButton("Clear Filters");
        clearBtn.setBackground(new Color(80, 80, 80));
        clearBtn.setForeground(fgColor);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearFilters());

        filtersPanel.add(locationLabel);
        filtersPanel.add(locationFilterField);
        filtersPanel.add(statusLabel);
        filtersPanel.add(statusFilterCombo);
        filtersPanel.add(categoryLabel);
        filtersPanel.add(categoryFilterField);
        filtersPanel.add(filterBtn);
        filtersPanel.add(clearBtn);

        return filtersPanel;
    }

    private JPanel createTablePanel(Color cardColor, Color fgColor, Color accentColor) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(cardColor);
        tablePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Table model
        tableModel = new DefaultTableModel(new Object[]{
            "ID", "Category", "Description", "Location", "Status", "Created", "Updated", "Priority", "Image", "Start Proof", "End Proof"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        issueTable = new JTable(tableModel);
        issueTable.setBackground(new Color(60, 60, 60));
        issueTable.setForeground(fgColor);
        issueTable.setGridColor(new Color(80, 80, 80));
        issueTable.setSelectionBackground(accentColor);
        issueTable.getTableHeader().setBackground(new Color(50, 50, 50));
        issueTable.getTableHeader().setForeground(fgColor);
        issueTable.setRowHeight(25);
        issueTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set column widths
        issueTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        issueTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Category
        issueTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Description
        issueTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Location
        issueTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        issueTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Created
        issueTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Updated
        issueTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Priority
        issueTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Image
        issueTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Start Proof
        issueTable.getColumnModel().getColumn(10).setPreferredWidth(100); // End Proof

        // Custom renderer for priority column
        issueTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ("HIGH".equals(value)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(fgColor);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                return c;
            }
        });

        // Custom renderer for status column
        issueTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                switch (status) {
                    case "Pending":
                        c.setForeground(Color.RED);
                        break;
                    case "In Progress":
                        c.setForeground(Color.ORANGE);
                        break;
                    case "Resolved":
                        c.setForeground(Color.GREEN);
                        break;
                    default:
                        c.setForeground(fgColor);
                }
                c.setFont(c.getFont().deriveFont(Font.BOLD));
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(issueTable);
        tableScroll.setBackground(cardColor);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(cardColor);

        JButton updateStatusBtn = new JButton("Update Status + Upload Proof");
        updateStatusBtn.setBackground(accentColor);
        updateStatusBtn.setForeground(Color.WHITE);
        updateStatusBtn.setFocusPainted(false);
        updateStatusBtn.addActionListener(e -> updateStatus());

        JButton viewProofBtn = new JButton("View Proof Images");
        viewProofBtn.setBackground(new Color(80, 80, 80));
        viewProofBtn.setForeground(fgColor);
        viewProofBtn.setFocusPainted(false);
        viewProofBtn.addActionListener(e -> viewProofImages());

        JButton viewImageBtn = new JButton("View Issue Image");
        viewImageBtn.setBackground(new Color(80, 80, 80));
        viewImageBtn.setForeground(fgColor);
        viewImageBtn.setFocusPainted(false);
        viewImageBtn.addActionListener(e -> viewImage());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(80, 80, 80));
        refreshBtn.setForeground(fgColor);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> {
            loadIssueTable();
            refreshStats();
        });

        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(viewProofBtn);
        buttonPanel.add(viewImageBtn);
        buttonPanel.add(refreshBtn);

        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void applyFilters() {
        loadIssueTable();
        refreshStats();
    }

    private void clearFilters() {
        locationFilterField.setText("");
        statusFilterCombo.setSelectedIndex(0);
        categoryFilterField.setText("");
        loadIssueTable();
        refreshStats();
    }

    private void loadIssueTable() {
        tableModel.setRowCount(0);
        String location = locationFilterField.getText().trim();
        String status = (String) statusFilterCombo.getSelectedItem();
        String category = categoryFilterField.getText().trim();

        List<Issue> issues = issueService.filterIssues(
            location.isEmpty() ? null : location,
            status.isEmpty() ? null : status,
            category.isEmpty() ? null : category
        );

        for (Issue issue : issues) {
            String priority = issue.isHighPriority() ? "HIGH" : "Normal";
            String hasImage = issue.getImagePath() != null && !issue.getImagePath().isEmpty() ? "Yes" : "No";
            String hasStartProof = issue.getProofStartPath() != null && !issue.getProofStartPath().isEmpty() ? "Yes" : "No";
            String hasEndProof = issue.getProofEndPath() != null && !issue.getProofEndPath().isEmpty() ? "Yes" : "No";

            tableModel.addRow(new Object[]{
                issue.getId(),
                issue.getCategory(),
                issue.getDescription(),
                issue.getLocation(),
                issue.getStatus(),
                issue.getCreatedAt().toString(),
                issue.getUpdatedAt().toString(),
                priority,
                hasImage,
                hasStartProof,
                hasEndProof
            });
        }
    }

    private void refreshStats() {
        Map<String, Integer> stats = issueService.getDashboardStats(null); // Admin sees all issues

        totalValueLabel.setText(String.valueOf(stats.getOrDefault("Total", 0)));
        pendingValueLabel.setText(String.valueOf(stats.getOrDefault("Pending", 0)));
        resolvedValueLabel.setText(String.valueOf(stats.getOrDefault("Resolved", 0)));
        highPriorityValueLabel.setText(String.valueOf(issueService.getHighPriorityCount(null)));
    }

    private void updateStatus() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int issueId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        // Create dialog for status update with proof upload
        JDialog updateDialog = new JDialog(this, "Update Status & Upload Proof", true);
        updateDialog.setLayout(new BorderLayout());
        updateDialog.setSize(500, 400);
        updateDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Status dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("New Status:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"In Progress", "Resolved"});
        statusCombo.setSelectedItem(currentStatus);
        contentPanel.add(statusCombo, gbc);

        // File chooser for proof
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Proof Image:"), gbc);
        gbc.gridx = 1;
        JButton chooseFileBtn = new JButton("Choose Image");
        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false);
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(chooseFileBtn, BorderLayout.EAST);
        contentPanel.add(filePanel, gbc);

        // Image preview
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel previewLabel = new JLabel("No image selected", SwingConstants.CENTER);
        previewLabel.setPreferredSize(new Dimension(200, 150));
        previewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.add(previewLabel, gbc);

        chooseFileBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(updateDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                // Show preview
                try {
                    ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    previewLabel.setIcon(new ImageIcon(img));
                    previewLabel.setText("");
                } catch (Exception ex) {
                    previewLabel.setIcon(null);
                    previewLabel.setText("Failed to load preview");
                }
            }
        });

        updateDialog.add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateBtn = new JButton("Update Status + Upload Proof");
        JButton cancelBtn = new JButton("Cancel");

        updateBtn.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            String proofPath = filePathField.getText().trim();

            if (newStatus.equals(currentStatus)) {
                JOptionPane.showMessageDialog(updateDialog, "Please select a different status.", "No Change", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ((newStatus.equals("In Progress") || newStatus.equals("Resolved")) && proofPath.isEmpty()) {
                JOptionPane.showMessageDialog(updateDialog, "Proof image is required for status change to " + newStatus, "Proof Required", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(updateDialog,
                "Are you sure you want to update the status to '" + newStatus + "'?",
                "Confirm Status Update", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String savedImagePath = null;
                if (!proofPath.isEmpty()) {
                    savedImagePath = saveImage(proofPath);
                    if (savedImagePath == null) {
                        JOptionPane.showMessageDialog(updateDialog, "Failed to save proof image.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (issueService.updateIssueStatus(issueId, newStatus, savedImagePath)) {
                    JOptionPane.showMessageDialog(updateDialog, "Status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadIssueTable();
                    refreshStats();
                    updateDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(updateDialog, "Failed to update status. Please check if the transition is valid.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> updateDialog.dispose());

        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        updateDialog.add(buttonPanel, BorderLayout.SOUTH);

        updateDialog.setVisible(true);
    }

    private void viewProofImages() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue to view proof images.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int issueId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Issue selectedIssue = issueService.getIssueById(issueId);

        if (selectedIssue != null) {
            JDialog proofDialog = new JDialog(this, "Proof Images for Issue #" + issueId, true);
            proofDialog.setLayout(new BorderLayout());
            proofDialog.setSize(800, 600);
            proofDialog.setLocationRelativeTo(this);

            JPanel imagesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            imagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Start proof image
            JPanel startPanel = new JPanel(new BorderLayout());
            startPanel.setBorder(BorderFactory.createTitledBorder("Work Started Proof"));
            if (selectedIssue.getProofStartPath() != null && !selectedIssue.getProofStartPath().isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(selectedIssue.getProofStartPath());
                    Image img = icon.getImage().getScaledInstance(350, 400, Image.SCALE_SMOOTH);
                    JLabel imgLabel = new JLabel(new ImageIcon(img));
                    startPanel.add(new JScrollPane(imgLabel), BorderLayout.CENTER);
                } catch (Exception ex) {
                    startPanel.add(new JLabel("Failed to load image", SwingConstants.CENTER), BorderLayout.CENTER);
                }
            } else {
                startPanel.add(new JLabel("No start proof available", SwingConstants.CENTER), BorderLayout.CENTER);
            }

            // End proof image
            JPanel endPanel = new JPanel(new BorderLayout());
            endPanel.setBorder(BorderFactory.createTitledBorder("Work Completed Proof"));
            if (selectedIssue.getProofEndPath() != null && !selectedIssue.getProofEndPath().isEmpty()) {
                try {
                    ImageIcon icon = new ImageIcon(selectedIssue.getProofEndPath());
                    Image img = icon.getImage().getScaledInstance(350, 400, Image.SCALE_SMOOTH);
                    JLabel imgLabel = new JLabel(new ImageIcon(img));
                    endPanel.add(new JScrollPane(imgLabel), BorderLayout.CENTER);
                } catch (Exception ex) {
                    endPanel.add(new JLabel("Failed to load image", SwingConstants.CENTER), BorderLayout.CENTER);
                }
            } else {
                endPanel.add(new JLabel("No completion proof available", SwingConstants.CENTER), BorderLayout.CENTER);
            }

            imagesPanel.add(startPanel);
            imagesPanel.add(endPanel);

            proofDialog.add(imagesPanel, BorderLayout.CENTER);

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> proofDialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeBtn);
            proofDialog.add(buttonPanel, BorderLayout.SOUTH);

            proofDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Issue not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewImage() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue to view image.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int issueId = (Integer) tableModel.getValueAt(selectedRow, 0);
        // Find the issue to get image path
        List<Issue> issues = issueService.getAllIssues();
        Issue selectedIssue = issues.stream().filter(i -> i.getId() == issueId).findFirst().orElse(null);

        if (selectedIssue != null && selectedIssue.getImagePath() != null && !selectedIssue.getImagePath().isEmpty()) {
            showImageDialog(selectedIssue.getImagePath(), "Issue Image");
        } else {
            JOptionPane.showMessageDialog(this, "No image available for this issue.", "No Image", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String saveImage(String sourcePath) {
        try {
            Path source = Paths.get(sourcePath);
            String fileName = System.currentTimeMillis() + "_proof_" + source.getFileName().toString();
            Path target = Paths.get("images", fileName);
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void showImageDialog(String imagePath, String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());

        try {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(600, 400, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(scaledIcon);
            JScrollPane scrollPane = new JScrollPane(imageLabel);
            dialog.add(scrollPane, BorderLayout.CENTER);

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeBtn);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setSize(650, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
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

public class UserDashboard extends JFrame {
    private final IssueService issueService = new IssueService();
    private JTextField locationField;
    private JComboBox<String> categoryCombo;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JTable issueTable;
    private JTextField searchField;
    private JPanel totalLabel, pendingLabel, inProgressLabel, resolvedLabel, highPriorityLabel;
    private JLabel totalValueLabel, pendingValueLabel, inProgressValueLabel, resolvedValueLabel, highPriorityValueLabel;
    private DefaultTableModel tableModel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String selectedLocation = "";

    private final String[] categories = {"Pothole", "Garbage", "Streetlight", "Water Leak", "Road Damage", "Other"};

    public UserDashboard() {
        initializeUI();
        refreshDashboard();
        loadIssueTable();
    }

    private void initializeUI() {
        setTitle("Smart Civic Issue Reporting System - Citizen Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
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

        // Sidebar navigation
        JPanel sidebar = createSidebar(accentColor, fgColor, bgColor);
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Center content
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(bgColor);

        // Stats cards
        JPanel statsPanel = createStatsPanel(cardColor, fgColor, accentColor);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Content cards
        cardLayout = new CardLayout();
        contentPanel = createContentPanel(cardColor, fgColor, accentColor);
        JPanel cardContainer = new JPanel(cardLayout);
        cardContainer.add(contentPanel, "CONTENT");
        centerPanel.add(cardContainer, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createHeaderPanel(Color fgColor, Color accentColor) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 30));
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Smart Civic Issue Reporting System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(accentColor);

        JLabel subtitle = new JLabel("Report and track public issues in your community");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(fgColor);

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createSidebar(Color accentColor, Color fgColor, Color bgColor) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(35, 35, 35));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JButton dashboardBtn = createSidebarButton("Dashboard", accentColor, fgColor);
        JButton reportBtn = createSidebarButton("Report Issue", accentColor, fgColor);
        JButton viewBtn = createSidebarButton("View Issues", accentColor, fgColor);
        JButton adminBtn = createSidebarButton("Admin Panel", accentColor, fgColor);

        dashboardBtn.addActionListener(e -> showDashboard());
        reportBtn.addActionListener(e -> showReportIssue());
        viewBtn.addActionListener(e -> showViewIssues());
        adminBtn.addActionListener(e -> openAdminLogin());

        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(reportBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(viewBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(adminBtn);

        return sidebar;
    }

    private JButton createSidebarButton(String text, Color accentColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(fgColor);
        button.setBackground(new Color(50, 50, 50));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(accentColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 50));
            }
        });

        return button;
    }

    private JPanel createStatsPanel(Color cardColor, Color fgColor, Color accentColor) {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        statsPanel.setBackground(new Color(45, 45, 45));
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        totalLabel = createStatCard("Total Issues", "0", cardColor, fgColor, accentColor);
        totalValueLabel = getValueLabelFromCard(totalLabel);
        pendingLabel = createStatCard("Pending", "0", cardColor, fgColor, accentColor);
        pendingValueLabel = getValueLabelFromCard(pendingLabel);
        inProgressLabel = createStatCard("In Progress", "0", cardColor, fgColor, accentColor);
        inProgressValueLabel = getValueLabelFromCard(inProgressLabel);
        resolvedLabel = createStatCard("Resolved", "0", cardColor, fgColor, accentColor);
        resolvedValueLabel = getValueLabelFromCard(resolvedLabel);
        highPriorityLabel = createStatCard("High Priority", "0", cardColor, fgColor, accentColor);
        highPriorityValueLabel = getValueLabelFromCard(highPriorityLabel);

        statsPanel.add(totalLabel);
        statsPanel.add(pendingLabel);
        statsPanel.add(inProgressLabel);
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

    private JPanel createContentPanel(Color cardColor, Color fgColor, Color accentColor) {
        JPanel panel = new JPanel(new CardLayout());
        CardLayout cl = (CardLayout) panel.getLayout();

        JPanel dashboardCard = createDashboardCard(cardColor, fgColor, accentColor);
        JPanel reportCard = createReportIssueCard(cardColor, fgColor, accentColor);
        JPanel viewCard = createViewIssuesCard(cardColor, fgColor, accentColor);

        panel.add(dashboardCard, "DASHBOARD");
        panel.add(reportCard, "REPORT");
        panel.add(viewCard, "VIEW");

        // Store the layout reference
        this.cardLayout = cl;
        this.contentPanel = panel;

        return panel;
    }

    private JPanel createDashboardCard(Color cardColor, Color fgColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to Civic Issue Reporting", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(fgColor);

        JTextArea infoText = new JTextArea(
            "Use this system to report civic issues in your community.\n\n" +
            "• Report new issues with photos and descriptions\n" +
            "• View status of reported issues\n" +
            "• Track resolution progress\n" +
            "• Help improve your community"
        );
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoText.setForeground(fgColor);
        infoText.setBackground(cardColor);
        infoText.setEditable(false);
        infoText.setWrapStyleWord(true);
        infoText.setLineWrap(true);

        card.add(welcomeLabel, BorderLayout.NORTH);
        card.add(infoText, BorderLayout.CENTER);

        return card;
    }

    private JPanel createReportIssueCard(Color cardColor, Color fgColor, Color accentColor) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(cardColor);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Location
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setForeground(fgColor);
        locationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(locationLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        locationField = new JTextField(20);
        locationField.setBackground(new Color(70, 70, 70));
        locationField.setForeground(fgColor);
        locationField.setCaretColor(fgColor);
        card.add(locationField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(fgColor);
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(categoryLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setBackground(new Color(70, 70, 70));
        categoryCombo.setForeground(fgColor);
        card.add(categoryCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(fgColor);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(descLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setBackground(new Color(70, 70, 70));
        descriptionArea.setForeground(fgColor);
        descriptionArea.setCaretColor(fgColor);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(descScroll, gbc);

        // Image
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        JLabel imageLabel = new JLabel("Image:");
        imageLabel.setForeground(fgColor);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(imageLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(cardColor);

        imagePathField = new JTextField();
        imagePathField.setBackground(new Color(70, 70, 70));
        imagePathField.setForeground(fgColor);
        imagePathField.setCaretColor(fgColor);
        imagePathField.setEditable(false);
        imagePanel.add(imagePathField, BorderLayout.CENTER);

        JButton browseBtn = new JButton("Browse");
        browseBtn.setBackground(accentColor);
        browseBtn.setForeground(Color.WHITE);
        browseBtn.setFocusPainted(false);
        browseBtn.addActionListener(e -> browseImage());
        imagePanel.add(browseBtn, BorderLayout.EAST);

        card.add(imagePanel, gbc);

        // Submit button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = new JButton("Submit Issue");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitBtn.setBackground(accentColor);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.addActionListener(e -> submitIssue());
        card.add(submitBtn, gbc);

        return card;
    }

    private JPanel createViewIssuesCard(Color cardColor, Color fgColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(cardColor);

        JLabel searchLabel = new JLabel("Filter by Location:");
        searchLabel.setForeground(fgColor);
        searchField = new JTextField(20);
        searchField.setBackground(new Color(70, 70, 70));
        searchField.setForeground(fgColor);
        searchField.setCaretColor(fgColor);

        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(accentColor);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> filterIssues());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        card.add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Category", "Description", "Location", "Status", "Created", "Priority"}, 0) {
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

        // Custom renderer for priority column
        issueTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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
        card.add(tableScroll, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(cardColor);

        JButton viewProofBtn = new JButton("View Proof Images");
        viewProofBtn.setBackground(accentColor);
        viewProofBtn.setForeground(Color.WHITE);
        viewProofBtn.setFocusPainted(false);
        viewProofBtn.addActionListener(e -> viewProofImages());

        JButton viewImageBtn = new JButton("View Issue Image");
        viewImageBtn.setBackground(new Color(80, 80, 80));
        viewImageBtn.setForeground(fgColor);
        viewImageBtn.setFocusPainted(false);
        viewImageBtn.addActionListener(e -> viewIssueImage());

        buttonPanel.add(viewProofBtn);
        buttonPanel.add(viewImageBtn);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void showDashboard() {
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private void showReportIssue() {
        cardLayout.show(contentPanel, "REPORT");
    }

    private void showViewIssues() {
        cardLayout.show(contentPanel, "VIEW");
        loadIssueTable();
    }

    private void openAdminLogin() {
        new AdminLogin(this);
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void submitIssue() {
        String location = locationField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        String imagePath = imagePathField.getText().trim();

        if (location.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String savedImagePath = null;
        if (!imagePath.isEmpty()) {
            savedImagePath = saveImage(imagePath);
            if (savedImagePath == null) {
                JOptionPane.showMessageDialog(this, "Failed to save image.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Issue issue = new Issue(category, description, location, savedImagePath);
        if (issueService.addIssue(issue)) {
            JOptionPane.showMessageDialog(this, "Issue reported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearReportForm();
            refreshDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to report issue. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String saveImage(String sourcePath) {
        try {
            Path source = Paths.get(sourcePath);
            String fileName = System.currentTimeMillis() + "_" + source.getFileName().toString();
            Path target = Paths.get("images", fileName);
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void clearReportForm() {
        locationField.setText("");
        descriptionArea.setText("");
        imagePathField.setText("");
        categoryCombo.setSelectedIndex(0);
    }

    private void filterIssues() {
        selectedLocation = searchField.getText().trim();
        loadIssueTable();
        refreshDashboard();
    }

    private void loadIssueTable() {
        tableModel.setRowCount(0);
        List<Issue> issues = selectedLocation.isEmpty() ?
            issueService.getAllIssues() :
            issueService.getIssuesByLocation(selectedLocation);

        for (Issue issue : issues) {
            String priority = issue.isHighPriority() ? "HIGH" : "Normal";
            tableModel.addRow(new Object[]{
                issue.getId(),
                issue.getCategory(),
                issue.getDescription(),
                issue.getLocation(),
                issue.getStatus(),
                issue.getCreatedAt().toString(),
                priority
            });
        }
    }

    private void refreshDashboard() {
        Map<String, Integer> stats = issueService.getDashboardStats(selectedLocation.isEmpty() ? null : selectedLocation);

        totalValueLabel.setText(String.valueOf(stats.getOrDefault("Total", 0)));
        pendingValueLabel.setText(String.valueOf(stats.getOrDefault("Pending", 0)));
        inProgressValueLabel.setText(String.valueOf(stats.getOrDefault("In Progress", 0)));
        resolvedValueLabel.setText(String.valueOf(stats.getOrDefault("Resolved", 0)));
        highPriorityValueLabel.setText(String.valueOf(issueService.getHighPriorityCount(selectedLocation.isEmpty() ? null : selectedLocation)));
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

    private void viewIssueImage() {
        int selectedRow = issueTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue to view image.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int issueId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Issue selectedIssue = issueService.getIssueById(issueId);

        if (selectedIssue != null && selectedIssue.getImagePath() != null && !selectedIssue.getImagePath().isEmpty()) {
            showImageDialog(selectedIssue.getImagePath(), "Issue Image");
        } else {
            JOptionPane.showMessageDialog(this, "No image available for this issue.", "No Image", JOptionPane.INFORMATION_MESSAGE);
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
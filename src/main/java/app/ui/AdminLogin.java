package app.ui;

import java.awt.*;
import javax.swing.*;

public class AdminLogin extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JFrame parentFrame;

    public AdminLogin(JFrame parent) {
        super(parent, "Admin Login", true);
        this.parentFrame = parent;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Dark theme
        Color bgColor = new Color(45, 45, 45);
        Color fgColor = new Color(220, 220, 220);
        Color accentColor = new Color(70, 130, 180);
        Color cardColor = new Color(60, 60, 60);

        getContentPane().setBackground(bgColor);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 30, 30));
        headerPanel.setBorder(new javax.swing.border.EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(accentColor);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(cardColor);
        formPanel.setBorder(new javax.swing.border.EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(fgColor);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        emailField.setBackground(new Color(70, 70, 70));
        emailField.setForeground(fgColor);
        emailField.setCaretColor(fgColor);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(fgColor);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setBackground(new Color(70, 70, 70));
        passwordField.setForeground(fgColor);
        passwordField.setCaretColor(fgColor);
        formPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(cardColor);

        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(accentColor);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> login());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.setBackground(new Color(80, 80, 80));
        cancelBtn.setForeground(fgColor);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);

        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if ("manoharreddyind@gmail.com".equals(email) && "Nani@8888".equals(password)) {
            dispose();
            new AdminDashboard(parentFrame);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.*;

public class HealthcareFinanceGUI {
    // Backend components
    private static String systemUser;
    private static String systemPass;
    private static Client activeClient;
    private static SavingsAccount activeAccount;
    
    // GUI Components
    private JFrame frame;
    private JTextPane processDisplayArea;
    private JLabel balanceLabel;
    private JLabel userInfoLabel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Colors
    private final Color BG_COLOR = new Color(12, 15, 28);
    private final Color PANEL_COLOR = new Color(7, 11, 20);
    private final Color TEXT_COLOR = new Color(168, 179, 207);
    private final Color ACCENT_COLOR = new Color(78, 205, 196);
    private final Color SUCCESS_COLOR = new Color(107, 220, 107);
    private final Color ERROR_COLOR = new Color(255, 107, 107);
    private final Color WARNING_COLOR = new Color(255, 217, 61);
    
    private StyledDocument doc;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // Audit logs list
    private static List<String> auditLogs = new ArrayList<>();
    
    public HealthcareFinanceGUI() {
        initializeGUI();
    }
    
    private void initializeGUI() {
        frame = new JFrame("Universal Health & Finance Network - Secure Terminal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 800);
        frame.setLocationRelativeTo(null);
        
        // Main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(BG_COLOR);
        splitPane.setDividerLocation(500);
        
        // Left Panel - Menu and Forms
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_COLOR);
        
        // Header
        leftPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Card layout for different screens
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BG_COLOR);
        
        cardPanel.add(createSecurityGatePanel(), "SECURITY");
        cardPanel.add(createRegistrationPanel(), "REGISTRATION");
        cardPanel.add(createMainMenuPanel(), "MAIN_MENU");
        
        leftPanel.add(cardPanel, BorderLayout.CENTER);
        
        // Right Panel - Full Process Display
        JPanel rightPanel = createFullProcessDisplayPanel();
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        frame.add(splitPane);
        frame.setVisible(true);
        
        cardLayout.show(cardPanel, "SECURITY");
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(26, 31, 46));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("🏥 UNIVERSAL HEALTH & FINANCE NETWORK");
        title.setFont(new Font("Monospaced", Font.BOLD, 16));
        title.setForeground(ACCENT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitle = new JLabel("SECURE CROSS-DOMAIN LEDGER // ENTERPRISE EDITION");
        subtitle.setFont(new Font("Monospaced", Font.PLAIN, 10));
        subtitle.setForeground(new Color(108, 122, 158));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);
        
        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }
    
    private JPanel createFullProcessDisplayPanel() {
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setBackground(PANEL_COLOR);
        processPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(42, 47, 66)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Top bar with user info and balance
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        userInfoLabel = new JLabel("👤 Not logged in");
        userInfoLabel.setForeground(TEXT_COLOR);
        userInfoLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        balanceLabel = new JLabel("💰 Balance: $0.00");
        balanceLabel.setForeground(SUCCESS_COLOR);
        balanceLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        topBar.add(userInfoLabel, BorderLayout.WEST);
        topBar.add(balanceLabel, BorderLayout.EAST);
        
        processPanel.add(topBar, BorderLayout.NORTH);
        
        // Process display area
        processDisplayArea = new JTextPane();
        processDisplayArea.setBackground(new Color(7, 11, 20));
        processDisplayArea.setForeground(TEXT_COLOR);
        processDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        processDisplayArea.setEditable(false);
        doc = processDisplayArea.getStyledDocument();
        
        JScrollPane scrollPane = new JScrollPane(processDisplayArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(42, 47, 66)));
        scrollPane.getViewport().setBackground(new Color(7, 11, 20));
        
        processPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add initial message
        addToProcessDisplay("==================================================", "info");
        addToProcessDisplay("  WELCOME TO UNIVERSAL HEALTH & FINANCE NETWORK", "accent");
        addToProcessDisplay("==================================================", "info");
        addToProcessDisplay("", "info");
        
        return processPanel;
    }
    
    private void addToProcessDisplay(String message, String type) {
        SwingUtilities.invokeLater(() -> {
            try {
                String timestamp = "[" + LocalDateTime.now().format(timeFormatter) + "] ";
                Color color;
                switch(type) {
                    case "success": color = SUCCESS_COLOR; break;
                    case "error": color = ERROR_COLOR; break;
                    case "warning": color = WARNING_COLOR; break;
                    case "accent": color = ACCENT_COLOR; break;
                    default: color = TEXT_COLOR;
                }
                
                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
                
                if (!message.isEmpty()) {
                    doc.insertString(doc.getLength(), timestamp + message + "\n", aset);
                } else {
                    doc.insertString(doc.getLength(), "\n", aset);
                }
                processDisplayArea.setCaretPosition(doc.getLength());
                
                // Also add to audit logs
                auditLogs.add(timestamp + message);
            } catch (BadLocationException e) {}
        });
    }
    
    private void addSeparator() {
        addToProcessDisplay("==================================================", "info");
    }
    
    private void updateUserInfo() {
        if (activeClient != null && userInfoLabel != null) {
            userInfoLabel.setText(String.format("👤 %s | ID: %s | Type: %s", 
                activeClient.getName(), activeClient.getId(), activeClient.getClientType()));
        }
    }
    
    private void updateBalanceLabel() {
        if (activeAccount != null && balanceLabel != null) {
            balanceLabel.setText(String.format("💰 Balance: $%.2f", activeAccount.getBalance()));
        }
    }
    
    private JPanel createSecurityGatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel gateLabel = new JLabel(">>> SECURITY GATE: Initialize System Administrator Login <<<");
        gateLabel.setForeground(ACCENT_COLOR);
        gateLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(gateLabel, gbc);
        
        // Username
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createStyledLabel("Create a Login Username:"), gbc);
        
        JTextField usernameField = createStyledTextField();
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createStyledLabel("Create a Login Password:"), gbc);
        
        JPasswordField passwordField = createStyledPasswordField();
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);
        
        JButton initButton = createStyledButton("▶ INITIALIZE SYSTEM");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(initButton, gbc);
        
        initButton.addActionListener(e -> {
            systemUser = usernameField.getText().trim();
            systemPass = new String(passwordField.getPassword()).trim();
            
            if (systemUser.isEmpty() || systemPass.isEmpty()) {
                addToProcessDisplay("[ERROR] Username and password required!", "error");
                return;
            }
            
            addToProcessDisplay(">>> SECURITY GATE: Initialize System Administrator Login <<<", "accent");
            addToProcessDisplay("[SYSTEM AUTH] Admin account created for user: " + systemUser, "success");
            addToProcessDisplay("[SECURITY] Security gate passed. Proceed to registration.", "info");
            addToProcessDisplay("", "info");
            
            cardLayout.show(cardPanel, "REGISTRATION");
        });
        
        return panel;
    }
    
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        
        JLabel title = new JLabel("--- Initial Identity Registration ---");
        title.setForeground(ACCENT_COLOR);
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        panel.add(title, gbc);
        
        // Form fields with default values
        JTextField idField = createStyledTextField();
        idField.setText("7244");
        JTextField nameField = createStyledTextField();
        nameField.setText("m.azfar");
        JTextField emailField = createStyledTextField();
        emailField.setText("sdf@gmail.com");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"STUDENT", "PATIENT"});
        typeCombo.setSelectedItem("STUDENT");
        typeCombo.setBackground(new Color(13, 17, 28));
        typeCombo.setForeground(ACCENT_COLOR);
        typeCombo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JTextField depositField = createStyledTextField();
        depositField.setText("2000");
        
        String[][] fields = {
            {"Enter Client Registration ID:", "7244"},
            {"Enter Full Name:", "m.azfar"},
            {"Enter Email Address:", "sdf@gmail.com"},
            {"Enter Client Type (STUDENT / PATIENT):", "STUDENT"},
            {"Enter Initial Bank Deposit Amount ($):", "2000"}
        };
        
        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridx = 0;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.EAST;
            panel.add(createStyledLabel(fields[i][0]), gbc);
            
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            if (i == 3) {
                panel.add(typeCombo, gbc);
            } else if (i == 4) {
                panel.add(depositField, gbc);
            } else if (i == 0) {
                panel.add(idField, gbc);
            } else if (i == 1) {
                panel.add(nameField, gbc);
            } else if (i == 2) {
                panel.add(emailField, gbc);
            }
        }
        
        JButton registerButton = createStyledButton("✓ REGISTER CLIENT");
        gbc.gridy = fields.length + 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(registerButton, gbc);
        
        registerButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            double deposit;
            
            try {
                deposit = Double.parseDouble(depositField.getText().trim());
            } catch (NumberFormatException ex) {
                addToProcessDisplay("[ERROR] Invalid deposit amount!", "error");
                return;
            }
            
            addToProcessDisplay("--- Initial Identity Registration ---", "accent");
            addToProcessDisplay("[REGISTRATION] Client " + name + " [" + type + "] enrolling...", "info");
            
            activeClient = new Client(id, name, email, type);
            activeAccount = new SavingsAccount("ACC-" + (int)(Math.random() * 90000 + 10000), deposit);
            
            addToProcessDisplay("[REGISTRATION] Client " + name + " [" + type + "] enrolled with account " + activeAccount.getAccountNumber(), "success");
            addToProcessDisplay("[BANK] Deposit of $" + deposit + " processed for " + activeAccount.getAccountNumber(), "success");
            addToProcessDisplay("", "info");
            
            updateUserInfo();
            updateBalanceLabel();
            
            cardLayout.show(cardPanel, "MAIN_MENU");
        });
        
        return panel;
    }
    
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel menuTitle = new JLabel("══════════ MAIN SYSTEM MENU ══════════");
        menuTitle.setForeground(ACCENT_COLOR);
        menuTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        menuTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(menuTitle, BorderLayout.NORTH);
        
        // Menu buttons panel
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] menuItems = {
            "1. View User Dashboard & Balance",
            "2. Deposit Money to Bank Account",
            "3. Simulate University Semester Billing",
            "4. Simulate Hospital Medical Treatment",
            "5. View Global System Audit Logs (Everyone sees this)",
            "6. Exit Application"
        };
        
        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            menuPanel.add(btn);
        }
        
        panel.add(menuPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showFullDashboard() {
        String password = JOptionPane.showInputDialog(frame, "Enter Admin Password to confirm Identity:");
        
        addToProcessDisplay("", "info");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("         MAIN SYSTEM MENU", "accent");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("Select an option (1-6): 1", "info");
        addToProcessDisplay("Enter Admin Password to confirm Identity: ****", "info");
        
        if (password != null && password.equals(systemPass)) {
            addToProcessDisplay("", "info");
            addToProcessDisplay("=================================", "info");
            addToProcessDisplay("      CURRENT USER PROFILE       ", "accent");
            addToProcessDisplay("=================================", "info");
            addToProcessDisplay("ID:        " + activeClient.getId(), "info");
            addToProcessDisplay("Name:      " + activeClient.getName(), "info");
            addToProcessDisplay("Email:     " + activeClient.getEmail(), "info");
            addToProcessDisplay("Role Type: " + activeClient.getClientType(), "info");
            addToProcessDisplay("=================================", "info");
            addToProcessDisplay("Current Account Balance: $" + activeAccount.getBalance(), "success");
            addToProcessDisplay("", "info");
            
            addToProcessDisplay("[DASHBOARD] User profile displayed successfully", "success");
        } else {
            addToProcessDisplay("[ACCESS DENIED] Incorrect password verification.", "error");
            addToProcessDisplay("[ALERT] Unauthorized access attempt blocked on Dashboard.", "warning");
            JOptionPane.showMessageDialog(frame, "ACCESS DENIED! Incorrect password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showFullDeposit() {
        String amountStr = JOptionPane.showInputDialog(frame, "Enter deposit amount ($):");
        
        addToProcessDisplay("", "info");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("         MAIN SYSTEM MENU", "accent");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("Select an option (1-6): 2", "info");
        
        if (amountStr != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                addToProcessDisplay("Enter deposit amount ($): " + amount, "info");
                activeAccount.deposit(amount);
                updateBalanceLabel();
                addToProcessDisplay("[BANK] Successfully deposited $" + amount, "success");
                addToProcessDisplay("", "info");
            } catch (NumberFormatException e) {
                addToProcessDisplay("[BANK ERROR] Invalid deposit amount.", "error");
            }
        }
    }
    
    private void showFullUniversityBilling() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(PANEL_COLOR);
        
        JTextField courseField = new JTextField("AI");
        JTextField feeField = new JTextField("2000");
        JTextField tokenField = new JTextField("BANK_SECURE_123");
        
        styleTextField(courseField);
        styleTextField(feeField);
        styleTextField(tokenField);
        
        panel.add(createStyledLabel("Enter Course Name:"));
        panel.add(courseField);
        panel.add(createStyledLabel("Enter Base Tuition Fee ($):"));
        panel.add(feeField);
        panel.add(createStyledLabel("Enter Network Security Token (Hint: BANK_SECURE_123):"));
        panel.add(tokenField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "University Semester Billing", 
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        addToProcessDisplay("", "info");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("         MAIN SYSTEM MENU", "accent");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("Select an option (1-6): 3", "info");
        
        if (result == JOptionPane.OK_OPTION) {
            String course = courseField.getText();
            double fee;
            try {
                fee = Double.parseDouble(feeField.getText());
            } catch (NumberFormatException e) {
                addToProcessDisplay("[ERROR] Invalid fee amount", "error");
                return;
            }
            String token = tokenField.getText();
            
            addToProcessDisplay("Enter Course Name: " + course, "info");
            addToProcessDisplay("Enter Base Tuition Fee ($): " + fee, "info");
            addToProcessDisplay("Enter Network Security Token (Hint: BANK_SECURE_123): " + token, "info");
            addToProcessDisplay("", "info");
            
            addToProcessDisplay("[SYSTEM] Intercepting transaction: University Tuition Fee for [" + course + "]", "info");
            
            if (!token.equals("BANK_SECURE_123")) {
                addToProcessDisplay("[SYSTEM ERROR] Security Authorization Failed! Transaction Blocked.", "error");
                addToProcessDisplay("[SECURITY CRITICAL] Auth Failure: Invalid token", "error");
                return;
            }
            
            double finalAmount = fee;
            if (activeClient.getClientType().equals("STUDENT")) {
                addToProcessDisplay("[CROSS-DOMAIN] Verification: Active Student ID. Applying 15% Network Discount.", "success");
                finalAmount = fee * 0.85;
            }
            
            addToProcessDisplay("[SYSTEM] Net Invoice Value: $" + finalAmount, "info");
            
            if (activeAccount.withdraw(finalAmount)) {
                addToProcessDisplay("[BANK] Successfully withdrew $" + finalAmount, "success");
                addToProcessDisplay("[SYSTEM SUCCESS] Invoice settled via direct ledger update.", "success");
                addToProcessDisplay("[BILLING SUCCESS] " + activeClient.getName() + " paid $" + finalAmount + " for University Tuition Fee for [" + course + "]", "success");
                updateBalanceLabel();
            } else {
                addToProcessDisplay("[BANK ERROR] Transaction Failed: Insufficient funds.", "error");
                addToProcessDisplay("[SYSTEM ERROR] Settlement failed due to funding restrictions.", "error");
                addToProcessDisplay("[BILLING DECLINED] Insufficient funds for " + activeClient.getName() + " on charge: $" + finalAmount, "error");
            }
            addToProcessDisplay("", "info");
        }
    }
    
    private void showFullHospitalBilling() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBackground(PANEL_COLOR);
        
        JTextField diagnosisField = new JTextField("heartproblem");
        JTextField treatmentField = new JTextField("2000");
        JCheckBox includeMedsCheck = new JCheckBox("true");
        includeMedsCheck.setText("Include Pharmacy Medicines?");
        includeMedsCheck.setBackground(PANEL_COLOR);
        includeMedsCheck.setForeground(TEXT_COLOR);
        JTextField medicineField = new JTextField("2000");
        JTextField tokenField = new JTextField("BANK_SECURE_123");
        
        styleTextField(diagnosisField);
        styleTextField(treatmentField);
        styleTextField(medicineField);
        styleTextField(tokenField);
        
        medicineField.setEnabled(true);
        
        panel.add(createStyledLabel("Enter Diagnosis Details:"));
        panel.add(diagnosisField);
        panel.add(createStyledLabel("Enter Base Treatment Cost ($):"));
        panel.add(treatmentField);
        panel.add(createStyledLabel("Include Pharmacy Medicines? (true/false):"));
        panel.add(includeMedsCheck);
        panel.add(createStyledLabel("Enter Medicine Cost ($):"));
        panel.add(medicineField);
        panel.add(createStyledLabel("Enter Network Security Token (Hint: BANK_SECURE_123):"));
        panel.add(tokenField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Hospital Medical Treatment", 
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        addToProcessDisplay("", "info");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("         MAIN SYSTEM MENU", "accent");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("Select an option (1-6): 4", "info");
        
        if (result == JOptionPane.OK_OPTION) {
            String diagnosis = diagnosisField.getText();
            double treatment;
            try {
                treatment = Double.parseDouble(treatmentField.getText());
            } catch (NumberFormatException e) {
                addToProcessDisplay("[ERROR] Invalid treatment cost", "error");
                return;
            }
            boolean includeMeds = includeMedsCheck.isSelected();
            double medicine = 0;
            if (includeMeds) {
                try {
                    medicine = Double.parseDouble(medicineField.getText());
                } catch (NumberFormatException e) {
                    addToProcessDisplay("[ERROR] Invalid medicine cost", "error");
                    return;
                }
            }
            String token = tokenField.getText();
            
            addToProcessDisplay("Enter Diagnosis Details: " + diagnosis, "info");
            addToProcessDisplay("Enter Base Treatment Cost ($): " + treatment, "info");
            addToProcessDisplay("Include Pharmacy Medicines? (true/false): " + includeMeds, "info");
            if (includeMeds) {
                addToProcessDisplay("Enter Medicine Cost ($): " + medicine, "info");
            }
            addToProcessDisplay("Enter Network Security Token (Hint: BANK_SECURE_123): " + token, "info");
            addToProcessDisplay("", "info");
            
            addToProcessDisplay("[SYSTEM] Intercepting transaction: Hospital Medical Treatment for [" + diagnosis + "]", "info");
            
            if (!token.equals("BANK_SECURE_123")) {
                addToProcessDisplay("[SYSTEM ERROR] Security Authorization Failed! Transaction Blocked.", "error");
                return;
            }
            
            double totalBase = treatment + medicine;
            double finalAmount = totalBase;
            
            if (activeClient.getClientType().equals("STUDENT")) {
                addToProcessDisplay("[CROSS-DOMAIN] Verification: Active Student ID. Applying 15% Network Discount.", "success");
                finalAmount = totalBase * 0.85;
            }
            
            addToProcessDisplay("[SYSTEM] Net Invoice Value: $" + finalAmount, "info");
            
            if (activeAccount.withdraw(finalAmount)) {
                addToProcessDisplay("[BANK] Successfully withdrew $" + finalAmount, "success");
                addToProcessDisplay("[SYSTEM SUCCESS] Invoice settled via direct ledger update.", "success");
                addToProcessDisplay("[BILLING SUCCESS] " + activeClient.getName() + " paid $" + finalAmount + " for Hospital Medical Treatment for [" + diagnosis + "]", "success");
                updateBalanceLabel();
            } else {
                addToProcessDisplay("[BANK ERROR] Transaction Failed: Insufficient funds.", "error");
                addToProcessDisplay("[SYSTEM ERROR] Settlement failed due to funding restrictions.", "error");
                addToProcessDisplay("[BILLING DECLINED] Insufficient funds for " + activeClient.getName() + " on charge: $" + finalAmount, "error");
            }
            addToProcessDisplay("", "info");
        }
    }
    
    private void showFullAuditLogs() {
        addToProcessDisplay("", "info");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("         MAIN SYSTEM MENU", "accent");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("Select an option (1-6): 5", "info");
        addToProcessDisplay("", "info");
        
        addToProcessDisplay("==================================================", "accent");
        addToProcessDisplay("       GLOBAL CENTRAL ENTERPRISE AUDIT TRAIL      ", "accent");
        addToProcessDisplay("==================================================", "accent");
        
        if (auditLogs.isEmpty()) {
            addToProcessDisplay(" Ledger clean. No transactions have been verified yet.", "info");
        } else {
            for (String log : auditLogs) {
                addToProcessDisplay(log, "info");
            }
        }
        addToProcessDisplay("==================================================", "accent");
        addToProcessDisplay("", "info");
    }
    
    private void exitApplication() {
        addToProcessDisplay("", "info");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("         MAIN SYSTEM MENU", "accent");
        addToProcessDisplay("=================================", "info");
        addToProcessDisplay("Select an option (1-6): 6", "info");
        addToProcessDisplay("", "info");
        addToProcessDisplay("Session closed safely. Data erased from memory runtime.", "warning");
        
        JOptionPane.showMessageDialog(frame, "Session closed safely. Data erased from memory runtime.", 
                                        "Exit", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
    
    // Styling helper methods
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        styleTextField(field);
        return field;
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(13, 17, 28));
        field.setForeground(ACCENT_COLOR);
        field.setCaretColor(ACCENT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(42, 47, 66)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setBackground(new Color(13, 17, 28));
        field.setForeground(ACCENT_COLOR);
        field.setCaretColor(ACCENT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(42, 47, 66)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return field;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(26, 31, 46));
        button.setForeground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        button.setFont(new Font("Monospaced", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
                button.setForeground(BG_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(26, 31, 46));
                button.setForeground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(13, 17, 28));
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(new Color(42, 47, 66)));
        button.setFont(new Font("Monospaced", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(26, 31, 46));
                button.setForeground(ACCENT_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(13, 17, 28));
                button.setForeground(TEXT_COLOR);
            }
        });
        
        if (text.contains("1.")) {
            button.addActionListener(e -> showFullDashboard());
        } else if (text.contains("2.")) {
            button.addActionListener(e -> showFullDeposit());
        } else if (text.contains("3.")) {
            button.addActionListener(e -> showFullUniversityBilling());
        } else if (text.contains("4.")) {
            button.addActionListener(e -> showFullHospitalBilling());
        } else if (text.contains("5.")) {
            button.addActionListener(e -> showFullAuditLogs());
        } else if (text.contains("6.")) {
            button.addActionListener(e -> exitApplication());
        }
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HealthcareFinanceGUI();
        });
    }
}

// Backend classes (simplified for GUI)
class Client {
    private String id, name, email, clientType;
    public Client(String id, String name, String email, String clientType) {
        this.id = id; this.name = name; this.email = email; this.clientType = clientType;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getClientType() { return clientType; }
}

class SavingsAccount {
    private String accountNumber;
    private double balance;
    
    public SavingsAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
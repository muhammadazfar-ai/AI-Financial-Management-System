import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

// ======================================================
// INTERFACES
// ======================================================

interface Billable {
    double generateInvoice();
    String getBillDetails();
}

interface SecureTransactable {
    boolean authorizeTransaction(String token);
}

// ======================================================
// ABSTRACT CLASSES
// ======================================================

abstract class User {
    private String id;
    private String name;
    private String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public abstract String getDisplayInfo();
}

abstract class BankAccount implements SecureTransactable {
    private String accountNumber;
    protected double balance;

    public BankAccount(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);
}

// ======================================================
// USER CLASS
// ======================================================

class Client extends User {
    private String clientType;

    public Client(String id, String name, String email, String clientType) {
        super(id, name, email);
        this.clientType = clientType.toUpperCase();
    }

    public String getClientType() { return clientType; }

    @Override
    public String getDisplayInfo() {
        return String.format("ID: %s | Name: %s | Email: %s | Type: %s", 
                           getId(), getName(), getEmail(), clientType);
    }
}

// ======================================================
// BANK ACCOUNT
// ======================================================

class SavingsAccount extends BankAccount {
    private static final String SECRET_TOKEN = "BANK_SECURE_123";

    public SavingsAccount(String accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            AuditLogger.logEvent("[BANK] Deposit Added: $" + amount);
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            AuditLogger.logEvent("[BANK] Withdrawal: $" + amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean authorizeTransaction(String token) {
        return SECRET_TOKEN.equals(token);
    }
}

// ======================================================
// BILLING CLASSES
// ======================================================

class UniversityCourse implements Billable {
    private String courseName;
    private double tuitionFee, booksFee, transportFee, hostelFee;

    public UniversityCourse(String courseName, double tuitionFee, double booksFee, 
                            double transportFee, double hostelFee) {
        this.courseName = courseName;
        this.tuitionFee = tuitionFee;
        this.booksFee = booksFee;
        this.transportFee = transportFee;
        this.hostelFee = hostelFee;
    }

    @Override
    public double generateInvoice() {
        return tuitionFee + booksFee + transportFee + hostelFee;
    }

    @Override
    public String getBillDetails() {
        return "University: " + courseName;
    }
}

class MedicalRecord implements Billable {
    private String disease;
    private double treatmentCost, medicineCost, emergencyCost;

    public MedicalRecord(String disease, double treatmentCost, double medicineCost, double emergencyCost) {
        this.disease = disease;
        this.treatmentCost = treatmentCost;
        this.medicineCost = medicineCost;
        this.emergencyCost = emergencyCost;
    }

    @Override
    public double generateInvoice() {
        return treatmentCost + medicineCost + emergencyCost;
    }

    @Override
    public String getBillDetails() {
        return "Medical: " + disease;
    }
}

// ======================================================
// EXPENSE CLASS
// ======================================================

class Expense {
    private String category;
    private double amount;
    private LocalDateTime date;

    public Expense(String category, double amount) {
        this.category = category;
        this.amount = amount;
        this.date = LocalDateTime.now();
    }

    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}

// ======================================================
// AUDIT LOGGER
// ======================================================

class AuditLogger {
    private static List<String> auditLogs = new ArrayList<>();

    public static void logEvent(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        auditLogs.add("[" + timestamp + "] " + event);
    }

    public static List<String> getLogs() {
        return new ArrayList<>(auditLogs);
    }

    public static void clearLogs() {
        auditLogs.clear();
    }
}

// ======================================================
// AI EXPENSE ANALYZER
// ======================================================

class AIExpenseAnalyzer {
    private List<Expense> expenses;

    public AIExpenseAnalyzer() {
        expenses = new ArrayList<>();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        AuditLogger.logEvent("[EXPENSE] Added: " + expense.getCategory() + " - $" + expense.getAmount());
    }

    public double calculateTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> categoryAnalysis() {
        Map<String, Double> categoryMap = new HashMap<>();
        for (Expense e : expenses) {
            categoryMap.put(e.getCategory(), 
                           categoryMap.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }
        return categoryMap;
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public String generateAIReport() {
        StringBuilder report = new StringBuilder();
        double total = calculateTotalExpenses();
        
        if (total == 0) {
            return "No expenses recorded yet. Add some expenses to get AI analysis!";
        }
        
        report.append("💰 TOTAL EXPENSES: $").append(String.format("%.2f", total)).append("\n\n");
        report.append("📊 CATEGORY BREAKDOWN:\n");
        
        Map<String, Double> analysis = categoryAnalysis();
        for (Map.Entry<String, Double> entry : analysis.entrySet()) {
            double percentage = (entry.getValue() / total) * 100;
            report.append(String.format("• %s: $%.2f (%.1f%%)\n", 
                          entry.getKey(), entry.getValue(), percentage));
        }
        
        report.append("\n🤖 AI SUGGESTIONS:\n");
        double totalSavings = 0;
        
        for (Map.Entry<String, Double> entry : analysis.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            
            if (category.equalsIgnoreCase("Food") && amount > 700) {
                double save = amount * 0.3;
                totalSavings += save;
                report.append(String.format("• [FOOD] High spending! Save up to $%.2f by cooking at home\n", save));
            }
            if (category.equalsIgnoreCase("Entertainment") && amount > 500) {
                double save = amount * 0.4;
                totalSavings += save;
                report.append(String.format("• [ENTERTAINMENT] Reduce subscriptions. Save up to $%.2f\n", save));
            }
            if (category.equalsIgnoreCase("Shopping") && amount > 600) {
                double save = amount * 0.35;
                totalSavings += save;
                report.append(String.format("• [SHOPPING] Control impulse buying. Save up to $%.2f\n", save));
            }
            if (category.equalsIgnoreCase("Medical") && amount > 800) {
                double save = amount * 0.15;
                totalSavings += save;
                report.append(String.format("• [MEDICAL] Consider insurance. Save up to $%.2f\n", save));
            }
            if (category.equalsIgnoreCase("University") && amount > 2000) {
                double save = amount * 0.1;
                totalSavings += save;
                report.append(String.format("• [UNIVERSITY] Use digital resources. Save up to $%.2f\n", save));
            }
        }
        
        if (totalSavings > 0) {
            report.append(String.format("\n💡 TOTAL POTENTIAL SAVINGS: $%.2f\n", totalSavings));
        } else {
            report.append("\n✅ Great job! Your spending is under control.\n");
        }
        
        double predicted = total * 1.12;
        report.append(String.format("\n📈 PREDICTED NEXT MONTH: $%.2f\n", predicted));
        
        return report.toString();
    }
}

// ======================================================
// ENTERPRISE NETWORK
// ======================================================

class EnterpriseNetwork {
    public boolean processBilling(Client client, BankAccount account, Billable bill, String token) {
        if (!account.authorizeTransaction(token)) {
            AuditLogger.logEvent("[SECURITY] Invalid Token for " + client.getName());
            return false;
        }

        double amount = bill.generateInvoice();
        
        if (client.getClientType().equalsIgnoreCase("STUDENT")) {
            amount *= 0.85;
            AuditLogger.logEvent("[DISCOUNT] Student discount applied");
        }

        boolean success = account.withdraw(amount);
        
        if (success) {
            AuditLogger.logEvent("[PAYMENT] " + client.getName() + " paid $" + amount + " for " + bill.getBillDetails());
        } else {
            AuditLogger.logEvent("[FAILED] Insufficient funds for " + client.getName());
        }
        
        return success;
    }
}

// ======================================================
// CUSTOM UI COMPONENTS
// ======================================================

class GradientPanel extends JPanel {
    private Color startColor;
    private Color endColor;
    
    public GradientPanel(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}

class RoundedButton extends JButton {
    private Color bgColor;
    
    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 20, 10, 20));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2.setColor(bgColor.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(bgColor.brighter());
        } else {
            g2.setColor(bgColor);
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}

// ======================================================
// MAIN GUI APPLICATION
// ======================================================

public class app extends JFrame {
    private Client client;
    private SavingsAccount account;
    private EnterpriseNetwork network;
    private AIExpenseAnalyzer aiAnalyzer;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel balanceLabel;
    private JTextArea reportArea;
    private DefaultTableModel expenseTableModel;
    private JTable expenseTable;
    private JTextArea auditArea;
    
    // Modern Color Scheme
    private final Color PRIMARY = new Color(79, 70, 229);
    private final Color SECONDARY = new Color(139, 92, 246);
    private final Color SUCCESS = new Color(34, 197, 94);
    private final Color DANGER = new Color(239, 68, 68);
    private final Color WARNING = new Color(245, 158, 11);
    private final Color BG_COLOR = new Color(249, 250, 251);
    private final Color CARD_BG = Color.WHITE;
    private final Color SIDEBAR_BG = new Color(31, 41, 55);
    
    public app() {

    initializeSystem();

    if(client != null && account != null){
        setupUI();
    }
}
    
    private void initializeSystem() {
        network = new EnterpriseNetwork();
        aiAnalyzer = new AIExpenseAnalyzer();
        showRegistrationDialog();
    }
    
    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Registration", true);
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header with gradient
        GradientPanel headerPanel = new GradientPanel(PRIMARY, SECONDARY);
        headerPanel.setPreferredSize(new Dimension(500, 80));
        headerPanel.setLayout(new GridBagLayout());
        JLabel titleLabel = new JLabel("CREATE NEW ACCOUNT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1 - Client ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("Client ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(new Color(55, 65, 81));
        formPanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        JTextField idField = new JTextField(20);
        idField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        formPanel.add(idField, gbc);
        
        // Row 2 - Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(55, 65, 81));
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        formPanel.add(nameField, gbc);
        
        // Row 3 - Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        emailLabel.setForeground(new Color(55, 65, 81));
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        formPanel.add(emailField, gbc);
        
        // Row 4 - Client Type
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel typeLabel = new JLabel("Client Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(new Color(55, 65, 81));
        formPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"STUDENT", "PATIENT"});
        typeCombo.setBackground(Color.WHITE);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        formPanel.add(typeCombo, gbc);
        
        // Row 5 - Initial Deposit
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel depositLabel = new JLabel("Initial Deposit ($):");
        depositLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        depositLabel.setForeground(new Color(55, 65, 81));
        formPanel.add(depositLabel, gbc);
        
        gbc.gridx = 1;
        JTextField depositField = new JTextField(20);
        depositField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        formPanel.add(depositField, gbc);
        
        // Row 6 - Admin Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Admin Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(55, 65, 81));
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        formPanel.add(passwordField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        
        RoundedButton registerBtn = new RoundedButton("REGISTER", PRIMARY);
        registerBtn.setPreferredSize(new Dimension(200, 45));
        registerBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String password = new String(passwordField.getPassword());
            
            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double deposit;
            try {
                deposit = Double.parseDouble(depositField.getText().trim());
                if (deposit < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid deposit amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            client = new Client(id, name, email, type);
            account = new SavingsAccount("ACC-" + System.currentTimeMillis(), deposit);
            AuditLogger.logEvent("System Started - User: " + client.getName());
            dialog.dispose();
        });
        
        buttonPanel.add(registerBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void setupUI() {
        setTitle("AI Financial Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setBackground(BG_COLOR);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        
        // Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        // Sidebar and Content
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_COLOR);
        
        contentPanel.add(createDashboard(), "Dashboard");
        contentPanel.add(createBankingPanel(), "Banking");
        contentPanel.add(createUniversityPanel(), "University");
        contentPanel.add(createMedicalPanel(), "Medical");
        contentPanel.add(createExpensePanel(), "Expenses");
        contentPanel.add(createAIPanel(), "AI");
        contentPanel.add(createAuditPanel(), "Audit");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeader() {
        GradientPanel header = new GradientPanel(PRIMARY, SECONDARY);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("💰 AI Financial Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        header.add(title, BorderLayout.WEST);
        
        if (client != null) {
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            infoPanel.setOpaque(false);
            
            JLabel userLabel = new JLabel("👤 " + client.getName());
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            userLabel.setForeground(Color.WHITE);
            
            JLabel typeLabel = new JLabel("📋 " + client.getClientType());
            typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            typeLabel.setForeground(Color.WHITE);
            
            infoPanel.add(userLabel);
            infoPanel.add(Box.createHorizontalStrut(20));
            infoPanel.add(typeLabel);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
            header.add(infoPanel, BorderLayout.EAST);
        }
        
        return header;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.gridx = 0;
        
        String[] menuItems = {"📊 Dashboard", "💰 Banking", "🎓 University", "🏥 Medical", "📝 Expenses", "🤖 AI Report", "📜 Audit Logs"};
        String[] panels = {"Dashboard", "Banking", "University", "Medical", "Expenses", "AI", "Audit"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuBtn = createSidebarButton(menuItems[i]);
            final String panelName = panels[i];
            menuBtn.addActionListener(e -> {
                cardLayout.show(contentPanel, panelName);
                refreshCurrentPanel(panelName);
            });
            gbc.gridy = i;
            sidebar.add(menuBtn, gbc);
        }
        
        // Logout button
        gbc.gridy = 10;
        gbc.insets = new Insets(50, 15, 20, 15);
        JButton logoutBtn = createSidebarButton("🚪 Logout");
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        sidebar.add(logoutBtn, gbc);
        
        return sidebar;
    }
    
  private JButton createSidebarButton(String text) {

    JButton btn = new JButton(text);

    // Basic Style
    btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
    btn.setForeground(new Color(240, 240, 240));
    btn.setBackground(new Color(31, 41, 55));
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);
    btn.setOpaque(true);

    // Size & Alignment
    btn.setPreferredSize(new Dimension(220, 50));
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // Rounded Padding
    btn.setBorder(BorderFactory.createCompoundBorder(
        new EmptyBorder(5, 10, 5, 10),
        BorderFactory.createEmptyBorder(12, 20, 12, 20)
    ));

    // Hover + Click Effect
    btn.addMouseListener(new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent e) {
            btn.setBackground(new Color(79, 70, 229)); // Purple hover
            btn.setForeground(Color.WHITE);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            btn.setBackground(new Color(31, 41, 55)); // Original sidebar color
            btn.setForeground(new Color(240, 240, 240));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            btn.setBackground(new Color(99, 102, 241)); // Click effect
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            btn.setBackground(new Color(79, 70, 229));
        }
    });

    return btn;
}  
    private void refreshCurrentPanel(String panelName) {
        switch (panelName) {
            case "Banking":
                refreshBalance();
                break;
            case "Expenses":
                refreshExpenseTable();
                break;
            case "AI":
                refreshAIReport();
                break;
            case "Audit":
                refreshAuditLogs();
                break;
        }
    }
    
    private JPanel createDashboard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(BG_COLOR);
        
        statsPanel.add(createStatCard("💰 Balance", "$" + String.format("%.2f", account.getBalance()), SUCCESS));
        statsPanel.add(createStatCard("📊 Expenses", "$" + String.format("%.2f", aiAnalyzer.calculateTotalExpenses()), PRIMARY));
        statsPanel.add(createStatCard("👤 Type", client.getClientType(), WARNING));
        statsPanel.add(createStatCard("🏦 Account", account.getAccountNumber(), SECONDARY));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(statsPanel, gbc);
        
        // User Info Card
        gbc.gridy = 1;
        JPanel infoCard = createInfoCard();
        panel.add(infoCard, gbc);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createInfoCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addInfoRow(card, gbc, "Client ID:", client.getId(), 0);
        addInfoRow(card, gbc, "Full Name:", client.getName(), 1);
        addInfoRow(card, gbc, "Email:", client.getEmail(), 2);
        addInfoRow(card, gbc, "Client Type:", client.getClientType(), 3);
        addInfoRow(card, gbc, "Account Number:", account.getAccountNumber(), 4);
        
        return card;
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, String label, String value, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(new Color(75, 85, 99));
        panel.add(val, gbc);
    }
    
    private JPanel createBankingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Balance Card
        JPanel balanceCard = new JPanel(new BorderLayout());
        balanceCard.setBackground(CARD_BG);
        balanceCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        JLabel balanceTitle = new JLabel("Current Balance");
        balanceTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        balanceTitle.setForeground(new Color(107, 114, 128));
        balanceTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        balanceLabel = new JLabel("$" + String.format("%.2f", account.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        balanceLabel.setForeground(PRIMARY);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        balanceCard.add(balanceTitle, BorderLayout.NORTH);
        balanceCard.add(balanceLabel, BorderLayout.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(balanceCard, gbc);
        
        // Deposit Panel
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(createTransactionPanel("Deposit Money", SUCCESS, true), gbc);
        
        // Withdraw Panel
        gbc.gridx = 1;
        panel.add(createTransactionPanel("Withdraw Money", DANGER, false), gbc);
        
        return panel;
    }
    
    private JPanel createTransactionPanel(String title, Color color, boolean isDeposit) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(CARD_BG);
        
        JTextField amountField = new JTextField(15);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        RoundedButton actionBtn = new RoundedButton(isDeposit ? "Deposit" : "Withdraw", color);
        actionBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) throw new NumberFormatException();
                
                if (isDeposit) {
                    account.deposit(amount);
                    JOptionPane.showMessageDialog(this, "✅ Successfully deposited $" + amount);
                } else {
                    if (account.withdraw(amount)) {
                        JOptionPane.showMessageDialog(this, "✅ Successfully withdrew $" + amount);
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                refreshBalance();
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        inputPanel.add(amountField);
        inputPanel.add(actionBtn);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUniversityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JPanel card = createBillingCard("🎓 University Billing", PRIMARY);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 10, 10, 10);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Course
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        JLabel courseLabel = new JLabel("Course Name:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(courseLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField courseField = new JTextField(20);
        courseField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(courseField, formGbc);
        
        // Tuition Fee
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        JLabel tuitionLabel = new JLabel("Tuition Fee ($):");
        tuitionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(tuitionLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField tuitionField = new JTextField(20);
        tuitionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(tuitionField, formGbc);
        
        // Books Fee
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        JLabel booksLabel = new JLabel("Books Fee ($):");
        booksLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(booksLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField booksField = new JTextField(20);
        booksField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(booksField, formGbc);
        
        // Transport Fee
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        JLabel transportLabel = new JLabel("Transport Fee ($):");
        transportLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(transportLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField transportField = new JTextField(20);
        transportField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(transportField, formGbc);
        
        // Hostel Fee
        formGbc.gridx = 0;
        formGbc.gridy = 4;
        JLabel hostelLabel = new JLabel("Hostel Fee ($):");
        hostelLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(hostelLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField hostelField = new JTextField(20);
        hostelField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(hostelField, formGbc);
        
        card.add(formPanel, BorderLayout.CENTER);
        
        RoundedButton processBtn = new RoundedButton("Process Payment", PRIMARY);
        processBtn.addActionListener(e -> {
            try {
                String course = courseField.getText().trim();
                double tuition = Double.parseDouble(tuitionField.getText().trim());
                double books = Double.parseDouble(booksField.getText().trim());
                double transport = Double.parseDouble(transportField.getText().trim());
                double hostel = Double.parseDouble(hostelField.getText().trim());
                
                if (course.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter course name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String token = JOptionPane.showInputDialog(this, "Enter Security Token:");
                if (token == null || !token.equals("BANK_SECURE_123")) {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Security Token!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Billable bill = new UniversityCourse(course, tuition, books, transport, hostel);
                boolean success = network.processBilling(client, account, bill, token);
                
                if (success) {
                    aiAnalyzer.addExpense(new Expense("University", bill.generateInvoice()));
                    JOptionPane.showMessageDialog(this, "✅ Payment processed successfully!\nTotal: $" + bill.generateInvoice());
                    refreshBalance();
                    courseField.setText("");
                    tuitionField.setText("");
                    booksField.setText("");
                    transportField.setText("");
                    hostelField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Payment failed! Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid amounts!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        card.add(processBtn, BorderLayout.SOUTH);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(card, gbc);
        
        return panel;
    }
    
    private JPanel createMedicalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        JPanel card = createBillingCard("🏥 Medical Billing", SECONDARY);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 10, 10, 10);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Disease
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        JLabel diseaseLabel = new JLabel("Disease:");
        diseaseLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(diseaseLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField diseaseField = new JTextField(20);
        diseaseField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(diseaseField, formGbc);
        
        // Treatment Cost
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        JLabel treatmentLabel = new JLabel("Treatment Cost ($):");
        treatmentLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(treatmentLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField treatmentField = new JTextField(20);
        treatmentField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(treatmentField, formGbc);
        
        // Medicine Cost
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        JLabel medicineLabel = new JLabel("Medicine Cost ($):");
        medicineLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(medicineLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField medicineField = new JTextField(20);
        medicineField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(medicineField, formGbc);
        
        // Emergency Cost
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        JLabel emergencyLabel = new JLabel("Emergency Cost ($):");
        emergencyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(emergencyLabel, formGbc);
        
        formGbc.gridx = 1;
        JTextField emergencyField = new JTextField(20);
        emergencyField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(emergencyField, formGbc);
        
        card.add(formPanel, BorderLayout.CENTER);
        
        RoundedButton processBtn = new RoundedButton("Process Payment", SECONDARY);
        processBtn.addActionListener(e -> {
            try {
                String disease = diseaseField.getText().trim();
                double treatment = Double.parseDouble(treatmentField.getText().trim());
                double medicine = Double.parseDouble(medicineField.getText().trim());
                double emergency = Double.parseDouble(emergencyField.getText().trim());
                
                if (disease.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter disease name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String token = JOptionPane.showInputDialog(this, "Enter Security Token:");
                if (token == null || !token.equals("BANK_SECURE_123")) {
                    JOptionPane.showMessageDialog(this, "❌ Invalid Security Token!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Billable bill = new MedicalRecord(disease, treatment, medicine, emergency);
                boolean success = network.processBilling(client, account, bill, token);
                
                if (success) {
                    aiAnalyzer.addExpense(new Expense("Medical", bill.generateInvoice()));
                    JOptionPane.showMessageDialog(this, "✅ Payment processed successfully!\nTotal: $" + bill.generateInvoice());
                    refreshBalance();
                    diseaseField.setText("");
                    treatmentField.setText("");
                    medicineField.setText("");
                    emergencyField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Payment failed! Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid amounts!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        card.add(processBtn, BorderLayout.SOUTH);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(card, gbc);
        
        return panel;
    }
    
    private JPanel createBillingCard(String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        
        return card;
    }
    
    private JPanel createExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top Panel for adding expenses
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(CARD_BG);
        addPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Add New Expense");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        addPanel.add(titleLabel, gbc);
        
        String[] categories = {"Food", "Entertainment", "Shopping", "Medical", "University", "Transport", "Utilities", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JTextField amountField = new JTextField(15);
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addPanel.add(catLabel, gbc);
        
        gbc.gridx = 1;
        addPanel.add(categoryCombo, gbc);
        
        gbc.gridx = 2;
        JLabel amtLabel = new JLabel("Amount ($):");
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addPanel.add(amtLabel, gbc);
        
        gbc.gridx = 3;
        addPanel.add(amountField, gbc);
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        RoundedButton addBtn = new RoundedButton("Add Expense", SUCCESS);
        addBtn.addActionListener(e -> {
            try {
                String category = (String) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) throw new NumberFormatException();
                aiAnalyzer.addExpense(new Expense(category, amount));

                refreshExpenseTable();

                refreshAIReport();

                amountField.setText("");
                JOptionPane.showMessageDialog(this, "✅ Expense added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        addPanel.add(addBtn, gbc);
        
        panel.add(addPanel, BorderLayout.NORTH);
        
        // Expense Table
        String[] columns = {"Date", "Category", "Amount ($)"};
        expenseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        expenseTable.setRowHeight(30);
        expenseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        expenseTable.getTableHeader().setBackground(new Color(243, 244, 246));
        
        // Set column widths
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
   private JPanel createAIPanel(){

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(BG_COLOR);

    // =========================
    // TITLE
    // =========================
    JLabel title = new JLabel("🤖 AI Financial Analysis Dashboard");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setHorizontalAlignment(SwingConstants.CENTER);
    title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    mainPanel.add(title, BorderLayout.NORTH);

    // =========================
    // CENTER PANEL
    // =========================
    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
    centerPanel.setBackground(BG_COLOR);
    centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

    // =========================
    // PIE CHART PANEL
    // =========================
    JPanel chartPanel = new JPanel() {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            Map<String, Double> data = aiAnalyzer.categoryAnalysis();

            if (data.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.drawString("No Expense Data Available", 100, 200);
                return;
            }

            double total = 0;

            for (double value : data.values()) {
                total += value;
            }

            Color[] colors = {
                new Color(79, 70, 229),
                new Color(34, 197, 94),
                new Color(239, 68, 68),
                new Color(245, 158, 11),
                new Color(6, 182, 212),
                new Color(168, 85, 247),
                new Color(236, 72, 153)
            };

            int startAngle = 0;
            int i = 0;

            for (Map.Entry<String, Double> entry : data.entrySet()) {

                int arcAngle = (int) Math.round((entry.getValue() / total) * 360);

                g2.setColor(colors[i % colors.length]);

                g2.fillArc(50, 40, 300, 300, startAngle, arcAngle);

                startAngle += arcAngle;

                i++;
            }

            // LEGEND
            int y = 370;
            i = 0;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));

            for (Map.Entry<String, Double> entry : data.entrySet()) {

                g2.setColor(colors[i % colors.length]);
                g2.fillRect(50, y, 20, 20);

                g2.setColor(Color.BLACK);

                g2.drawString(
                        entry.getKey() + " ($" +
                        String.format("%.2f", entry.getValue()) + ")",
                        80,
                        y + 15
                );

                y += 30;
                i++;
            }
        }
    };

    chartPanel.setBackground(Color.WHITE);
    chartPanel.setBorder(BorderFactory.createTitledBorder("Expense Distribution"));

    // =========================
    // AI REPORT PANEL
    // =========================
    reportArea = new JTextArea();
reportArea.setEditable(false);
reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
reportArea.setLineWrap(true);
reportArea.setWrapStyleWord(true);
reportArea.setBackground(Color.WHITE);

    StringBuilder report = new StringBuilder();

    Map<String, Double> analysis = aiAnalyzer.categoryAnalysis();

    double total = aiAnalyzer.calculateTotalExpenses();

    report.append("=========== AI FINANCIAL REPORT ===========\n\n");

    report.append("💰 Total Expenses: $")
            .append(String.format("%.2f", total))
            .append("\n\n");

    String highestCategory = "";
    double highestAmount = 0;

    for (Map.Entry<String, Double> entry : analysis.entrySet()) {

        double percentage = (entry.getValue() / total) * 100;

        report.append("📌 ")
                .append(entry.getKey())
                .append(" : $")
                .append(String.format("%.2f", entry.getValue()))
                .append(" (")
                .append(String.format("%.1f", percentage))
                .append("%)\n");

        if (entry.getValue() > highestAmount) {
            highestAmount = entry.getValue();
            highestCategory = entry.getKey();
        }
    }

    report.append("\n====================================\n");

    // =========================
    // AI RECOMMENDATIONS
    // =========================
    report.append("\n🤖 AI Recommendations:\n\n");

    if (highestCategory.equalsIgnoreCase("Food")) {
        report.append("• Reduce restaurant spending.\n");
        report.append("• Cooking at home can save 25%-30%.\n");
    }

    if (highestCategory.equalsIgnoreCase("Shopping")) {
        report.append("• Avoid impulse buying.\n");
        report.append("• Create a monthly shopping budget.\n");
    }

    if (highestCategory.equalsIgnoreCase("Entertainment")) {
        report.append("• Reduce unnecessary subscriptions.\n");
    }

    if (highestCategory.equalsIgnoreCase("Medical")) {
        report.append("• Consider health insurance planning.\n");
    }

    if (highestCategory.equalsIgnoreCase("University")) {
        report.append("• Use digital resources to lower costs.\n");
    }

    // =========================
    // FUTURE PREDICTION
    // =========================
    double nextMonth = total * 1.15;

    report.append("\n📈 Future Prediction:\n");
    report.append("Estimated Next Month Expense: $")
            .append(String.format("%.2f", nextMonth))
            .append("\n");

    double savings = total * 0.20;

    report.append("\n💡 Possible Savings Next Month: $")
            .append(String.format("%.2f", savings))
            .append("\n");

    if (total > 5000) {
        report.append("\n⚠ Warning: Your expenses are very high.\n");
    } else {
        report.append("\n✅ Your spending is under reasonable control.\n");
    }

    reportArea.setText(report.toString());

    JScrollPane scrollPane = new JScrollPane(reportArea);

    scrollPane.setBorder(BorderFactory.createTitledBorder("AI Insights"));

    centerPanel.add(chartPanel);
    centerPanel.add(scrollPane);

    mainPanel.add(centerPanel, BorderLayout.CENTER);

    // =========================
    // REFRESH BUTTON
    // =========================
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(BG_COLOR);

    RoundedButton refreshBtn = new RoundedButton("Refresh AI Report", PRIMARY);

    refreshBtn.addActionListener(e -> {
        cardLayout.show(contentPanel, "AI");
    });

    bottomPanel.add(refreshBtn);

    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    return mainPanel;
} 
    private void refreshBalance() {
        if (balanceLabel != null) {
            balanceLabel.setText("$" + String.format("%.2f", account.getBalance()));
        }
    }
    
    private void refreshExpenseTable() {
        expenseTableModel.setRowCount(0);
        for (Expense expense : aiAnalyzer.getExpenses()) {
            expenseTableModel.addRow(new Object[]{
                expense.getFormattedDate(),
                expense.getCategory(),
                String.format("$%.2f", expense.getAmount())
            });
        }
    }
    
    private void refreshAIReport() {

    if(reportArea == null){
        return;
    }

    StringBuilder report = new StringBuilder();

    Map<String, Double> analysis =
            aiAnalyzer.categoryAnalysis();

    double total =
            aiAnalyzer.calculateTotalExpenses();

    report.append("=========== AI FINANCIAL REPORT ===========\n\n");

    report.append("💰 Total Expenses: $")
            .append(String.format("%.2f", total))
            .append("\n\n");

    String highestCategory = "";
    double highestAmount = 0;

    for (Map.Entry<String, Double> entry : analysis.entrySet()) {

        double percentage = 0;

        if(total > 0){
            percentage = (entry.getValue() / total) * 100;
        }

        report.append("📌 ")
                .append(entry.getKey())
                .append(" : $")
                .append(String.format("%.2f", entry.getValue()))
                .append(" (")
                .append(String.format("%.1f", percentage))
                .append("%)\n");

        if(entry.getValue() > highestAmount){
            highestAmount = entry.getValue();
            highestCategory = entry.getKey();
        }
    }

    report.append("\n====================================\n");

    report.append("\n🤖 AI Recommendations:\n\n");

    if(highestCategory.equalsIgnoreCase("Food")){
        report.append("• Reduce restaurant spending.\n");
        report.append("• Cooking at home saves money.\n");
    }

    if(highestCategory.equalsIgnoreCase("Shopping")){
        report.append("• Avoid unnecessary shopping.\n");
        report.append("• Create a shopping budget.\n");
    }

    if(highestCategory.equalsIgnoreCase("Medical")){
        report.append("• Consider health insurance.\n");
    }

    if(highestCategory.equalsIgnoreCase("University")){
        report.append("• Use digital study resources.\n");
    }

    double nextMonth = total * 1.15;

    report.append("\n📈 Future Prediction:\n");

    report.append("Estimated Next Month Expense: $")
            .append(String.format("%.2f", nextMonth))
            .append("\n");

    double savings = total * 0.20;

    report.append("\n💡 Possible Savings Next Month: $")
            .append(String.format("%.2f", savings))
            .append("\n");

    reportArea.setText(report.toString());

    reportArea.repaint();
}
    
    private void refreshAuditLogs() {
        StringBuilder logs = new StringBuilder();
        for (String log : AuditLogger.getLogs()) {
            logs.append(log).append("\n");
        }
        if (logs.length() == 0) {
            logs.append("No audit logs available.");
        }
        auditArea.setText(logs.toString());
    }
    
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application
        SwingUtilities.invokeLater(() -> {
            app application = new app();
            application.setVisible(true);
        });
    }

    private JPanel createAuditPanel() {

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(BG_COLOR);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel title = new JLabel("📜 Audit Logs");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(PRIMARY);

    panel.add(title, BorderLayout.NORTH);

    auditArea = new JTextArea();

    auditArea.setEditable(false);
    auditArea.setFont(new Font("Consolas", Font.PLAIN, 14));
    auditArea.setBackground(Color.WHITE);
    auditArea.setForeground(Color.BLACK);

    JScrollPane scrollPane = new JScrollPane(auditArea);

    scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    panel.add(scrollPane, BorderLayout.CENTER);

    // Buttons Panel
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(BG_COLOR);

    RoundedButton refreshBtn =
            new RoundedButton("Refresh Logs", PRIMARY);

    refreshBtn.addActionListener(e -> {
        refreshAuditLogs();
    });

    RoundedButton clearBtn =
            new RoundedButton("Clear Logs", DANGER);

    clearBtn.addActionListener(e -> {

        AuditLogger.clearLogs();

        refreshAuditLogs();

        JOptionPane.showMessageDialog(
                this,
                "Logs cleared successfully!"
        );
    });

    bottomPanel.add(refreshBtn);
    bottomPanel.add(clearBtn);

    panel.add(bottomPanel, BorderLayout.SOUTH);

    refreshAuditLogs();

    return panel;
}
}
    
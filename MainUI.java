import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

// ===============================
// MAIN GUI APPLICATION
// ===============================
public class MainUI {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField userField;
    private JPasswordField passField;

    private Client client;
    private SavingsAccount account;
    private EnterpriseNetwork network = new EnterpriseNetwork();
    private AIExpenseAnalyzer aiAnalyzer = new AIExpenseAnalyzer();

    private String adminUser = "admin";
    private String adminPass = "1234";

    public MainUI() {
        initLoginScreen();
    }

    // ===============================
    // LOGIN SCREEN
    // ===============================
    private void initLoginScreen() {

        frame = new JFrame("AI Financial System");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        Color bg = new Color(15, 23, 42);   // dark navy
        Color panel = new Color(30, 41, 59); // dark blue
        Color accent = new Color(59, 130, 246); // blue

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(bg);
        loginPanel.setLayout(null);

        JLabel title = new JLabel("AI FINANCIAL SYSTEM LOGIN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(260, 60, 400, 40);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(300, 150, 100, 30);

        userField = new JTextField();
        userField.setBounds(380, 150, 200, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(300, 200, 100, 30);

        passField = new JPasswordField();
        passField.setBounds(380, 200, 200, 30);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(380, 260, 200, 40);
        loginBtn.setBackground(accent);
        loginBtn.setForeground(Color.WHITE);

        loginBtn.addActionListener(e -> handleLogin());

        loginPanel.add(title);
        loginPanel.add(userLabel);
        loginPanel.add(userField);
        loginPanel.add(passLabel);
        loginPanel.add(passField);
        loginPanel.add(loginBtn);

        frame.add(loginPanel);
        frame.setVisible(true);
    }

    // ===============================
    // LOGIN LOGIC
    // ===============================
    private void handleLogin() {

        String u = userField.getText();
        String p = new String(passField.getPassword());

        if (u.equals(adminUser) && p.equals(adminPass)) {
            JOptionPane.showMessageDialog(frame, "Admin Login Success");
            initDashboard();
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid Login");
        }
    }

    // ===============================
    // DASHBOARD
    // ===============================
    private void initDashboard() {

        frame.getContentPane().removeAll();

        Color bg = new Color(15, 23, 42);
        Color accent = new Color(59, 130, 246);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(bg);

        JLabel title = new JLabel("DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(380, 20, 200, 30);

        JButton bankBtn = createButton("Banking", 100, 100);
        JButton billBtn = createButton("Billing", 100, 170);
        JButton aiBtn = createButton("AI Report", 100, 240);
        JButton auditBtn = createButton("Audit Logs", 100, 310);

        JTextArea output = new JTextArea();
        output.setBounds(350, 100, 480, 350);
        output.setBackground(new Color(30, 41, 59));
        output.setForeground(Color.WHITE);

        bankBtn.addActionListener(e -> openBanking(output));
        billBtn.addActionListener(e -> openBilling(output));
        aiBtn.addActionListener(e -> output.setText(aiAnalyzer.generateAIReportString()));
        auditBtn.addActionListener(e -> output.setText("Audit Logs:\n" + EnterpriseNetwork.getLogs()));

        panel.add(title);
        panel.add(bankBtn);
        panel.add(billBtn);
        panel.add(aiBtn);
        panel.add(auditBtn);
        panel.add(output);

        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    // ===============================
    // BANKING UI
    // ===============================
    private void openBanking(JTextArea output) {

        String amount = JOptionPane.showInputDialog("Enter Deposit Amount:");

        try {
            double amt = Double.parseDouble(amount);
            account.deposit(amt);
            output.setText("Deposited: " + amt + "\nBalance: " + account.getBalance());
        } catch (Exception e) {
            output.setText("Invalid input");
        }
    }

    // ===============================
    // BILLING UI
    // ===============================
    private void openBilling(JTextArea output) {

        String[] options = {"University", "Medical"};
        String choice = (String) JOptionPane.showInputDialog(
                frame,
                "Select Billing Type",
                "Billing",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null) return;

        Billable bill;

        if (choice.equals("University")) {
            bill = new UniversityCourse("CS", 500, 100, 50, 200);
        } else {
            bill = new MedicalRecord("Flu", 200, 50, 30);
        }

        String token = JOptionPane.showInputDialog("Enter Security Token:");

        network.processNetworkBilling(client, account, bill, token);

        aiAnalyzer.addExpense(new Expense(choice, bill.generateInvoice()));

        output.setText(choice + " bill processed successfully.");
    }

    // ===============================
    // BUTTON STYLE
    // ===============================
    private JButton createButton(String text, int x, int y) {

        JButton btn = new JButton(text);
        btn.setBounds(x, y, 200, 50);
        btn.setBackground(new Color(59, 130, 246));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    // ===============================
    // MAIN
    // ===============================
    public static void main(String[] args) {

        MainUI ui = new MainUI();

        // temporary backend init
        ui.client = new Client("1", "Azfar", "test@email.com", "STUDENT");
        ui.account = new SavingsAccount("ACC-101", 1000);
    }
}
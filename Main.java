import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

    public abstract void displayDashboard();
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
    public void displayDashboard() {
        System.out.println("\n==================================");
        System.out.println("         USER DASHBOARD");
        System.out.println("==================================");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Client Type: " + clientType);
        System.out.println("==================================");
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
            System.out.println("[BANK] Deposit Successful: $" + amount);
            EnterpriseNetwork.logSystemEvent("[BANK] Deposit Added: $" + amount);
        } else {
            System.out.println("[BANK ERROR] Invalid Amount.");
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            System.out.println("[BANK] Withdrawal Successful: $" + amount);
            return true;
        }
        System.out.println("[BANK ERROR] Insufficient Funds.");
        return false;
    }

    @Override
    public boolean authorizeTransaction(String token) {
        return SECRET_TOKEN.equals(token);
    }
}

// ======================================================
// UNIVERSITY BILLING
// ======================================================

class UniversityCourse implements Billable {
    private String courseName;
    private double tuitionFee;
    private double booksFee;
    private double transportFee;
    private double hostelFee;

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
        return "University Expenses for Course: " + courseName;
    }
}

// ======================================================
// MEDICAL RECORD
// ======================================================

class MedicalRecord implements Billable {
    private String disease;
    private double treatmentCost;
    private double medicineCost;
    private double emergencyCost;

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
        return "Medical Treatment for: " + disease;
    }
}

// ======================================================
// EXPENSE CLASS
// ======================================================

class Expense {
    private String category;
    private double amount;

    public Expense(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() { return category; }
    public double getAmount() { return amount; }
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
    }

    public double calculateTotalExpenses() {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        return total;
    }

    public double predictNextMonthExpense() {
        double total = calculateTotalExpenses();
        return total * 1.12;
    }

    public Map<String, Double> categoryAnalysis() {
        Map<String, Double> categoryMap = new HashMap<>();
        for (Expense e : expenses) {
            String category = e.getCategory();
            categoryMap.put(category, categoryMap.getOrDefault(category, 0.0) + e.getAmount());
        }
        return categoryMap;
    }
public void generateAIReport() {

    System.out.println("\n=======================================");
    System.out.println("         AI SMART FINANCIAL REPORT");
    System.out.println("=======================================");

    double total = calculateTotalExpenses();
    System.out.println("Current Month Total Expenses: $" + total);

    double predicted = predictNextMonthExpense();
    System.out.println("Expected Next Month Expenses: $" + predicted);

    Map<String, Double> analysis = categoryAnalysis();

    System.out.println("\n========= EXPENSE BREAKDOWN =========");

    for (Map.Entry<String, Double> entry : analysis.entrySet()) {
        String category = entry.getKey();
        double amount = entry.getValue();
        double percentage = (amount / total) * 100;

        System.out.println(category + " : $" + amount +
                " (" + String.format("%.2f", percentage) + "%)");
    }

    System.out.println("\n========= AI PERSONALIZED ADVICE =========");

    double possibleSavings = 0;

    for (Map.Entry<String, Double> entry : analysis.entrySet()) {

        String category = entry.getKey();
        double amount = entry.getValue();
        double save = 0;

        // ================= RULE ENGINE =================

        if (category.equalsIgnoreCase("Food") && amount > 700) {
            save = amount * 0.30;
            System.out.println("\n[FOOD ALERT]");
            System.out.println("Overspending detected in Food: $" + amount);
            System.out.println("AI Advice: Reduce food spending & cook at home.");
        }

        else if (category.equalsIgnoreCase("Entertainment") && amount > 500) {
            save = amount * 0.40;
            System.out.println("\n[ENTERTAINMENT ALERT]");
            System.out.println("High entertainment usage: $" + amount);
            System.out.println("AI Advice: Limit streaming, gaming & outings.");
        }

        else if (category.equalsIgnoreCase("Shopping") && amount > 600) {
            save = amount * 0.35;
            System.out.println("\n[SHOPPING ALERT]");
            System.out.println("Impulse buying detected: $" + amount);
            System.out.println("AI Advice: Create strict shopping budget.");
        }

        else if (category.equalsIgnoreCase("Medical") && amount > 800) {
            save = amount * 0.15;
            System.out.println("\n[MEDICAL ALERT]");
            System.out.println("High medical spending: $" + amount);
            System.out.println("AI Advice: Consider insurance & preventive care.");
        }

        else if (category.equalsIgnoreCase("University") && amount > 2000) {
            save = amount * 0.10;
            System.out.println("\n[UNIVERSITY ALERT]");
            System.out.println("High education cost: $" + amount);
            System.out.println("AI Advice: Use digital books & shared transport.");
        }

        possibleSavings += save;
    }

    System.out.println("\n=======================================");
    System.out.println("TOTAL POSSIBLE SAVINGS: $" + String.format("%.2f", possibleSavings));

    double optimized = predicted - possibleSavings;

    System.out.println("OPTIMIZED NEXT MONTH EXPENSE: $" + String.format("%.2f", optimized));

    double score = (possibleSavings > 1000) ? 55 :
                   (possibleSavings > 500) ? 70 : 90;

    System.out.println("AI FINANCIAL SCORE: " + score + "/100");

    if (score < 60) {
        System.out.println("\n[AI ALERT]");
        System.out.println("High financial risk detected.");
    } else {
        System.out.println("\n[AI STATUS]");
        System.out.println("Financial behavior is stable.");
    }

    System.out.println("=======================================");
}

    String generateAIReportString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

// ======================================================
// ENTERPRISE NETWORK
// ======================================================

class EnterpriseNetwork {
    private static final List<String> auditTrail = new ArrayList<>();

    public static void logSystemEvent(String event) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        auditTrail.add("[" + now.toString().substring(11, 19) + "] " + event);
    }

    public static void printAuditTrail() {
        System.out.println("\n==================================");
        System.out.println("          SYSTEM AUDIT");
        System.out.println("==================================");
        if (auditTrail.isEmpty()) {
            System.out.println("No logs available.");
        } else {
            for (String log : auditTrail) {
                System.out.println(log);
            }
        }
        System.out.println("==================================");
    }

    public void processNetworkBilling(Client client, BankAccount account, Billable bill, String token) {
        System.out.println("\n[SYSTEM] Processing: " + bill.getBillDetails());

        if (!account.authorizeTransaction(token)) {
            System.out.println("[SECURITY ERROR] Invalid Token.");
            logSystemEvent("[SECURITY] Invalid Transaction Attempt.");
            return;
        }

        double amount = bill.generateInvoice();

        if (client.getClientType().equalsIgnoreCase("STUDENT")) {
            System.out.println("[DISCOUNT] 15% Student Discount Applied.");
            amount *= 0.85;
        }

        System.out.println("Final Invoice: $" + amount);
        boolean success = account.withdraw(amount);

        if (success) {
            System.out.println("[SYSTEM] Billing Successful.");
            logSystemEvent("[PAYMENT SUCCESS] " + client.getName() + " paid $" + amount);
        } else {
            System.out.println("[SYSTEM] Payment Failed.");
            logSystemEvent("[PAYMENT FAILED] Insufficient Balance.");
        }
    }

    public static void process(Client client, SavingsAccount account, Billable bill, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'process'");
    }

    public static String getLogs() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLogs'");
    }
}

// ======================================================
// MAIN APPLICATION
// ======================================================

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EnterpriseNetwork network = new EnterpriseNetwork();
        AIExpenseAnalyzer aiAnalyzer = new AIExpenseAnalyzer();

        System.out.println("========================================");
        System.out.println(" AI UNIVERSITY + HEALTH + BANK SYSTEM");
        System.out.println("========================================");

        System.out.print("Create Admin Username: ");
        String adminUser = scanner.nextLine();

        System.out.print("Create Admin Password: ");
        String adminPass = scanner.nextLine();

        System.out.println("\n===== CLIENT REGISTRATION =====");
        System.out.print("Enter Client ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Client Type (STUDENT/PATIENT): ");
        String type = scanner.nextLine();

        System.out.print("Enter Initial Deposit: ");
        double deposit = scanner.nextDouble();
        scanner.nextLine();

        Client client = new Client(id, name, email, type);
        SavingsAccount account = new SavingsAccount("ACC-" + (int)(Math.random() * 99999), deposit);

        EnterpriseNetwork.logSystemEvent("[REGISTRATION] New Client Added.");

        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("            MAIN MENU");
            System.out.println("=================================");
            System.out.println("1. View Dashboard");
            System.out.println("2. Deposit Money");
            System.out.println("3. University Billing");
            System.out.println("4. Medical Billing");
            System.out.println("5. Add Personal Expense");
            System.out.println("6. Generate AI Financial Report");
            System.out.println("7. View Audit Logs");
            System.out.println("8. Exit");
            System.out.print("Select Option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Admin Password: ");
                    String verify = scanner.nextLine();
                    if (verify.equals(adminPass)) {
                        client.displayDashboard();
                        System.out.println("Bank Balance: $" + account.getBalance());
                    } else {
                        System.out.println("ACCESS DENIED.");
                    }
                    break;

                case 2:
                    System.out.print("Enter Deposit Amount: ");
                    double dep = scanner.nextDouble();
                    account.deposit(dep);
                    break;

                case 3:
                    System.out.print("Enter Course Name: ");
                    String course = scanner.nextLine();
                    System.out.print("Enter Tuition Fee: ");
                    double tuition = scanner.nextDouble();
                    System.out.print("Enter Books Fee: ");
                    double books = scanner.nextDouble();
                    System.out.print("Enter Transport Fee: ");
                    double transport = scanner.nextDouble();
                    System.out.print("Enter Hostel Fee: ");
                    double hostel = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter Security Token: ");
                    String token = scanner.nextLine();

                    Billable uniBill = new UniversityCourse(course, tuition, books, transport, hostel);
                    network.processNetworkBilling(client, account, uniBill, token);
                    aiAnalyzer.addExpense(new Expense("University", uniBill.generateInvoice()));
                    break;

                case 4:
                    System.out.print("Enter Disease: ");
                    String disease = scanner.nextLine();
                    System.out.print("Enter Treatment Cost: ");
                    double treatment = scanner.nextDouble();
                    System.out.print("Enter Medicine Cost: ");
                    double medicine = scanner.nextDouble();
                    System.out.print("Enter Emergency Cost: ");
                    double emergency = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter Security Token: ");
                    String medToken = scanner.nextLine();

                    Billable medBill = new MedicalRecord(disease, treatment, medicine, emergency);
                    network.processNetworkBilling(client, account, medBill, medToken);
                    aiAnalyzer.addExpense(new Expense("Medical", medBill.generateInvoice()));
                    break;

                case 5:
                    System.out.print("How many expenses to add? ");
                    int count = scanner.nextInt();
                    scanner.nextLine();
                    for (int i = 1; i <= count; i++) {
                        System.out.println("\nExpense #" + i);
                        System.out.print("Category: ");
                        String category = scanner.nextLine();
                        System.out.print("Amount: ");
                        double amount = scanner.nextDouble();
                        scanner.nextLine();
                        aiAnalyzer.addExpense(new Expense(category, amount));
                        System.out.println("Expense Added Successfully.");
                    }
                    break;

                case 6:
                    aiAnalyzer.generateAIReport();
                    break;

                case 7:
                    EnterpriseNetwork.printAuditTrail();
                    break;

                case 8:
                    running = false;
                    System.out.println("\nSystem Closed Successfully.");
                    break;

                default:
                    System.out.println("Invalid Option.");
            }
        }
        scanner.close();
    }
}
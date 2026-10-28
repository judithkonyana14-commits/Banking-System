package banking;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class BankingSystemMain {

    
    private final ArrayList<BankAccount> accounts = new ArrayList<>();
    private final TransactionLogger logger = new TransactionLogger("transactions.txt");
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new BankingSystemMain().run();
    }

    

    private void run() {
        createInitialAccounts();
        System.out.println("Transactions are saved to: " + logger.getLogFilePath());
        boolean running = true;
        try {
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();   
                switch (choice) {
                    case "1": displayAllAccounts(); break;
                    case "2": depositMoney(); break;
                    case "3": withdrawMoney(); break;
                    case "4": transferMoney(); break;
                    case "5": calculateInterest(); break;
                    case "6": requestLoan(); break;
                    case "7": displayAccountDetails(); break;
                    case "8":
                        System.out.println("Thank you for using the Console Banking System. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 8.");
                }
            }
        } catch (NoSuchElementException e) {
            // EXCEPTION HANDLING:
            System.out.println("\nInput closed. Exiting the program. Goodbye!");
        } finally {
            scanner.close();   
        }
    }

    private void printMenu() {
        System.out.println();
        
        System.out.println("Menu");
        
        System.out.println("1. Display all accounts");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Transfer money");
        System.out.println("5. Calculate interest");
        System.out.println("6. Request bank loan");
        System.out.println("7. Display account details");
        System.out.println("8. Exit");
        
        System.out.print("Enter choice from menu:");
    }

    private void createInitialAccounts() {
        // POLYMORPHISM: 
        BankAccount account1 = new SavingsAccount("1001", "Majara Masupha", 7900.00);
        BankAccount account2 = new CurrentAccount("2001", "Lineo Lekoala", 1900.00);
        BankAccount account3 = new SavingsAccount("3001", "Leemisa Makoetlane", 380.00);
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
    }

    

    private void displayAllAccounts() {
        System.out.println("\n--- ALL ACCOUNTS ---");
        for (BankAccount account : accounts) {
            account.displayAccountInfo();   
            System.out.println("----------------------------------------");
        }
    }

    private void depositMoney() {
        String accNo = readAccountNumber("Enter account number: ");
        BankAccount account = null;
        double amount = Double.NaN;
        try {
            account = findAccount(accNo);
            amount = readAmount("Enter deposit amount: M");
            account.deposit(amount);   
            System.out.println("Deposit successful. New balance: " + BankAccount.formatMoney(account.getBalance()));
            logger.log(account.getAccountNumber(), "DEPOSIT", amount, account.getBalance(),
                    "SUCCESS - Deposit completed");
        } catch (BankAccount.TransactionException e) {   
            System.out.println("Deposit rejected: " + e.getMessage());
            logRejected(accNo, "DEPOSIT", amount, account, e.getMessage());
        }
    }

    private void withdrawMoney() {
        String accNo = readAccountNumber("Enter account number: ");
        BankAccount account = null;
        double amount = Double.NaN;
        try {
            account = findAccount(accNo);
            amount = readAmount("Enter withdrawal amount: M");
            account.withdraw(amount);  
            System.out.println("Withdrawal successful. New balance: " + BankAccount.formatMoney(account.getBalance()));
            logger.log(account.getAccountNumber(), "WITHDRAWAL", amount, account.getBalance(),
                    "SUCCESS - Withdrawal completed");
        } catch (BankAccount.TransactionException e) {
            System.out.println("Withdrawal rejected: " + e.getMessage());
            logRejected(accNo, "WITHDRAWAL", amount, account, e.getMessage());
        }
    }

    private void transferMoney() {
        String fromNo = readAccountNumber("Enter SOURCE account number: ");
        String toNo = readAccountNumber("Enter DESTINATION account number: ");
        BankAccount source = null;
        double amount = Double.NaN;
        try {
            source = findAccount(fromNo);
            BankAccount destination = findAccount(toNo);
            amount = readAmount("Enter transfer amount: M");
            source.transferTo(destination, amount);   
            System.out.println("Transfer successful.");
            System.out.println(source.getAccountNumber() + " balance: " + BankAccount.formatMoney(source.getBalance()));
            System.out.println(destination.getAccountNumber() + " balance: " + BankAccount.formatMoney(destination.getBalance()));
            logger.log(source.getAccountNumber(), "TRANSFER", destination.getAccountNumber(), amount,
                    source.getBalance(), "SUCCESS - Transfer completed. "
                            + destination.getAccountNumber() + " balance is now "
                            + BankAccount.formatMoney(destination.getBalance()));
        } catch (BankAccount.TransactionException e) {
            System.out.println("Transfer rejected: " + e.getMessage() + " No balances were changed.");
            double balance = (source == null) ? Double.NaN : source.getBalance();
            logger.log(fromNo, "TRANSFER", toNo, amount, balance, "REJECTED - " + e.getMessage());
        }
    }

    private void calculateInterest() {
        String accNo = readAccountNumber("Enter account number: ");
        try {
            BankAccount account = findAccount(accNo);
            double before = account.getBalance();
            double interest = account.calculateInterest();   
            System.out.println("Balance before interest: " + BankAccount.formatMoney(before));
            System.out.println("Interest earned        : " + BankAccount.formatMoney(interest));
            System.out.println("Balance after interest : " + BankAccount.formatMoney(account.getBalance()));
            String result = (interest > 0) ? "SUCCESS - Interest of " + BankAccount.formatMoney(interest) + " added"
                                           : "SUCCESS - No interest earned on this account";
            logger.log(account.getAccountNumber(), "INTEREST", interest, account.getBalance(), result);
        } catch (BankAccount.TransactionException e) {
            System.out.println("Interest calculation rejected: " + e.getMessage());
            logRejected(accNo, "INTEREST", Double.NaN, null, e.getMessage());
        }
    }

    private void requestLoan() {
        String accNo = readAccountNumber("Enter account number: ");
        BankAccount account = null;
        double amount = Double.NaN;
        try {
            account = findAccount(accNo);
            if (!(account instanceof Loanable)) {
                throw new BankAccount.TransactionException("This account type cannot request loans.");
            }
            Loanable loanable = (Loanable) account;   
            System.out.println("Loan limit for this account: " + BankAccount.formatMoney(loanable.getLoanLimit()));
            amount = readAmount("Enter loan amount: M");
            double approved = loanable.requestLoan(amount);
            System.out.println("Loan approved: " + BankAccount.formatMoney(approved)
                    + ". New balance: " + BankAccount.formatMoney(account.getBalance()));
            logger.log(account.getAccountNumber(), "LOAN", amount, account.getBalance(),
                    "SUCCESS - Loan approved");
        } catch (BankAccount.TransactionException e) {
            System.out.println("Loan rejected: " + e.getMessage());
            logRejected(accNo, "LOAN", amount, account, e.getMessage());
        }
    }

    private void displayAccountDetails() {
        String accNo = readAccountNumber("Enter account number: ");
        try {
            BankAccount account = findAccount(accNo);
            System.out.println("\n--- ACCOUNT DETAILS ---");
            account.displayAccountInfo();   
        } catch (BankAccount.TransactionException e) {
            System.out.println(e.getMessage());
        }
    }

    

    private String readAccountNumber(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim().toUpperCase();
    }

    
    private double readAmount(String prompt) throws BankAccount.TransactionException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        
        if (!text.matches("-?\\d+(\\.\\d{1,2})?")) {
            throw new BankAccount.TransactionException(
                    "Invalid amount \"" + text + "\". Enter a number such as 250 or 250.50.");
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new BankAccount.TransactionException("Invalid amount \"" + text + "\".");
        }
    }

    private BankAccount findAccount(String accountNumber) throws BankAccount.TransactionException {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        throw new BankAccount.TransactionException("Account \"" + accountNumber + "\" does not exist.");
    }

    private void logRejected(String accNo, String type, double amount, BankAccount account, String reason) {
        double balance = (account == null) ? Double.NaN : account.getBalance();
        logger.log(accNo, type, amount, balance, "REJECTED - " + reason);
    }
}

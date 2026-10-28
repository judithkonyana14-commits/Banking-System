package banking;

import java.util.Locale;


 
public abstract class BankAccount {

    

    public static class TransactionException extends Exception {
        private static final long serialVersionUID = 1L;

        public TransactionException(String message) {
            super(message);
        }
    }

    // ENCAPSULATION: 
    private final String accountNumber;
    private final String accountHolder;
    private double balance;

    // Constructor: 
    public BankAccount(String accountNumber, String accountHolder, double openingBalance) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty.");
        }
        if (accountHolder == null || accountHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty.");
        }
        if (Double.isNaN(openingBalance) || Double.isInfinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.accountNumber = accountNumber.trim().toUpperCase();
        this.accountHolder = accountHolder.trim();
        this.balance = roundToCents(openingBalance);
    }

    

     
    public abstract void deposit(double amount) throws TransactionException;

    
    public abstract void withdraw(double amount) throws TransactionException;

    
    public abstract double calculateInterest();

    
    public abstract String getAccountType();

    

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }




    public double getAvailableFunds() {
        return balance;
    }

    protected void validateAmount(double amount) throws TransactionException {
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new TransactionException("Amount must be greater than zero.");
        }
    }

    
    protected void increaseBalance(double amount) {
        balance = roundToCents(balance + amount);
    }

    protected void decreaseBalance(double amount) {
        balance = roundToCents(balance - amount);
    }

    
    public void transferTo(BankAccount destination, double amount) throws TransactionException {
        if (destination == null) {
            throw new TransactionException("Destination account does not exist.");
        }
        if (destination == this || destination.getAccountNumber().equals(accountNumber)) {
            throw new TransactionException("You cannot transfer money to the same account.");
        }
        validateAmount(amount);
        withdraw(amount);                       
        try {
            destination.deposit(amount);       
        } catch (TransactionException e) {
            increaseBalance(amount);            
            throw new TransactionException("Transfer cancelled and reversed: " + e.getMessage());
        }
    }

    
    public void displayAccountInfo() {
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Account Holder : " + accountHolder);
        System.out.println("Account Type   : " + getAccountType());   
        System.out.println("Balance        : " + formatMoney(balance));
    }

    

    
    public static String formatMoney(double value) {
        double rounded = roundToCents(value);
        if (rounded == 0) {
            rounded = 0.0; 
        }
        String sign = rounded < 0 ? "-" : "";
        return sign + "M" + String.format(Locale.US, "%,.2f", Math.abs(rounded));
    }

    private static double roundToCents(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

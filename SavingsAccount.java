package banking;


 
public class SavingsAccount extends BankAccount implements Loanable {

    
    private static final double INTEREST_RATE = 0.05;       
    private static final double MINIMUM_BALANCE = 100.00;   
    private static final double LOAN_LIMIT = 5000.00;       

    public SavingsAccount(String accountNumber, String accountHolder, double openingBalance) {
        super(accountNumber, accountHolder, openingBalance);  // inheritance: superclass builds the account
    }

    // METHOD OVERRIDING:

    @Override
    public void deposit(double amount) throws TransactionException {
        validateAmount(amount);        
        increaseBalance(amount);
    }

    @Override
    public void withdraw(double amount) throws TransactionException {
        validateAmount(amount);
        // Savings rule: 
        if (amount > getAvailableFunds()) {
            throw new TransactionException("A savings account must keep a minimum balance of "
                    + formatMoney(MINIMUM_BALANCE) + ". The most you can take out is "
                    + formatMoney(getAvailableFunds()) + ".");
        }
        decreaseBalance(amount);
    }

    @Override
    public double calculateInterest() {
        // Savings rule: 
        double interest = Math.round(getBalance() * INTEREST_RATE * 100.0) / 100.0;
        increaseBalance(interest);
        return interest;
    }

    @Override
    public double getAvailableFunds() {
        
        return Math.max(0, getBalance() - MINIMUM_BALANCE);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    // INTERFACE 

    @Override
    public double requestLoan(double amount) throws TransactionException {
        validateAmount(amount);
        if (amount > LOAN_LIMIT) {
            throw new TransactionException("Loan limit exceeded. The maximum loan for a savings account is "
                    + formatMoney(LOAN_LIMIT) + ".");
        }
        increaseBalance(amount);
        return amount;
    }

    @Override
    public double getLoanLimit() {
        return LOAN_LIMIT;
    }

    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();   
        System.out.println("Minimum Balance: " + formatMoney(MINIMUM_BALANCE));
        System.out.println("Interest Rate  : " + (int) (INTEREST_RATE * 100) + "% per year");
        System.out.println("Available      : " + formatMoney(getAvailableFunds()) + " (can be withdrawn)");
        System.out.println("Loan Limit     : " + formatMoney(LOAN_LIMIT));
    }
}

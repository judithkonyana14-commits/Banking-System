package banking;


public class CurrentAccount extends BankAccount implements Loanable {

    
    private static final double OVERDRAFT_LIMIT = 2000.00;  
    private static final double LOAN_LIMIT = 10000.00;     

    public CurrentAccount(String accountNumber, String accountHolder, double openingBalance) {
        super(accountNumber, accountHolder, openingBalance);
    }

    // METHOD 

    @Override
    public void deposit(double amount) throws TransactionException {
        validateAmount(amount);        
        increaseBalance(amount);
    }

    @Override
    public void withdraw(double amount) throws TransactionException {
        validateAmount(amount);
        // Current-account rule: 
        if (amount > getAvailableFunds()) {
            throw new TransactionException("Insufficient funds. Available (including the "
                    + formatMoney(OVERDRAFT_LIMIT) + " overdraft) is "
                    + formatMoney(getAvailableFunds()) + ".");
        }
        decreaseBalance(amount);
    }

    @Override
    public double calculateInterest() {
        return 0.0;   
    }

    @Override
    public double getAvailableFunds() {
        return getBalance() + OVERDRAFT_LIMIT;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    

    @Override
    public double requestLoan(double amount) throws TransactionException {
        validateAmount(amount);
        if (amount > LOAN_LIMIT) {
            throw new TransactionException("Loan limit exceeded. The maximum loan for a current account is "
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
        System.out.println("Overdraft Limit: " + formatMoney(OVERDRAFT_LIMIT));
        System.out.println("Interest Rate  : 0% (no interest)");
        System.out.println("Available      : " + formatMoney(getAvailableFunds()) + " (balance + overdraft)");
        System.out.println("Loan Limit     : " + formatMoney(LOAN_LIMIT));
    }
}

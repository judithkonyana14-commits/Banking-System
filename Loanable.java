package banking;


public interface Loanable {

    /**
     *
     *
     * @return the approved loan amount (success)
     * @throws BankAccount.TransactionException if the request is rejected (balance unchanged)
     */
    double requestLoan(double amount) throws BankAccount.TransactionException;

    /** The maximum loan this account type may receive. */
    double getLoanLimit();
}

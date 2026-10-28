package banking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


 
public final class TransactionLogger {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String fileName;   

    public TransactionLogger(String fileName) {
        this.fileName = fileName;
    }

     
    public String getLogFilePath() {
        return new File(fileName).getAbsolutePath();
    }

    
    public void log(String accountNumber, String type, double amount, double balance, String result) {
        writeRecord("ACCOUNT: " + accountNumber + " | TYPE: " + type
                + " | AMOUNT: " + money(amount) + " | BALANCE: " + money(balance)
                + " | RESULT: " + result);
    }

    
    public void log(String accountNumber, String type, String destinationAccount,
                    double amount, double balance, String result) {
        writeRecord("ACCOUNT: " + accountNumber + " | TYPE: " + type + " | TO: " + destinationAccount
                + " | AMOUNT: " + money(amount) + " | BALANCE: " + money(balance)
                + " | RESULT: " + result);
    }

    private String money(double value) {
        return Double.isNaN(value) ? "N/A" : BankAccount.formatMoney(value);
    }

    // FILE HANDLING + EXCEPTION HANDLING
    private void writeRecord(String text) {
        String line = LocalDateTime.now().format(TIME_FORMAT) + " | " + text;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            
            System.out.println("Warning: could not write to " + fileName + " (" + e.getMessage() + ").");
        }
    }
}

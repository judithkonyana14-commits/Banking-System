# Console Banking System (BIOP2110 Object Oriented Programming I)

A beginner-level Java console application for Windows 10 and Visual Studio Code. It uses only the
standard Java library: no database, internet, frameworks or extra downloads other than a JDK.
Currency is the Lesotho Maloti (M).

## 1. Quick start (3 steps)
1. Right-click `BankingSystem.zip` > **Extract All...** (do not run it from inside the zip).
2. Open the extracted `BankingSystem` folder, then double-click **run.bat**.
3. Use the menu by typing a number from 1 to 8 and pressing Enter.

## 2. Extract the ZIP and open it in VS Code
1. Extract the zip, for example to `Documents`. You should get a folder called `BankingSystem` that contains `src`, `run.bat`, `README.md`, `transactions.txt` and `.vscode`.
2. Start VS Code, choose **File > Open Folder...** and select the `BankingSystem` folder (the one that contains `src`, not a folder above or below it).
3. If VS Code asks "Do you trust the authors?", choose **Yes, I trust the authors**.

## 3. Check that Java is installed
Open a terminal (VS Code: **Terminal > New Terminal**) and type:
```text
javac -version
java -version
```
Both should print a version number of 17 or higher (21 is fine). If you get "not recognized":
install a JDK (for example Eclipse Temurin from adoptium.net), enable the option that adds Java to PATH,
then close and reopen VS Code and try again. `run.bat` also checks this and tells you what to do.

## 4. Run the application
**Option A - run.bat (simplest):** double-click `run.bat` in File Explorer, or type `.\run.bat` in the VS Code terminal.
It checks Java, compiles `src\banking\*.java` into a `bin` folder, and starts the program.

**Option B - VS Code terminal (manual):** in the terminal, inside the `BankingSystem` folder:
```text
javac -d bin src\banking\*.java
java -cp bin banking.BankingSystemMain
```

**Option C - VS Code Run button (optional):** if you have the "Extension Pack for Java", open
`src\banking\BankingSystemMain.java`, press **Run** above `main`, or use the launch configuration
"Run Console Banking System" in the Run and Debug panel. The project does not depend on this option.

## 5. Where is transactions.txt?
It is in the `BankingSystem` folder next to `run.bat` (the program uses the relative path `transactions.txt`
and run.bat always starts from its own folder). The program prints the full path when it starts.
Open it in VS Code or Notepad to read the log. New records are **appended**; if you delete the file, the
program creates it again automatically. One example line:
```text
2026-10-27 21:56:38 | ACCOUNT: SAV1001 | TYPE: DEPOSIT | AMOUNT: M500.00 | BALANCE: M5,500.00 | RESULT: SUCCESS - Deposit completed
```
Transfers also contain `TO: <destination account>`. Rejected attempts contain `RESULT: REJECTED - <reason>`.

## 6. Starting accounts and rules
| Number | Holder | Type | Opening balance |
|---|---|---|---|
| SAV1001 | Thabo Mokoena | Savings | M5,000.00 |
| CUR2001 | Lerato Molapo | Current | M1,500.00 |
| SAV1002 | Palesa Nkosi | Savings | M250.00 |

| Rule | Value | Constant |
|---|---|---|
| Savings interest | 5% of the balance | `SavingsAccount.INTEREST_RATE` |
| Savings minimum balance | M100.00 | `SavingsAccount.MINIMUM_BALANCE` |
| Savings loan limit | M5,000.00 | `SavingsAccount.LOAN_LIMIT` |
| Current overdraft | M2,000.00 | `CurrentAccount.OVERDRAFT_LIMIT` |
| Current loan limit | M10,000.00 | `CurrentAccount.LOAN_LIMIT` |

The loan limits are example values chosen by the author because the assignment does not give any. Change the
constants at the top of each class to change them. Amounts accept up to 2 decimals (for example `250` or `250.50`).

## 7. Project structure and what each file does
```text
BankingSystem/
├── src/banking/
│   ├── BankAccount.java        abstract superclass (+ nested TransactionException)
│   ├── SavingsAccount.java     5% interest, M100.00 minimum balance, loan limit
│   ├── CurrentAccount.java     no interest, M2,000.00 overdraft, loan limit
│   ├── Loanable.java           interface for loans
│   ├── TransactionLogger.java  final class that appends records to transactions.txt
│   └── BankingSystemMain.java  console menu and Scanner input
├── transactions.txt
├── run.bat
├── README.md
└── .vscode/settings.json, launch.json
```
(`bin/` is created when you compile; it holds the compiled `.class` files.)

## 8. Where each concept is demonstrated
| Concept | Where |
|---|---|
| Abstraction | `abstract class BankAccount`; abstract `deposit`, `withdraw`, `calculateInterest` |
| Inheritance | `SavingsAccount` and `CurrentAccount` `extends BankAccount` and call `super(...)` |
| Method overriding | `@Override` of `withdraw`, `calculateInterest`, `getAvailableFunds`, `displayAccountInfo` in both subclasses |
| Encapsulation | private fields in `BankAccount`; no `setBalance`; balance only changed by protected `increaseBalance` / `decreaseBalance` after validation |
| Polymorphism | `ArrayList<BankAccount>` in `BankingSystemMain`; calls such as `account.withdraw(amount)` run the subclass version |
| Interface implementation | `Loanable` implemented by both account classes with different loan limits |
| Final class | `public final class TransactionLogger` |
| Exception handling | `BankAccount.TransactionException`, `try-catch` in every menu action, safe number reading, `IOException` caught in the logger |
| File handling | `TransactionLogger.writeRecord` (`BufferedWriter` + `FileWriter` in append mode, `java.time` timestamps) |

## 9. Test checklist
Tick these in the running program (starting balances: SAV1001 M5,000, CUR2001 M1,500, SAV1002 M250):
- [ ] Option 1 shows three accounts.
- [ ] Deposit M500 to SAV1001 works; deposit 0 and -50 are rejected.
- [ ] Withdraw M1000 from SAV1001 works; withdraw M200 from SAV1002 is rejected (below M100 minimum).
- [ ] Withdraw M2000 from CUR2001 works (balance -M500); withdrawing M2000 again is rejected.
- [ ] Transfer M300 from SAV1001 to CUR2001 works; SAV1002 to CUR2001 for M5000 is rejected and nothing changes; SAV1001 to SAV1001 is rejected.
- [ ] Interest on a savings account adds 5%; on CUR2001 it adds nothing.
- [ ] Loan of M3000 on SAV1001 works; M9000 is rejected (limit M5,000).
- [ ] Letters as an amount, menu choice `9`, and account `XYZ999` all give a message and return to the menu.
- [ ] `transactions.txt` contains SUCCESS lines and REJECTED lines with reasons.
- [ ] Option 8 shows the goodbye message and exits.

## 10. Possible lecturer questions
- **Why is `BankAccount` abstract?** A general "bank account" does not exist on its own; it must be savings or current. Abstract stops plain objects being created and forces subclasses to define deposit, withdraw and interest.
- **Why do the subclasses extend it?** They are kinds of bank account, so they reuse the shared data and methods instead of copying them.
- **Abstraction vs encapsulation?** Abstraction shows what an account can do without the details. Encapsulation hides the data (private fields) so it can only change through methods that check the rules.
- **Where is polymorphism?** The `ArrayList<BankAccount>` holds both types; `account.withdraw()` runs the savings or current version depending on the real object.
- **What is overriding?** A subclass gives its own body to an inherited method with the same signature. Savings `withdraw` protects the M100 minimum; current `withdraw` allows the overdraft.
- **Why is `Loanable` an interface?** Loans are an ability, not a kind of account. Each class promises `requestLoan` and sets its own limit, and a class can implement many interfaces.
- **Why is `TransactionLogger` final?** It is complete and was never designed to be extended; final stops anyone changing how records are written.
- **How does exception handling prevent crashes?** Rule breaks throw `TransactionException`, which the menu code catches, then it prints the reason and returns to the menu. Text is checked before converting to a number.
- **How are records appended?** `new FileWriter(name, true)` opens in append mode and creates the file if needed; try-with-resources closes it.
- **How are rejected transactions recorded?** The `catch` block calls the logger with `REJECTED - <reason>`.
- **How are transfers kept consistent?** The source is withdrawn first (throws before changing anything if it fails), then the destination is deposited; if that failed, the withdrawal is reversed. Both change or neither.

## 11. Testing disclosure
This project was compiled with JDK 21 and run with scripted input in a Linux environment, covering all menu options, invalid input and logging.
`run.bat` could not be executed there (it needs Windows), so it was checked by reading only. Please run it once on your laptop.

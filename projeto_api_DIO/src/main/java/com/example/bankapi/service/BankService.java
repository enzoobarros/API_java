
package com.example.bankapi.service;

import com.example.bankapi.model.Account;
import com.example.bankapi.model.Transaction;
import com.example.bankapi.model.TransactionType;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BankService {

    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();

    private final AtomicLong accountIdCounter = new AtomicLong(1);
    private final AtomicLong transactionIdCounter = new AtomicLong(1);

    public Account createAccount(String owner, BigDecimal initialBalance) {
        Long id = accountIdCounter.getAndIncrement();
        Account account = new Account(id, owner, initialBalance);
        accounts.put(id, account);
        return account;
    }

    public Account getAccount(Long id) {
        return accounts.get(id);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public Transaction deposit(Long accountId, BigDecimal amount) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        account.setBalance(account.getBalance().add(amount));

        Transaction tx = recordTransaction(null, accountId, amount, TransactionType.DEPOSIT);
        return tx;
    }

    public Transaction withdraw(Long accountId, BigDecimal amount) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Transaction tx = recordTransaction(accountId, null, amount, TransactionType.WITHDRAW);
        return tx;
    }

    public Transaction transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account from = accounts.get(fromAccountId);
        Account to = accounts.get(toAccountId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        Transaction tx = recordTransaction(fromAccountId, toAccountId, amount, TransactionType.TRANSFER);
        return tx;
    }

    public List<Transaction> getTransactionsForAccount(Long accountId) {
        List<Transaction> list = new ArrayList<>();
        for (Transaction tx : transactions.values()) {
            if (accountId.equals(tx.getFromAccountId()) || accountId.equals(tx.getToAccountId())) {
                list.add(tx);
            }
        }
        return list;
    }

    private Transaction recordTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount, TransactionType type) {
        Long id = transactionIdCounter.getAndIncrement();
        Transaction tx = new Transaction(id, fromAccountId, toAccountId, amount, LocalDateTime.now(), type);
        transactions.put(id, tx);
        return tx;
    }
}

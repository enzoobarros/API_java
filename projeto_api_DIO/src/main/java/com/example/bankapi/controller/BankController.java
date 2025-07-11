
package com.example.bankapi.controller;

import com.example.bankapi.dto.TransferRequest;
import com.example.bankapi.model.Account;
import com.example.bankapi.model.Transaction;
import com.example.bankapi.service.BankService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, String> body) {
        String owner = body.get("owner");
        BigDecimal initialBalance = new BigDecimal(body.getOrDefault("initialBalance", "0"));
        Account account = bankService.createAccount(owner, initialBalance);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Account account = bankService.getAccount(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok(bankService.getAllAccounts());
    }

    @PostMapping("/transactions/deposit")
    public ResponseEntity<Transaction> deposit(@RequestBody Map<String, String> body) {
        Long accountId = Long.valueOf(body.get("accountId"));
        BigDecimal amount = new BigDecimal(body.get("amount"));
        Transaction tx = bankService.deposit(accountId, amount);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/transactions/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestBody Map<String, String> body) {
        Long accountId = Long.valueOf(body.get("accountId"));
        BigDecimal amount = new BigDecimal(body.get("amount"));
        Transaction tx = bankService.withdraw(accountId, amount);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/transactions/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest req) {
        Transaction tx = bankService.transfer(req.getFromAccountId(), req.getToAccountId(), req.getAmount());
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam Long accountId) {
        return ResponseEntity.ok(bankService.getTransactionsForAccount(accountId));
    }
}


# Bank API

Uma API REST simples para transações bancárias, construída com Spring Boot.

## Executar

```bash
# compilar
mvn clean package

# rodar
mvn spring-boot:run
```

## Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | /api/accounts | Cria conta (`{ "owner": "João", "initialBalance": "1000.00" }`) |
| GET    | /api/accounts/{id} | Detalhes da conta |
| GET    | /api/accounts | Lista todas as contas |
| POST   | /api/transactions/deposit | Depósito (`{ "accountId":1, "amount":"100.00" }`) |
| POST   | /api/transactions/withdraw | Saque (`{ "accountId":1, "amount":"50.00" }`) |
| POST   | /api/transactions/transfer | Transferência (`{ "fromAccountId":1, "toAccountId":2, "amount":"25.00" }`) |
| GET    | /api/transactions?accountId=1 | Lista transações da conta |

A API usa armazenamento em memória (ConcurrentHashMap) e não persiste dados em disco.

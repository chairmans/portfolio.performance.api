# Portfolio Daily Return Summary API (demo)

This is a small Spring Boot application that exposes a POST endpoint to compute daily portfolio returns and a decision status.

Endpoint
- POST /api/performance/daily-return

Request (sample):
{
  "portfolioId":"PF-1001",
  "validationDate":"2026-06-14",
  "beginMarketValue":1000000,
  "endMarketValue":1035000,
  "netCashFlow":10000,
  "benchmarkReturnPct":1.8,
  "currency":"USD",
  "requwatedBy":"advisor01"
}

Response (sample):
{
  "portfolioId":"PF-1001",
  "validationDate":"2026-06-14",
  "portfolioReturnPct":2.5,
  "benchmarkReturnPct":1.8,
  "excessReturnPct":0.7,
  "status":"VALID",
  "reasons":[],
  "processedAt":"2026-06-14T10:30:00Z"
}

Approach & assumptions
- Layered structure: controller -> service -> repository
- Accepts simple JSON request. Minimal validation implemented per instructions.
- Saves a record to the configured database (MS SQL Server) for audit; if you do not want DB writes, you can comment out the repository.save calls in `DailyReturnService`.
- Tests cover VALID, REVIEW_REQUIRED and INVALID_INPUT scenarios by unit-testing the service logic with a mocked repository.

Build & run
- Java 17 is required.
- Build and run with Maven:

```powershell
mvn -DskipTests=false test
mvn spring-boot:run
```

By default the application uses the DB settings in `src/main/resources/application.properties`. Ensure a reachable MS SQL Server is available or adjust settings. For running tests, the repository is mocked so no DB is required.

Notes
- This is demonstration-level code and not production hardened.
- The status string `REVIWE_REQUIRED` intentionally follows the spelling in the task instructions.


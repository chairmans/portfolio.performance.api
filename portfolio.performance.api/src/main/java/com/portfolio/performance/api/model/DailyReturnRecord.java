package com.portfolio.performance.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_return_record")
@Data
@NoArgsConstructor
public class DailyReturnRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String portfolioId;
    private String validationDate;

    private BigDecimal beginMarketValue;
    private BigDecimal endMarketValue;
    private BigDecimal netCashFlow;
    private BigDecimal benchmarkReturnPct;
    private String currency;
    private String requwatedBy;

    private BigDecimal portfolioReturnPct;
    private BigDecimal excessReturnPct;
    private String status;
    private String reasons;
    private Instant processedAt;
}



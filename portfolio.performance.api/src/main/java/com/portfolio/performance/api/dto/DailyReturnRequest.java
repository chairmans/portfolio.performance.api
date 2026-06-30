package com.portfolio.performance.api.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DailyReturnRequest {

    private String portfolioId;
    private String validationDate; // ISO yyyy-MM-dd
    private BigDecimal beginMarketValue;
    private BigDecimal endMarketValue;
    private BigDecimal netCashFlow;
    private BigDecimal benchmarkReturnPct;
    private String currency;
    private String requwatedBy;
}



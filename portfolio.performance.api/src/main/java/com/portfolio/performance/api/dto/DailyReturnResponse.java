package com.portfolio.performance.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class DailyReturnResponse {

    private String portfolioId;
    private String validationDate;
    private BigDecimal portfolioReturnPct;
    private BigDecimal benchmarkReturnPct;
    private BigDecimal excessReturnPct;
    private String status;
    private List<String> reasons;
    private Instant processedAt;
}



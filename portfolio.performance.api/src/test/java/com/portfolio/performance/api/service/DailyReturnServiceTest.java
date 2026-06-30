package com.portfolio.performance.api.service;

import com.portfolio.performance.api.constant.DailyReturnConstants;
import com.portfolio.performance.api.dto.DailyReturnRequest;
import com.portfolio.performance.api.dto.DailyReturnResponse;
import com.portfolio.performance.api.model.DailyReturnRecord;
import com.portfolio.performance.api.repository.DailyReturnRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DailyReturnServiceTest {

    private DailyReturnRecordRepository repo;
    private DailyReturnService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(DailyReturnRecordRepository.class);
        when(repo.save(any(DailyReturnRecord.class))).thenAnswer(i -> i.getArgument(0));
        service = new DailyReturnService(repo);
    }

    @Test
    void validScenario_shouldReturnValid() {
        DailyReturnRequest req = new DailyReturnRequest();
        req.setPortfolioId("PF-1001");
        req.setValidationDate("2026-06-14");
        req.setBeginMarketValue(BigDecimal.valueOf(1_000_000));
        req.setEndMarketValue(BigDecimal.valueOf(1_035_000));
        req.setNetCashFlow(BigDecimal.valueOf(10_000));
        req.setBenchmarkReturnPct(BigDecimal.valueOf(1.8));
        req.setCurrency("USD");

        DailyReturnResponse resp = service.process(req);

        assertThat(resp.getStatus()).isEqualTo(DailyReturnConstants.STATUS_VALID);
        // portfolio return = ((1_035_000-1_000_000-10_000)/1_000_000)*100 = (25_000/1_000_000)*100 = 2.5
        assertThat(resp.getPortfolioReturnPct()).isEqualByComparingTo(BigDecimal.valueOf(2.5).setScale(4));
        assertThat(resp.getExcessReturnPct()).isEqualByComparingTo(BigDecimal.valueOf(0.7).setScale(4));
    }

    @Test
    void reviewRequired_dueToBenchmarkDifference() {
        DailyReturnRequest req = new DailyReturnRequest();
        req.setPortfolioId("PF-2001");
        req.setValidationDate("2026-06-14");
        req.setBeginMarketValue(BigDecimal.valueOf(1000));
        req.setEndMarketValue(BigDecimal.valueOf(1500));
        req.setNetCashFlow(BigDecimal.ZERO);
        // portfolio return = 50% -> benchmark 40 -> difference 10 > 5
        req.setBenchmarkReturnPct(BigDecimal.valueOf(40));
        req.setCurrency("USD");

        DailyReturnResponse resp = service.process(req);

        assertThat(resp.getStatus()).isEqualTo(DailyReturnConstants.STATUS_REVIEW_REQUIRED);
        assertThat(resp.getReasons()).anyMatch(r -> r.contains(DailyReturnConstants.REASON_EXCESS_VS_BENCHMARK_GT_5PCT));
    }

    @Test
    void invalidInput_beginZero_endNonZero() {
        DailyReturnRequest req = new DailyReturnRequest();
        req.setPortfolioId("PF-3001");
        req.setValidationDate("2026-06-14");
        req.setBeginMarketValue(BigDecimal.ZERO);
        req.setEndMarketValue(BigDecimal.valueOf(100));
        req.setNetCashFlow(BigDecimal.ZERO);
        req.setBenchmarkReturnPct(BigDecimal.ZERO);
        req.setCurrency("USD");

        DailyReturnResponse resp = service.process(req);

        assertThat(resp.getStatus()).isEqualTo(DailyReturnConstants.STATUS_INVALID_INPUT);
        assertThat(resp.getReasons()).anyMatch(r -> r.contains(DailyReturnConstants.REASON_BEGIN_ZERO_END_NONZERO));
    }
}






package com.portfolio.performance.api.service;

import com.portfolio.performance.api.constant.DailyReturnConstants;
import com.portfolio.performance.api.dto.DailyReturnRequest;
import com.portfolio.performance.api.dto.DailyReturnResponse;
import com.portfolio.performance.api.model.DailyReturnRecord;
import com.portfolio.performance.api.repository.DailyReturnRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyReturnService {

    private final DailyReturnRecordRepository repository;

    public DailyReturnService(DailyReturnRecordRepository repository) {
        this.repository = repository;
    }

    public DailyReturnResponse process(DailyReturnRequest req) {
        List<String> reasons = new ArrayList<>();

        // defensive defaults
        BigDecimal begin = safe(req.getBeginMarketValue());
        BigDecimal end = safe(req.getEndMarketValue());
        BigDecimal net = safe(req.getNetCashFlow());
        BigDecimal bench = safe(req.getBenchmarkReturnPct());

        // validation rules
        if (req.getCurrency() == null || req.getCurrency().trim().isEmpty()) {
            reasons.add(DailyReturnConstants.REASON_CURRENCY_MISSING);
            return buildInvalid(req, reasons);
        }

        if (begin.compareTo(BigDecimal.ZERO) < 0 || end.compareTo(BigDecimal.ZERO) < 0) {
            reasons.add(DailyReturnConstants.REASON_NEGATIVE_VALUES);
            return buildInvalid(req, reasons);
        }

        if (begin.compareTo(BigDecimal.ZERO) == 0 && end.compareTo(BigDecimal.ZERO) != 0) {
            reasons.add(DailyReturnConstants.REASON_BEGIN_ZERO_END_NONZERO);
            return buildInvalid(req, reasons);
        }

        BigDecimal portfolioReturnPct;
        if (begin.compareTo(BigDecimal.ZERO) > 0) {
            // ((endValue-beginValue-netCashFlow)/beginValue)*100
            BigDecimal diff = end.subtract(begin).subtract(net);
            portfolioReturnPct = diff.divide(begin, 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        } else {
            // both zero -> zero return
            portfolioReturnPct = BigDecimal.ZERO;
        }

        BigDecimal excess = portfolioReturnPct.subtract(bench);

        // determine status
        String status = DailyReturnConstants.STATUS_VALID;
        if (portfolioReturnPct.subtract(bench).abs().compareTo(DailyReturnConstants.EXCESS_RETURN_THRESHOLD) > 0) {
            status = DailyReturnConstants.STATUS_REVIEW_REQUIRED;
            reasons.add(DailyReturnConstants.REASON_EXCESS_VS_BENCHMARK_GT_5PCT);
        }

        // absolute net cash flow > 20% of begin market value
        if (begin.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal absNet = net.abs();
            BigDecimal threshold = begin.multiply(DailyReturnConstants.NET_CASH_FLOW_THRESHOLD_PERCENTAGE);
            if (absNet.compareTo(threshold) > 0) {
                if (!status.equals(DailyReturnConstants.STATUS_REVIEW_REQUIRED)) status = DailyReturnConstants.STATUS_REVIEW_REQUIRED;
                reasons.add(DailyReturnConstants.REASON_NET_CASH_FLOW_GT_20PCT_OF_BEGIN);
            }
        }

        DailyReturnResponse resp = new DailyReturnResponse();
        resp.setPortfolioId(req.getPortfolioId());
        resp.setValidationDate(req.getValidationDate());
        resp.setPortfolioReturnPct(portfolioReturnPct.setScale(4, RoundingMode.HALF_UP));
        resp.setBenchmarkReturnPct(bench.setScale(4, RoundingMode.HALF_UP));
        resp.setExcessReturnPct(excess.setScale(4, RoundingMode.HALF_UP));
        resp.setStatus(status);
        resp.setReasons(reasons);
        resp.setProcessedAt(Instant.now());

        // persist record (best-effort; failures will bubble up)
        DailyReturnRecord record = new DailyReturnRecord();
        record.setPortfolioId(req.getPortfolioId());
        record.setValidationDate(req.getValidationDate());
        record.setBeginMarketValue(begin);
        record.setEndMarketValue(end);
        record.setNetCashFlow(net);
        record.setBenchmarkReturnPct(bench);
        record.setCurrency(req.getCurrency());
        record.setRequwatedBy(req.getRequwatedBy());
        record.setPortfolioReturnPct(resp.getPortfolioReturnPct());
        record.setExcessReturnPct(resp.getExcessReturnPct());
        record.setStatus(resp.getStatus());
        record.setReasons(String.join(",", reasons));
        record.setProcessedAt(resp.getProcessedAt());

        repository.save(record);

        return resp;
    }

    private DailyReturnResponse buildInvalid(DailyReturnRequest req, List<String> reasons) {
        DailyReturnResponse resp = new DailyReturnResponse();
        resp.setPortfolioId(req.getPortfolioId());
        resp.setValidationDate(req.getValidationDate());
        resp.setPortfolioReturnPct(null);
        resp.setBenchmarkReturnPct(req.getBenchmarkReturnPct());
        resp.setExcessReturnPct(null);
        resp.setStatus(DailyReturnConstants.STATUS_INVALID_INPUT);
        resp.setReasons(reasons);
        resp.setProcessedAt(Instant.now());

        // save a record for auditing
        DailyReturnRecord record = new DailyReturnRecord();
        record.setPortfolioId(req.getPortfolioId());
        record.setValidationDate(req.getValidationDate());
        record.setBeginMarketValue(safe(req.getBeginMarketValue()));
        record.setEndMarketValue(safe(req.getEndMarketValue()));
        record.setNetCashFlow(safe(req.getNetCashFlow()));
        record.setBenchmarkReturnPct(safe(req.getBenchmarkReturnPct()));
        record.setCurrency(req.getCurrency());
        record.setRequwatedBy(req.getRequwatedBy());
        record.setStatus(resp.getStatus());
        record.setReasons(String.join(",", reasons));
        record.setProcessedAt(resp.getProcessedAt());
        repository.save(record);

        return resp;
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}









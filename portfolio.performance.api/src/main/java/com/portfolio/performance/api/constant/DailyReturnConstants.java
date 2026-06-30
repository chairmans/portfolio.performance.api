package com.portfolio.performance.api.constant;

import java.math.BigDecimal;

public class DailyReturnConstants {

    // Status values
    public static final String STATUS_VALID = "VALID";
    public static final String STATUS_REVIEW_REQUIRED = "REVIEW_REQUIRED";
    public static final String STATUS_INVALID_INPUT = "INVALID_INPUT";

    // Reason codes
    public static final String REASON_CURRENCY_MISSING = "currency_missing";
    public static final String REASON_NEGATIVE_VALUES = "negative_values";
    public static final String REASON_BEGIN_ZERO_END_NONZERO = "begin_zero_end_nonzero";
    public static final String REASON_EXCESS_VS_BENCHMARK_GT_5PCT = "excess_vs_benchmark_gt_5pct";
    public static final String REASON_NET_CASH_FLOW_GT_20PCT_OF_BEGIN = "net_cash_flow_gt_20pct_of_begin";

    // Threshold values
    public static final BigDecimal EXCESS_RETURN_THRESHOLD = new BigDecimal("5");
    public static final BigDecimal NET_CASH_FLOW_THRESHOLD_PERCENTAGE = new BigDecimal("0.2");

    private DailyReturnConstants() {
        // Private constructor to prevent instantiation
    }
}



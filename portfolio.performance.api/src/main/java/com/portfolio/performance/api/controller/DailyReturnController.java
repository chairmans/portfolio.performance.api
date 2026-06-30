package com.portfolio.performance.api.controller;

import com.portfolio.performance.api.dto.DailyReturnRequest;
import com.portfolio.performance.api.dto.DailyReturnResponse;
import com.portfolio.performance.api.service.DailyReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performance")
public class DailyReturnController {

    private final DailyReturnService service;

    public DailyReturnController(DailyReturnService service) {
        this.service = service;
    }

    @PostMapping("/daily-return")
    public ResponseEntity<DailyReturnResponse> compute(@RequestBody DailyReturnRequest req) {
        DailyReturnResponse resp = service.process(req);
        if ("INVALID_INPUT".equals(resp.getStatus())) {
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }
}


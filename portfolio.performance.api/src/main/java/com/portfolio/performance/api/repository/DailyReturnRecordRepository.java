package com.portfolio.performance.api.repository;

import com.portfolio.performance.api.model.DailyReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyReturnRecordRepository extends JpaRepository<DailyReturnRecord, Long> {

}



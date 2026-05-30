package com.example.demo.reconciliation.repository;

import com.example.demo.reconciliation.entity.ReconciliationResult;
import com.example.demo.reconciliation.entity.ReconciliationResultType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ReconciliationResultRepository extends JpaRepository<ReconciliationResult, Long> {

    List<ReconciliationResult> findByResultTypeInOrderByCreatedAtDesc(Collection<ReconciliationResultType> resultTypes);
}

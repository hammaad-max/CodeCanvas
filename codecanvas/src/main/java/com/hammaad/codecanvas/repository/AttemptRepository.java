package com.hammaad.codecanvas.repository;

import com.hammaad.codecanvas.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    List<Attempt> findByStudentIdOrderByCreatedAtAsc(Long studentId);

    @Transactional
    long deleteByStudentId(Long studentId);
}

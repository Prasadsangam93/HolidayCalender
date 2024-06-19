package com.example.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.Holiday;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("select case when count(h) > 0 then true else false end from Holiday h where h.fullDate = :fullDate")
    boolean isFullDateExists(LocalDate fullDate);
}

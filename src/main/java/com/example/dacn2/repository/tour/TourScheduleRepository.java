package com.example.dacn2.repository.tour;

import com.example.dacn2.entity.tour.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {}

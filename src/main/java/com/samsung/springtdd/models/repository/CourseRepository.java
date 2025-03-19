package com.samsung.springtdd.models.repository;

import com.samsung.springtdd.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStartTimeAfter(LocalDateTime dateTime);
}
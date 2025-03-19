package com.samsung.springtdd.models.repository;

import com.samsung.springtdd.models.Course;
import com.samsung.springtdd.models.Registration;
import com.samsung.springtdd.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudent(Student student);
    void deleteByStudentAndCourse(Student student, Course course);
}
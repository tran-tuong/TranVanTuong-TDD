package com.samsung.springtdd.services;

import com.samsung.springtdd.models.Course;
import com.samsung.springtdd.models.Registration;
import com.samsung.springtdd.models.Student;
import com.samsung.springtdd.models.repository.CourseRepository;
import com.samsung.springtdd.models.repository.RegistrationRepository;
import com.samsung.springtdd.models.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationService(CourseRepository courseRepository,
                               StudentRepository studentRepository,
                               RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<Course> registerCourse(Long courseId, String email) {
        Student student = findStudentByEmail(email);
        Course course = findCourseById(courseId);

        validateFutureCourse(course, "register");
        BigInteger price = calculatePrice(course, student);

        Registration registration = Registration.builder()
                .student(student)
                .course(course)
                .price(price)
                .registeredDate(LocalDateTime.now())
                .build();
        registrationRepository.save(registration);

        return getFutureCourses(student);
    }

    public List<Course> getRegisteredCourses(String email) {
        return getFutureCourses(findStudentByEmail(email));
    }

    public boolean unregisterCourse(Long courseId, String email) {
        Student student = findStudentByEmail(email);
        Course course = findCourseById(courseId);

        validateFutureCourse(course, "unregister");
        registrationRepository.deleteByStudentAndCourse(student, course);
        return true;
    }

    private Student findStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            throw new IllegalArgumentException("Student with email " + email + " not found");
        }
        return student;
    }

    private Course findCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with ID " + courseId + " not found"));
    }

    private void validateFutureCourse(Course course, String action) {
        if (course.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot " + action + " a past course");
        }
    }

    private BigInteger calculatePrice(Course course, Student student) {
        BigInteger price = course.getPrice();
        if (registrationRepository.findByStudent(student).size() >= 2) {
            price = price.multiply(BigInteger.valueOf(75)).divide(BigInteger.valueOf(100));
        }
        return price;
    }

    private List<Course> getFutureCourses(Student student) {
        return registrationRepository.findByStudent(student).stream()
                .map(Registration::getCourse)
                .filter(course -> course.getStartTime().isAfter(LocalDateTime.now()))
                .toList();
    }
}
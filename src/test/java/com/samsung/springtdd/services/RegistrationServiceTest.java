package com.samsung.springtdd.services;

import com.samsung.springtdd.models.Course;
import com.samsung.springtdd.models.Registration;
import com.samsung.springtdd.models.Student;
import com.samsung.springtdd.models.repository.CourseRepository;
import com.samsung.springtdd.models.repository.RegistrationRepository;
import com.samsung.springtdd.models.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private Student student;
    private Course futureCourse;
    private Course pastCourse;

    @BeforeEach
    void setup() {
        student = Student.builder()
                .id(1L)
                .email("student1@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        futureCourse = Course.builder()
                .id(1L)
                .name("Java Basics")
                .startTime(LocalDateTime.of(2025, 4, 1, 9, 0))
                .endTime(LocalDateTime.of(2025, 4, 30, 17, 0))
                .price(BigInteger.valueOf(1000000))
                .build();

        pastCourse = Course.builder()
                .id(2L)
                .name("Old Course")
                .startTime(LocalDateTime.of(2023, 1, 1, 9, 0))
                .endTime(LocalDateTime.of(2023, 1, 31, 17, 0))
                .price(BigInteger.valueOf(800000))
                .build();
    }

    @Test
    void shouldRegisterCourseSuccessfully() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(futureCourse));
        when(registrationRepository.findByStudent(student)).thenReturn(Collections.emptyList());
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Course> registeredCourses = registrationService.registerCourse(1L, "student1@example.com");

        assertNotNull(registeredCourses);
        assertEquals(1, registeredCourses.size());
        assertEquals(futureCourse, registeredCourses.get(0));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void shouldApplyDiscountWhenRegisteringThirdCourse() {
        Registration reg1 = Registration.builder()
                .student(student)
                .course(futureCourse)
                .price(BigInteger.valueOf(1000000))
                .registeredDate(LocalDateTime.now())
                .build();
        Registration reg2 = Registration.builder()
                .student(student)
                .course(futureCourse)
                .price(BigInteger.valueOf(1000000))
                .registeredDate(LocalDateTime.now())
                .build();
        List<Registration> existingRegistrations = Arrays.asList(reg1, reg2);

        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(futureCourse));
        when(registrationRepository.findByStudent(student)).thenReturn(existingRegistrations);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> {
            Registration saved = invocation.getArgument(0);
            assertEquals(BigInteger.valueOf(750000), saved.getPrice());
            return saved;
        });

        List<Course> registeredCourses = registrationService.registerCourse(1L, "student1@example.com");

        assertNotNull(registeredCourses);
        assertEquals(2, registeredCourses.size());
        assertEquals(futureCourse, registeredCourses.get(0));
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFoundOnRegister() {
        when(studentRepository.findByEmail("unknown@example.com")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.registerCourse(1L, "unknown@example.com"));

        assertEquals("Student with email unknown@example.com not found", exception.getMessage());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFoundOnRegister() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.registerCourse(1L, "student1@example.com"));

        assertEquals("Course with ID 1 not found", exception.getMessage());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringPastCourse() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(2L)).thenReturn(Optional.of(pastCourse));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                registrationService.registerCourse(2L, "student1@example.com"));

        assertEquals("Cannot register a past course", exception.getMessage());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    void shouldGetRegisteredCoursesSuccessfully() {
        Registration reg = Registration.builder()
                .student(student)
                .course(futureCourse)
                .price(BigInteger.valueOf(1000000))
                .registeredDate(LocalDateTime.now())
                .build();

        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(registrationRepository.findByStudent(student)).thenReturn(Collections.singletonList(reg));

        List<Course> courses = registrationService.getRegisteredCourses("student1@example.com");

        assertNotNull(courses);
        assertEquals(1, courses.size());
        assertEquals(futureCourse, courses.get(0));
    }

    @Test
    void shouldReturnEmptyListWhenNoRegisteredCourses() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(registrationRepository.findByStudent(student)).thenReturn(Collections.emptyList());

        List<Course> courses = registrationService.getRegisteredCourses("student1@example.com");

        assertNotNull(courses);
        assertEquals(0, courses.size());
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFoundOnGetRegisteredCourses() {
        when(studentRepository.findByEmail("unknown@example.com")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.getRegisteredCourses("unknown@example.com"));

        assertEquals("Student with email unknown@example.com not found", exception.getMessage());
    }

    @Test
    void shouldUnregisterCourseSuccessfully() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(futureCourse));
        doNothing().when(registrationRepository).deleteByStudentAndCourse(student, futureCourse);

        boolean result = registrationService.unregisterCourse(1L, "student1@example.com");

        assertTrue(result);
        verify(registrationRepository, times(1)).deleteByStudentAndCourse(student, futureCourse);
    }

    @Test
    void shouldThrowExceptionWhenUnregisteringPastCourse() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(2L)).thenReturn(Optional.of(pastCourse));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                registrationService.unregisterCourse(2L, "student1@example.com"));

        assertEquals("Cannot unregister a past course", exception.getMessage());
        verify(registrationRepository, never()).deleteByStudentAndCourse(any(Student.class), any(Course.class));
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFoundOnUnregister() {
        when(studentRepository.findByEmail("unknown@example.com")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.unregisterCourse(1L, "unknown@example.com"));

        assertEquals("Student with email unknown@example.com not found", exception.getMessage());
        verify(registrationRepository, never()).deleteByStudentAndCourse(any(Student.class), any(Course.class));
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFoundOnUnregister() {
        when(studentRepository.findByEmail("student1@example.com")).thenReturn(student);
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registrationService.unregisterCourse(999L, "student1@example.com"));

        assertEquals("Course with ID 999 not found", exception.getMessage());
        verify(registrationRepository, never()).deleteByStudentAndCourse(any(Student.class), any(Course.class));
    }
}
package com.samsung.springtdd.controller;

import com.samsung.springtdd.controllers.RegistrationController;
import com.samsung.springtdd.models.Course;
import com.samsung.springtdd.models.Student;
import com.samsung.springtdd.services.RegistrationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    private Student student;
    private List<Course> futureCourses;

    @BeforeEach
    void setup() {
        student = new Student();
        student.setId(1L);
        student.setEmail("student1@example.com");
        student.setFirstName("John");
        student.setLastName("Doe");

        futureCourses = Arrays.asList(
                Course.builder()
                        .id(1L)
                        .name("Java Basics")
                        .startTime(LocalDateTime.of(2025, 4, 1, 9, 0))
                        .endTime(LocalDateTime.of(2025, 4, 30, 17, 0))
                        .price(BigInteger.valueOf(1000000))
                        .build(),
                Course.builder()
                        .id(2L)
                        .name("Spring Boot")
                        .startTime(LocalDateTime.of(2025, 5, 1, 9, 0))
                        .endTime(LocalDateTime.of(2025, 5, 31, 17, 0))
                        .price(BigInteger.valueOf(1500000))
                        .build()
        );
    }

    @Test
    public void shouldRegisterCourseSuccessfully() throws Exception {
        String requestJson = "{\"courseId\": 1, \"email\": \"student1@example.com\"}";
        when(registrationService.registerCourse(1L, "student1@example.com")).thenReturn(futureCourses);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Java Basics")))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)));
    }

    @Test
    public void shouldReturnRegisteredCourses() throws Exception {
        when(registrationService.getRegisteredCourses("student1@example.com")).thenReturn(futureCourses);

        mockMvc.perform(get("/registered-courses/student1@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Java Basics")))
                .andExpect(jsonPath("$[1].name", Matchers.is("Spring Boot")));
    }

    @Test
    public void shouldUnregisterCourseSuccessfully() throws Exception {
        when(registrationService.unregisterCourse(1L, "student1@example.com")).thenReturn(true);

        mockMvc.perform(delete("/unregister/1/student1@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Unregistered successfully"));
    }

    @Test
    public void shouldFailWhenRegisteringPastCourse() throws Exception {
        String requestJson = "{\"courseId\": 3, \"email\": \"student1@example.com\"}";
        when(registrationService.registerCourse(3L, "student1@example.com"))
                .thenThrow(new IllegalStateException("Cannot register for a past course"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("Cannot register for a past course")));
    }

    @Test
    public void shouldFailWhenStudentNotFound() throws Exception {
        String requestJson = "{\"courseId\": 1, \"email\": \"unknown@example.com\"}";
        when(registrationService.registerCourse(1L, "unknown@example.com"))
                .thenThrow(new IllegalArgumentException("Student with email unknown@example.com not found"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("Student with email unknown@example.com not found")));
    }

    @Test
    public void shouldFailWhenRegisterRequestBodyInvalid() throws Exception {
        String invalidJson = "{\"courseId\": \"invalid\", \"email\": \"student1@example.com\"}";

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailWhenUnregisteringNonExistentCourse() throws Exception {
        when(registrationService.unregisterCourse(999L, "student1@example.com"))
                .thenThrow(new IllegalArgumentException("Course with ID 999 not found"));

        mockMvc.perform(delete("/unregister/999/student1@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.is("Course with ID 999 not found")));
    }
}

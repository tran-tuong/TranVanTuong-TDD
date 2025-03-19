package com.samsung.springtdd.controllers;

import com.samsung.springtdd.models.Course;
import com.samsung.springtdd.models.DTO.RegistrationRequest;
import com.samsung.springtdd.services.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<List<Course>> register(@RequestBody RegistrationRequest request) {
        List<Course> registeredCourses = registrationService.registerCourse(request.getCourseId(), request.getEmail());
        return ResponseEntity.ok(registeredCourses);
    }

    @GetMapping("/registered-courses/{email}")
    public ResponseEntity<List<Course>> getRegisteredCourses(@PathVariable String email) {
        List<Course> courses = registrationService.getRegisteredCourses(email);
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/unregister/{courseId}/{email}")
    public ResponseEntity<String> unregister(@PathVariable Long courseId, @PathVariable String email) {
        boolean success = registrationService.unregisterCourse(courseId, email);
        return ResponseEntity.ok("Unregistered successfully");
    }
}


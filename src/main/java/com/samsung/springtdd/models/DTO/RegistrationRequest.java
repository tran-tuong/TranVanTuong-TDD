package com.samsung.springtdd.models.DTO;

public class RegistrationRequest {
    private Long courseId;
    private String email;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.philosophia.dto.authentification;


import com.philosophia.dto.student.StudentResponse;

public record LoginResponse(String username, String role, String token, StudentResponse profile) {}
package vn.edu.ptithcm.mindcard.dto.request.auth;

public record LoginRequest (
        String identity,
        String password
){ }

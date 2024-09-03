package com.example.springJwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springJwt.model.AuthenticationResponse;
import com.example.springJwt.model.User;
import com.example.springJwt.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authService;

	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody User request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
		return ResponseEntity.ok(authService.authenticate(request));
	}

	@PostMapping("/refresh_token")
	public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request,
			HttpServletResponse response) {
		return authService.refreshToken(request, response);
	}
}

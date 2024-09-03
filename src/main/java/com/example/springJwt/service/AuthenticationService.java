package com.example.springJwt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springJwt.model.AuthenticationResponse;
import com.example.springJwt.model.Token;
import com.example.springJwt.model.User;
import com.example.springJwt.repository.TokenRepository;
import com.example.springJwt.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthenticationService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	public User register(User request) {
		if (repository.findByUsername(request.getUsername()).isPresent()) {
			System.out.println("User already exists");
			return null;
		}
		User user = new User();
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole());
		user = repository.save(user);
		return user;
	}

	public AuthenticationResponse authenticate(User request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			User user = repository.findByUsername(request.getUsername()).orElseThrow();
			String accessToken = jwtService.generateAccessToken(user);
			String refreshToken = jwtService.generateRefreshToken(user);
			revokeAllTokenByUser(user);
			saveUserToken(accessToken, refreshToken, user);
			return new AuthenticationResponse(accessToken, refreshToken, "User login was successful");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new AuthenticationResponse(null, null, "User login not successful");
	}

	private void revokeAllTokenByUser(User user) {
		List<Token> validTokens = tokenRepository.findActiveTokensByUserId(user.getId());
		if (validTokens.isEmpty()) {
			return;
		}
		validTokens.forEach(t -> {
			t.setLoggedOut(true);
		});
		tokenRepository.saveAll(validTokens);
	}

	private void saveUserToken(String accessToken, String refreshToken, User user) {
		Token token = new Token();
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		token.setLoggedOut(false);
		token.setUser(user);
		tokenRepository.save(token);
	}

	public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request,
			HttpServletResponse response) {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return new ResponseEntity<AuthenticationResponse>(HttpStatus.UNAUTHORIZED);
		}
		String token = authHeader.substring(7);
		String username = jwtService.extractUsername(token);
		User user = repository.findByUsername(username).orElseThrow(() -> new RuntimeException("No user found"));
		if (jwtService.isValidRefreshToken(token, user)) {
			String accessToken = jwtService.generateAccessToken(user);
			String refreshToken = jwtService.generateRefreshToken(user);
			revokeAllTokenByUser(user);
			saveUserToken(accessToken, refreshToken, user);
			return new ResponseEntity<AuthenticationResponse>(
					new AuthenticationResponse(accessToken, refreshToken, "New token generated"), HttpStatus.OK);
		}
		return new ResponseEntity<AuthenticationResponse>(HttpStatus.UNAUTHORIZED);

	}
}

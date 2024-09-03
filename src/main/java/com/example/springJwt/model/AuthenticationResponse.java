package com.example.springJwt.model;

public class AuthenticationResponse {
	private String accessToken;
	private String refreshToken;
	private String message;

	public AuthenticationResponse(String accessToken, String refreshToken, String message) {
		this.accessToken = accessToken;
		this.message = message;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getMessage() {
		return message;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

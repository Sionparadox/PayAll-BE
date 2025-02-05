package com.example.PayAll_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
	private int code;
	private String accessToken;
	private String refreshToken;
	private String message;
	private String status;
}

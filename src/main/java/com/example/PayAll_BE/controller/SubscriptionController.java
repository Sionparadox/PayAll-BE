package com.example.PayAll_BE.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.dto.Subscription.SubscriptionResponseDto;
import com.example.PayAll_BE.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
	private final SubscriptionService subscriptionService;

	@GetMapping
	public ResponseEntity<ApiResult> getAllSubscriptions() {
		List<SubscriptionResponseDto> subscriptions = subscriptionService.getAllSubscriptions();
		return ResponseEntity.ok(new ApiResult(200, "OK", "전체 구독 조회 성공", subscriptions));
	}
}
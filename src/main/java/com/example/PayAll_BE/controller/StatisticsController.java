package com.example.PayAll_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PayAll_BE.dto.ApiResult;
import com.example.PayAll_BE.service.StatisticsService;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
	private final StatisticsService statisticsService;

	@GetMapping
	public ResponseEntity<ApiResult> getStatistics(
		@RequestParam Long userId,
		@RequestParam String date
	) {
		return ResponseEntity.ok(new ApiResult(200, "OK", "소비분석 조회 성공", statisticsService.getStatistics(userId, date)));
	}

}

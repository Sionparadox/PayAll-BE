package com.example.PayAll_BE.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public class CardRecommendationResultDto {
		private String cardName;
		private String paymentPlace;
		private BigDecimal discountAmount;
}

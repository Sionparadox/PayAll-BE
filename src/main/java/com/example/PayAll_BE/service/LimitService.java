package com.example.PayAll_BE.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Limit.LimitRequestDto;
import com.example.PayAll_BE.dto.Limit.LimitResponseDto;
import com.example.PayAll_BE.entity.Limit;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.repository.LimitRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LimitService {
	private final LimitRepository limitRepository;
	private final UserRepository userRepository;

	public LimitResponseDto registerLimit(Long userId, LimitRequestDto limitRequestDto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		long limitPrice = limitRequestDto.getLimitPrice();

		Limit limit = Limit.builder()
			.user(user)
			.limitPrice(limitPrice)
			.limitDate(LocalDateTime.now())
			.build();

		Limit savedLimit = limitRepository.save(limit);

		return LimitResponseDto.builder()
			.limitId(savedLimit.getLimitId())
			.userId(userId)
			.limitPrice(savedLimit.getLimitPrice())
			.limitDate(savedLimit.getLimitDate())
			.build();
	}

	public LimitResponseDto getLimit(Long userId) {
		// 가장 최근 소비 목표 조회
		Limit limit = limitRepository.findTopByUser_IdOrderByLimitDateDesc(userId)
			.orElseThrow(() -> new IllegalArgumentException("소비 목표가 존재하지 않습니다."));

		return LimitResponseDto.builder()
			.limitId(limit.getLimitId())
			.userId(limit.getUser().getId())
			.limitPrice(limit.getLimitPrice())
			.limitDate(limit.getLimitDate())
			.build();
	}
}

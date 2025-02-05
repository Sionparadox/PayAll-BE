package com.example.PayAll_BE.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.PayAll_BE.dto.Statistics.StatisticsDetailResponseDto;
import com.example.PayAll_BE.dto.Statistics.StatisticsResponseDto;
import com.example.PayAll_BE.entity.Payment;
import com.example.PayAll_BE.entity.Statistics;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.entity.enums.StatisticsCategory;
import com.example.PayAll_BE.repository.PaymentRepository;
import com.example.PayAll_BE.repository.StatisticsRepository;
import com.example.PayAll_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsService {
	private final StatisticsRepository statisticsRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;

	public StatisticsResponseDto getStatistics(Long userId, String date) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

		LocalDate startDate = LocalDate.parse(date + "-01");
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = startDate.plusMonths(1).atStartOfDay().minusSeconds(1);

		List<Statistics> statistics = statisticsRepository.findByUserIdAndStatisticsDateBetween(1L, startDateTime, endDateTime);

		// 총 지출 계산
		long totalSpent = statistics.stream().mapToLong(Statistics::getStatisticsAmount).sum();

		// 카테고리별 지출 계산
		List<StatisticsResponseDto.CategoryExpense> categoryExpenses = statistics.stream()
			.collect(Collectors.groupingBy(
				Statistics::getStatisticsCategory,
				Collectors.summingLong(Statistics::getStatisticsAmount)
			))
			.entrySet().stream()
			.map(entry -> new StatisticsResponseDto.CategoryExpense(
				entry.getKey().ordinal(), // 카테고리 ID
				entry.getKey().getCategory(), // 카테고리 이름
				entry.getValue() // 지출 금액
			))
			.collect(Collectors.toList());

		int daysInMonth = startDate.lengthOfMonth(); // 월별 일수
		long dateAverage = totalSpent / daysInMonth; // 하루 평균 지출

		// 전월 대비 차이 계산
		LocalDate previousMonthStart = startDate.minusMonths(1);
		LocalDateTime previousStartDateTime = previousMonthStart.atStartOfDay();
		LocalDateTime previousEndDateTime = previousMonthStart.plusMonths(1).atStartOfDay().minusSeconds(1);

		List<Statistics> previousStatistics = statisticsRepository.findByUserIdAndStatisticsDateBetween(
			userId, previousStartDateTime, previousEndDateTime);
		long previousTotalSpent = previousStatistics.stream().mapToLong(Statistics::getStatisticsAmount).sum();
		long difference = totalSpent - previousTotalSpent;

		return StatisticsResponseDto.builder()
			.name(user.getName())
			.date(date)
			.totalSpent(totalSpent)
			.dateAverage(dateAverage) // 하루 평균 지출
			.difference(difference) // 전월 대비 차이
			.categoryExpenses(categoryExpenses)
			.fixedExpenses(null) // 고정 지출(일단 비움)
			.build();
	}

	public StatisticsDetailResponseDto getCategoryDetails(Long userId, StatisticsCategory category, String date) {

		LocalDate startDate = LocalDate.parse(date + "-01");
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = startDate.plusMonths(1).atStartOfDay().minusSeconds(1);

		// 해당 카테고리의 결제 내역 조회
		List<Payment> payments = paymentRepository.findByAccount_User_IdAndCategoryAndPaymentTimeBetween(
			userId, category, startDateTime, endDateTime
		);

		// 총 소비 금액 계산
		long totalSpent = payments.stream().mapToLong(Payment::getPrice).sum();

		// 소비 내역이 없을 경우
		if (payments.isEmpty()) {
			return StatisticsDetailResponseDto.builder()
				.categoryId(category.ordinal())
				.categoryName(category.name())
				.totalSpent(0)
				.transactions(List.of())
				.build();
		}

		// 날짜별 소비 내역 그룹화
		Map<LocalDate, List<Payment>> paymentsByDate = payments.stream()
			.collect(Collectors.groupingBy(payment -> payment.getPaymentTime().toLocalDate()));

		List<StatisticsDetailResponseDto.TransactionDetail> transactionDetails = paymentsByDate.entrySet().stream()
			.map(entry -> {
				LocalDate transactionDate = entry.getKey();
				List<Payment> dailyPayments = entry.getValue();

				// 날짜별 소비 금액
				long dateSpent = dailyPayments.stream().mapToLong(Payment::getPrice).sum();

				// 세부 내역 리스트 생성
				List<StatisticsDetailResponseDto.TransactionDetail.HistoryDetail> historyDetails = dailyPayments.stream()
					.map(payment -> new StatisticsDetailResponseDto.TransactionDetail.HistoryDetail(
						payment.getPaymentPlace(),
						category.name(), // 태그? 배지? -> 카테고리 이름 사용
						payment.getPrice(),
						payment.getPaymentType().name(),
						payment.getPaymentTime().toLocalTime().toString()
					))
					.collect(Collectors.toList());

				return new StatisticsDetailResponseDto.TransactionDetail(
					transactionDate.toString(),
					dateSpent,
					historyDetails
				);
			})
			.collect(Collectors.toList());

		return StatisticsDetailResponseDto.builder()
			.categoryId(category.ordinal())
			.categoryName(category.name())
			.totalSpent(totalSpent)
			.transactions(transactionDetails)
			.build();
	}
}

package com.example.PayAll_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PayAll_BE.entity.Limit;

public interface LimitRepository extends JpaRepository<Limit, Long> {

	Optional<Limit> findTopByUser_IdOrderByLimitDateDesc(Long userId);
}
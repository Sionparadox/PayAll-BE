package com.example.PayAll_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.PayAll_BE.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUserIdAndProductId(Long userId, Long productId);

	List<Cart> findAllByUserId(Long userId);
}

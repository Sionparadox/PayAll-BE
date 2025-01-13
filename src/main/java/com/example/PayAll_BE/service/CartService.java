package com.example.PayAll_BE.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.PayAll_BE.dto.Cart.CartRequestDto;
import com.example.PayAll_BE.dto.Cart.CartResponseDto;
import com.example.PayAll_BE.dto.ProductDto;
import com.example.PayAll_BE.entity.Cart;
import com.example.PayAll_BE.entity.User;
import com.example.PayAll_BE.exception.BadRequestException;
import com.example.PayAll_BE.exception.ForbiddenException;
import com.example.PayAll_BE.exception.NotFoundException;
import com.example.PayAll_BE.mapper.CartMapper;
import com.example.PayAll_BE.repository.CartRepository;
import com.example.PayAll_BE.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final RestTemplate restTemplate;

	private final String productApiUrl = "http://localhost:8081/redis/product/";
	private final UserRepository userRepository;

	public CartResponseDto addCart(CartRequestDto cartRequestDto) {

		User user = userRepository.findById(cartRequestDto.getUserId())
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		// 상품 정보 조회
		ResponseEntity<ProductDto> response = restTemplate.getForEntity(
			productApiUrl + cartRequestDto.getProductId().toString(),
			ProductDto.class);
		if (response.getBody() == null) {
			throw new NotFoundException("상품 id가 없습니다.");
		}
		ProductDto productDto = response.getBody();

		// 장바구니에 같은 상품 있으면 수량 +1
		Cart existingCart = cartRepository.findByUserIdAndProductId(cartRequestDto.getUserId(),
			cartRequestDto.getProductId());
		if (existingCart != null) {
			existingCart.setQuantity(existingCart.getQuantity() + 1);
			return CartMapper.toDto(cartRepository.save(existingCart));
		}

		Cart cart = Cart.builder()
			.user(user)
			.productId(cartRequestDto.getProductId())
			.productName(productDto.getProductName())
			.productPrice(productDto.getPrice())
			.quantity(1)
			.link(productDto.getShopUrl())
			.image(productDto.getShopImage())
			.storeName(productDto.getShopName())
			.build();

		return CartMapper.toDto(cartRepository.save(cart));

	}

	public List<CartResponseDto> getCarts(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

		List<Cart> carts = cartRepository.findAllByUserId(userId);

		return carts.stream().map(CartMapper::toDto).toList();

	}

	public void updateQuantity(Long cartId, int quantity, Long userId) {
		if (quantity < 1) {
			throw new BadRequestException("수량은 1 이상이어야 합니다.");
		}

		Cart cart = cartRepository.findById(cartId)
			.orElseThrow(() -> new NotFoundException("해당 장바구니 항목을 찾을 수 없습니다."));

		if (!cart.getUser().getId().equals(userId)) {
			throw new ForbiddenException("장바구니 수량을 수정할 수 없습니다.");
		}

		cart.setQuantity(quantity);
		cartRepository.save(cart);

	}

	public void deleteCart(Long cartId, Long userId) {

		Cart cart = cartRepository.findById(cartId)
			.orElseThrow(() -> new EntityNotFoundException("해당 장바구니 항목을 찾을 수 없습니다."));

		if (!cart.getUser().getId().equals(userId)) {
			throw new ForbiddenException("장바구니를 삭제할 수 없습니다.");
		}

		cartRepository.delete(cart);
	}

	public void deleteCarts(List<Long> cartIds, Long userId) {

		List<Cart> carts = cartRepository.findAllById(cartIds);

		carts.forEach(
			cart -> {
				if (!cart.getUser().getId().equals(userId)) {
					throw new ForbiddenException("장바구니를 삭제할 수 없습니다.");
				}
			}
		);

		cartRepository.deleteAllById(cartIds);
	}

}
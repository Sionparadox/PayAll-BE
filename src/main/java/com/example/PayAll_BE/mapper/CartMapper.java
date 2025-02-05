package com.example.PayAll_BE.mapper;

import com.example.PayAll_BE.dto.Cart.CartResponseDto;
import com.example.PayAll_BE.entity.Cart;

public class CartMapper {
	public static CartResponseDto toDto(Cart cart) {
		return CartResponseDto.builder()
			.cartId(cart.getCartId())
			.productId(cart.getProductId())
			.productName(cart.getProductName())
			.productImage(cart.getImage())
			.price(cart.getProductPrice())
			.quantity(cart.getQuantity())
			.storeName(cart.getStoreName())
			.link(cart.getLink())
			.totalPrice(cart.getQuantity() * cart.getProductPrice())
			.build();

	}
}

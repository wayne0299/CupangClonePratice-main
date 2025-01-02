package com.github.cupangclone.service;

import com.github.cupangclone.repository.cart.Cart;
import com.github.cupangclone.repository.cart.CartRepository;
import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.items.ItemsRepository;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import com.github.cupangclone.repository.userPrincipal.UserPrincipalRepository;
import com.github.cupangclone.web.dto.cart.CartResponse;
import com.github.cupangclone.web.dto.cart.CartRequest;
import com.github.cupangclone.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemsRepository itemsRepository;
    private final UserPrincipalRepository userPrincipalRepository;

    // 장바구니에 아이템을 추가하는 메서드
    @Transactional(transactionManager = "tmJpa1")
    public String addItemToCart(String email, CartRequest cartRequest) {
        // 사용자가 존재하는지 확인
        UserPrincipal user = userPrincipalRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // 아이템이 존재하는지 확인
        Items item = itemsRepository.findById(cartRequest.getItemId()).orElseThrow(() -> new NotFoundException("아이템을 찾을 수 없습니다."));

        // 장바구니에 이미 해당 아이템이 있는지 확인
        Optional<Cart> existingCart = cartRepository.findByUserPrincipalAndItem(user, item);
        if (existingCart.isPresent()) {
            // 장바구니에 이미 존재하면 수량만 증가하고 updatedAt 갱신
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + cartRequest.getQuantity());
            cart.setUpdatedAt(LocalDateTime.now());  // 수량이 변경될 때 updatedAt 갱신
            cartRepository.save(cart);
        } else {
            // 장바구니에 없는 아이템이면 새로 추가
            Cart cart = Cart.builder().userPrincipal(user).item(item).quantity(cartRequest.getQuantity()).updatedAt(LocalDateTime.now())  // 첫 추가 시 updatedAt도 설정
                    .build();
            cartRepository.save(cart);
        }

        return "아이템이 장바구니에 추가되었습니다.";
    }


    // 장바구니에서 아이템을 삭제하는 메서드
    @Transactional
    public String removeItemFromCart(String email, Long cartItemId) {
        // 사용자가 존재하는지 확인
        UserPrincipal user = userPrincipalRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // 장바구니에서 해당 아이템 찾기
        Cart cartItem = cartRepository.findByIdAndUserPrincipal(cartItemId, user)
                .orElseThrow(() -> new NotFoundException("장바구니에서 해당 아이템을 찾을 수 없습니다."));

        // 아이템 삭제
        cartRepository.delete(cartItem);

        return "아이템이 장바구니에서 삭제되었습니다.";
    }


    // 장바구니 아이템 수량 변경
    @Transactional(transactionManager = "tmJpa1")
    public String updateItemQuantity(String email, CartRequest cartRequest) {
        UserPrincipal user = userPrincipalRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // 장바구니에서 해당 아이템 찾기
        Cart cartItem = cartRepository.findByIdAndUserPrincipal(cartRequest.getItemId(), user)
                .orElseThrow(() -> new NotFoundException("장바구니에서 해당 아이템을 찾을 수 없습니다."));

        // 수량이 0 이하일 경우, 아이템 삭제 처리
        if (cartRequest.getQuantity() <= 0) {
            cartRepository.delete(cartItem);
            return "아이템이 장바구니에서 삭제되었습니다.";
        }

        cartItem.setQuantity(cartRequest.getQuantity());  // 수량 변경
        cartItem.setUpdatedAt(LocalDateTime.now());  // 수량 변경 시 updatedAt 갱신
        cartRepository.save(cartItem);

        return "아이템 수량이 변경되었습니다.";
    }



    // 장바구니에 있는 모든 아이템을 가져오는 메서드
    public List<CartResponse> getCartItems(String email) {
        // 사용자가 존재하는지 확인
        UserPrincipal user = userPrincipalRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // 장바구니에서 사용자의 아이템들을 조회
        List<Cart> cartItems = cartRepository.findByUserPrincipal(user);

        // DTO로 변환하여 반환
        return cartItems.stream()
                .map(cart -> CartResponse.fromItem(cart.getItem(), cart.getQuantity())) // fromItem 메서드 사용
                .toList();
    }
}

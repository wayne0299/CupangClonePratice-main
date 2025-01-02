package com.github.cupangclone.web.controller;

import com.github.cupangclone.service.AuthService;
import com.github.cupangclone.service.CartService;
import com.github.cupangclone.web.dto.cart.CartRequest;
import com.github.cupangclone.web.dto.cart.CartResponse;
import com.github.cupangclone.web.exceptions.NotAcceptResponse;
import com.github.cupangclone.web.exceptions.NotFoundResponse;
import com.github.cupangclone.web.exceptions.SuccessResponse;
import com.github.cupangclone.web.exceptions.responMessage.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    // 인증 확인 후 이메일을 반환하는 메서드
    private String getEmailFromRequest(HttpServletRequest request) {
        return authService.blockAccessWithOnlyToken(request);
    }

    /**
     * 장바구니에 아이템 추가
     */
    @PostMapping("/add")
    public ResponseEntity<Message> addItemToCart(@RequestBody CartRequest cartRequest, HttpServletRequest request) {
        // cartRequest를 사용하려면 여기서 잘 정의된 것을 확인
        String email = authService.blockAccessWithOnlyToken(request);
        if (email != null) {
            // cartRequest.getQuantity() 등을 호출하여 사용
            cartService.addItemToCart(email, cartRequest);
            return new SuccessResponse().sendMessage("장바구니에 아이템이 추가되었습니다.");
        } else {
            return new NotAcceptResponse().sendMessage("잘못된 접근입니다.");
        }
    }


    /**
     * 장바구니에 담긴 모든 아이템 조회
     */
    @GetMapping("/all")
    public ResponseEntity<Message> getAllCartItems(HttpServletRequest request) {
        String email = getEmailFromRequest(request);

        if (email == null) {
            return new NotAcceptResponse().sendMessage("잘못된 접근입니다.");
        }

        List<CartResponse> cartItems = cartService.getCartItems(email);
        return new SuccessResponse().getMessageResponseEntity(cartItems);
    }

    /**
     * 장바구니 아이템 수량 업데이트
     */
    @PutMapping("/update_quantity")
    public ResponseEntity<Message> updateCartItemQuantity(@RequestParam Long itemId, @RequestParam int quantity, HttpServletRequest request) {
        String email = getEmailFromRequest(request);

        if (email == null) {
            return new NotAcceptResponse().sendMessage("잘못된 접근입니다.");
        }

        // CartRequest 객체 생성
        CartRequest cartRequest = new CartRequest();
        cartRequest.setItemId(itemId);  // 아이템 ID 설정
        cartRequest.setQuantity((long) quantity);  // 수량 설정 (Long으로 변환)

        // updateItemQuantity 메서드 호출
        String message = cartService.updateItemQuantity(email, cartRequest);

        if (message.contains("아이템 수량이 변경되었습니다.")) {
            return new SuccessResponse().sendMessage(message);
        } else {
            return new NotFoundResponse().sendMessage(message);
        }
    }



    /**
     * 장바구니에서 아이템 삭제
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Message> removeItemFromCart(@RequestParam Long itemId, HttpServletRequest request) {
        String email = getEmailFromRequest(request);

        if (email == null) {
            return new NotAcceptResponse().sendMessage("잘못된 접근입니다.");
        }

        String message = cartService.removeItemFromCart(email, itemId);
        if (message.contains("장바구니에서 아이템이 삭제되었습니다.")) {
            return new SuccessResponse().sendMessage(message);
        } else {
            return new NotFoundResponse().sendMessage(message);
        }
    }
}

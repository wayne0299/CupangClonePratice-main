package com.github.cupangclone.web.dto.cart;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartRequest {

    private Long itemId;    // 장바구니에 추가할 아이템의 ID
    private Long quantity;  // 장바구니에 추가할 아이템의 수량

}

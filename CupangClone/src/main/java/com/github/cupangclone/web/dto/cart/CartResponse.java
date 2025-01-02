package com.github.cupangclone.web.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.github.cupangclone.repository.items.Items;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private Long itemId;          // 아이템 ID
    private String itemName;      // 아이템 이름
    private Long quantity;        // 아이템 수량 (Long 타입으로 수정)
    private long itemPrice;       // 아이템 가격
    private long totalPrice;      // 아이템 총 가격 (수량 * 가격)

    // 장바구니 아이템을 CartResponse로 변환하는 메서드
    public static CartResponse fromItem(Items item, Long quantity) {
        long totalPrice = item.getItemPrice() * quantity;
        return new CartResponse(
                item.getItemId(),
                item.getItemName(),
                quantity,
                item.getItemPrice(),
                totalPrice
        );
    }
}



package com.github.cupangclone.repository.cart;

import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserPrincipal userPrincipal;  // 장바구니의 사용자

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Items item;  // 장바구니에 담긴 아이템

    private Long quantity;  // 아이템 수량

    private LocalDateTime updatedAt;  // 장바구니 아이템이 수정된 시간

}

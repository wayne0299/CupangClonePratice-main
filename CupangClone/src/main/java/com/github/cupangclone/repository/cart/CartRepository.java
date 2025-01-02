package com.github.cupangclone.repository.cart;

import com.github.cupangclone.repository.items.Items;
import com.github.cupangclone.repository.userPrincipal.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // 특정 사용자와 아이템에 대한 장바구니 항목을 조회
    Optional<Cart> findByUserPrincipalAndItem(UserPrincipal userPrincipal, Items item);

    // 특정 사용자의 모든 장바구니 아이템을 조회
    List<Cart> findByUserPrincipal(UserPrincipal userPrincipal);

    // 특정 사용자와 장바구니 아이템 ID로 장바구니 항목을 조회
    Optional<Cart> findByIdAndUserPrincipal(Long cartItemId, UserPrincipal userPrincipal);
}

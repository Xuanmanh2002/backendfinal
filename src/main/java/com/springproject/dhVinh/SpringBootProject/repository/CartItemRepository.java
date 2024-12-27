package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.CartItem;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci JOIN ci.carts c WHERE c.admins.id = :adminId")
    List<CartItem> findByAdmin(@Param("adminId") Long adminId);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.carts.id = :cartId")
    long sumQuantityByCart(@Param("cartId") Long cartId);

    @Query("SELECT COALESCE(SUM(ci.totalPrice), 0) FROM CartItem ci WHERE ci.carts.id = :cartId")
    double sumTotalPriceByCart(@Param("cartId") Long cartId);

    @Query("SELECT COALESCE(SUM(ci.totalValidityPeriod), 0) FROM CartItem ci WHERE ci.carts.id = :cartId")
    long sumValidityPeriodByCart(@Param("cartId") Long cartId);

    @Query("SELECT COALESCE(SUM(ci.totalBenefit), 0) FROM CartItem ci WHERE ci.carts.id = :cartId")
    long sumBenefitByCart(@Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.services.id = :serviceId")
    CartItem findByService(@Param("serviceId") Long serviceId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.carts = :cart")
    Set<CartItem> findByCart(@Param("cart") Cart cart);
}

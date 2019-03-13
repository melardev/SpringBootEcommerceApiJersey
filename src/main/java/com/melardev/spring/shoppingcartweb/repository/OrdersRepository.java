package com.melardev.spring.shoppingcartweb.repository;


import com.melardev.spring.shoppingcartweb.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

    @Query("Select o from Order o where o.user.id = :id")
    Page<Order> findOrdersByUserId(@Param("id") Long id, Pageable pageRequest);

    @Query("Select o from Order o where o.user.id = ?1")
    List<Order> findOrdersByUserId(Long id);

    @Query("Select o from Order o left join fetch o.orderItems oi join fetch o.address a where o.id = :id")
    Optional<Order> findByIdWithDetails(Long id);

    @Query("Select new com.melardev.spring.shoppingcartweb.models.extensions.OrderExtension(o.id, o.trackingNumber, o.orderStatus, o.createdAt, o.updatedAt, o.address, o.user.id, o.user.username) from Order o where o.id = :id")
    Optional<Order> findByIdWithAddressAndUserBasic(Long id);

    // Used on tutorial on magic methods in my Spring Boot playlist
    List<Order> findAllByUserId(Long id);
    List<Order> getAllByUserId(Long id); // also some really weird names
    List<Order> findByUserId(Long id); // also some really weird names
    Page<Order> findAllByUserId(Long id, Pageable pageable);

    List<Order> findAllByUserUsername(String name);

    Page<Order> findAllByUserUsername(String name, Pageable pageable);

    List<Order> findAllByUser_Username(String name);

    Page<Order> findAllByUser_Username(String name, Pageable pageable);

    List<Order> findAllByAddress_UserUsername(String username);

    List<Order> findAllByUser_RolesName(String roleName);

    Page<Order> findAllByAddress_UserUsername(String username, Pageable pageable);

    List<Order> findAllByOrderItems_Product_Name(String name);

    Page<Order> findAllByOrderItems_Product_Name(String productName, Pageable pageable);

    List<Order> findAllByOrderItems_ProductSlug(String name);

    Page<Order> findAllByOrderItems_Product_Slug(String slug, Pageable pageable);
}

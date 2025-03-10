package org.example.datn_website_be.repository;

import org.example.datn_website_be.dto.response.ProductResponse;
import org.example.datn_website_be.dto.response.ProductViewCustomerReponse;
import org.example.datn_website_be.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("select new org.example.datn_website_be.dto.response.ProductResponse(" +
            "p.id, p.name,p.pricePerBaseUnit,p.quantity, p.baseUnit, c.id, c.name, p.status,p.createdAt" +
            ")" +
            "from Product p " +
            "join p.category c")
    List<ProductResponse> findProductRequests();

    @Query("select new org.example.datn_website_be.dto.response.ProductResponse(" +
            "p.id, p.name,p.pricePerBaseUnit,p.quantity, p.baseUnit, c.id, c.name, p.status,p.createdAt" +
            ")" +
            "from Product p " +
            "join p.category c  where p.id=:id")

    Optional<ProductResponse> findProductRequestsById(@Param("id") Long id);
    @Query("SELECT new org.example.datn_website_be.dto.response.ProductViewCustomerReponse(" +
            "p.id, " +
            "p.name, " +
            "p.quantity, " +
            "p.baseUnit, " +
            "c.id, " +
            "c.name, " +
            "p.pricePerBaseUnit, " +
            "CAST(CASE " +
            "    WHEN prod.status = 'ONGOING' AND pro.status = 'ONGOING' " +
            "    THEN (p.pricePerBaseUnit * (1 - pro.value / 100)) " +
            "    ELSE p.pricePerBaseUnit " +
            "END AS BigDecimal)" +
            ") " +
            "FROM Product p " +
            "INNER JOIN p.category c " +
            "LEFT JOIN p.promotionDetails prod WITH prod.status = 'ONGOING' " +
            "LEFT JOIN prod.promotion pro WITH pro.status = 'ONGOING' " +
            "WHERE p.status = 'ACTIVE'")
    List<ProductViewCustomerReponse> findAllActiveProductsWithPromotion();

}

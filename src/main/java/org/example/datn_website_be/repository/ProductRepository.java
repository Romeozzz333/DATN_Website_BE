package org.example.datn_website_be.repository;

import org.example.datn_website_be.dto.response.ProductImageResponse;
import org.example.datn_website_be.dto.response.ProductProductDetailResponse;
import org.example.datn_website_be.dto.response.ProductResponse;
import org.example.datn_website_be.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
            "p.id, p.name,p.pricePerBaseUnit,p.quantity, p.baseUnit, c.id, c.name, p.status" +
            ")" +
            "from Product p " +
            "join p.category c")
    List<ProductResponse> findProductRequests();

    @Query("select new org.example.datn_website_be.dto.response.ProductResponse(" +
            "p.id, p.name,p.pricePerBaseUnit,p.quantity, p.baseUnit, c.id, c.name, p.status" +
            ")" +
            "from Product p " +
            "join p.category c  where p.id=:id")
    Optional<ProductResponse> findProductRequestsById(@Param("id") Long id);

}

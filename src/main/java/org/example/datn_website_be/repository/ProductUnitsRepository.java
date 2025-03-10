package org.example.datn_website_be.repository;

import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.ProductUnits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductUnitsRepository extends JpaRepository<ProductUnits, Long>{
}

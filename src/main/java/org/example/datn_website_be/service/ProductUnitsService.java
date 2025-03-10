package org.example.datn_website_be.service;

import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.request.ProductUnitsRequest;
import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.model.ProductUnits;
import org.example.datn_website_be.repository.ProductUnitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductUnitsService {
    @Autowired
    ProductUnitsRepository productUnitsRepository;
    @Transactional
    public boolean createProductUnits(Product product, List<ProductUnitsRequest> productUnitRequests) {
        for (ProductUnitsRequest request : productUnitRequests) {
            ProductUnits productUnits = ProductUnits.builder()
                    .unitName(request.getUnitName())
                    .conversionFactor(request.getConversionFactor())
                    .product(product)
                    .type(request.isType())
                    .build();
            productUnits.setStatus(Status.ACTIVE.toString());
            ProductUnits saveProductUnits = productUnitsRepository.save(productUnits);
            System.out.println("saveProductUnits"+saveProductUnits.getUnitName());
        }
        return true;
    }
}

package org.example.datn_website_be.service;

import org.example.datn_website_be.dto.request.UpdateProduct.UpdateProductProductUnitsRequest;
import org.example.datn_website_be.model.ProductUnits;
import org.springframework.transaction.annotation.Transactional;
import org.example.datn_website_be.Enum.Status;
import org.example.datn_website_be.dto.request.ProductRequest;
import org.example.datn_website_be.dto.response.*;
import org.example.datn_website_be.model.Category;
import org.example.datn_website_be.model.Product;
import org.example.datn_website_be.repository.*;
import org.example.datn_website_be.webconfig.NotificationController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductUnitsService productUnitsService;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RandomPasswordGeneratorService randomCodePromotion;

    @Autowired
    ProductUnitsRepository productUnitsRepository;

    @Autowired
    NotificationController notificationController;

    @Transactional
    public void addProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getIdCategory())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài nguyên danh mục trong hệ thống!"));
        if (category.getStatus().equals(Status.INACTIVE.toString())) {
            throw new RuntimeException("Danh mục " + category.getName() + " không còn hoạt động");
        }
        if (productRequest.getListImages().isEmpty()) {
            throw new RuntimeException("Danh sách hình ảnh không được để trống");
        }
        Product product = Product.builder()
                .name(productRequest.getName())
                .baseUnit(productRequest.getBaseUnit())
                .pricePerBaseUnit(productRequest.getPricePerBaseUnit())
                .quantity(productRequest.getQuantity())
                .category(category)
                .build();
        product.setStatus(Status.ACTIVE.toString());
        Product saveProduct = productRepository.save(product);
        boolean checkImage = productImageService.createProductImage(saveProduct, productRequest.getListImages());
        if (!checkImage) {
            throw new RuntimeException("Xảy ra lỗi khi thêm ảnh cho sản phẩm chi tiết");
        }
        boolean checkProductDetail = productUnitsService.createProductUnits(saveProduct, productRequest.getProductUnits());
        if (!checkProductDetail) {
            throw new RuntimeException("Xảy ra lỗi khi thêm sản phẩm chi tiết");
        }
    }

    @Transactional
    public void updateProduct(UpdateProductProductUnitsRequest updateProductProductUnitsRequest) {
        Product product = productRepository.findById(updateProductProductUnitsRequest.getProductRequest().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài nguyên sản phẩm trong hệ thống!"));
        Category category = categoryRepository.findById(updateProductProductUnitsRequest.getProductRequest().getIdCategory())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài nguyên danh mục trong hệ thống!"));
        if (!product.getCategory().getId().equals(category.getId())) {
            if (category.getStatus().equals(Status.INACTIVE.toString())) {
                throw new RuntimeException("Danh mục " + category.getName() + " không còn hoạt động");
            }
            product.setCategory(category);
        }
        product.setName(updateProductProductUnitsRequest.getProductRequest().getName());
        product.setPricePerBaseUnit(updateProductProductUnitsRequest.getProductRequest().getPricePerBaseUnit());
        product.setQuantity(updateProductProductUnitsRequest.getProductRequest().getQuantity());
        product.setBaseUnit(updateProductProductUnitsRequest.getProductRequest().getBaseUnit());
        product.setCategory(category);
        Product saveProduct = productRepository.save(product);
        boolean checkProductDetail = productUnitsService.updateProductUnits(saveProduct, updateProductProductUnitsRequest.getProductRequest().getProductUnits());
        if (!checkProductDetail) {
            throw new RuntimeException("Xảy ra lỗi khi thêm đơn vị quy đổi sản phẩm");
        }
        if (!updateProductProductUnitsRequest.getProductRequest().getListImages().isEmpty()) {
            productImageRepository.deleteByProduct(saveProduct);
            boolean checkImage = productImageService.createProductImage(saveProduct, updateProductProductUnitsRequest.getProductRequest().getListImages());
            if (!checkImage) {
                throw new RuntimeException("Xảy ra lỗi khi thêm ảnh cho sản phẩm chi tiết");
            }
        }
        notificationController.sendNotification();
    }

    public ProductResponse findProductById(Long id) {
        Optional<ProductResponse> optional = productRepository.findProductRequestsById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài nguyên sản phẩm trong hệ thống!");
        }
        return optional.get();
    }

    public List<ProductResponse> findProductProductDetailResponse() {
        return productRepository.findProductRequests();
    }

    public List<ProductResponse> filterProductProductDetailResponse(
            String search, String idCategory, String status) {
        return productRepository.findProductRequests().stream()
                .filter(response ->
                        search == null ||
                                search.isEmpty() ||
                                (response.getName() != null && response.getName().toLowerCase().contains(search.trim().toLowerCase()))
                )
                .filter(response ->
                        idCategory == null ||
                                idCategory.isEmpty() ||
                                (response.getIdCategory() != null && response.getIdCategory().toString().contains(idCategory.trim()))
                )
                .filter(response ->
                        status == null ||
                                status.isEmpty() ||
                                (response.getStatus() != null && response.getStatus().equalsIgnoreCase(status.trim().toLowerCase()))
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public Product updateStatus(Long id, boolean aBoolean) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Không tìm thấy tài nguyên sản phẩm trong hệ thống!");
        }
        String newStatus = aBoolean ? Status.ACTIVE.toString() : Status.INACTIVE.toString();
        optionalProduct.get().setStatus(newStatus);
        Product product = productRepository.save(optionalProduct.get());
        List<ProductUnits> productUnits = productUnitsRepository.findByProductId(id);
        for (ProductUnits units : productUnits) {
            units.setStatus(newStatus);
            productUnitsRepository.save(units);
        }
        notificationController.sendNotification();
        return product;
    }

//    public Map<Long, String> getProductNameById(List<Long> listId) {
//        Map<Long, String> mapName = new HashMap<>();
//        for (Long id : listId) {
//            ProductDetail pd = productDetailRepository.findById(id).get();
//            mapName.put(id, pd.getProduct().getName());
//        }
//        return mapName;
//    }

    public List<ProductResponse> findProductRequests() {
        return productRepository.findProductRequests();
    }

//    public ProductViewCustomerReponse getFindProductPriceRangeWithPromotionByIdProduct(Long idProduct) {
//        Optional<ProductViewCustomerReponse> productViewCustomerReponse = productDetailRepository.findProductPriceRangeWithPromotionByIdProduct(idProduct);
//        if (productViewCustomerReponse.isEmpty()) {
//            throw new RuntimeException("Không tìm thấy tài nguyên sản phẩm trong hệ thống!");
//        }
//        return productViewCustomerReponse.get();
//    }
public List<ProductViewCustomerReponse> getProductPriceRangeWithPromotion() {
    List<ProductViewCustomerReponse> productViewCustomerReponses = productRepository.findAllActiveProductsWithPromotion();
    return productViewCustomerReponses;
}
}

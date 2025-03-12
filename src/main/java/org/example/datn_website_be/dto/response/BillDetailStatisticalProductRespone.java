package org.example.datn_website_be.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class BillDetailStatisticalProductRespone {

    private byte[] imageByte;

    private Long idProduct;

    private String nameProduct;

    private Integer quantity;

    private BigDecimal priceDiscount;

    private BigDecimal revenue;

}

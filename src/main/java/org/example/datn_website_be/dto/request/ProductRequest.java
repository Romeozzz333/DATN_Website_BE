package org.example.datn_website_be.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    private String name;



    @NotNull(message = "Danh mục không được để trống")
    private Long idCategory;

    @NotNull(message = "Danh sách quy đổi không được để trống")
    @Size(min = 1, message = "Phải có ít nhất một giá trị quy đổi")
    private List<@Valid ProductUnits> productUnits;

}

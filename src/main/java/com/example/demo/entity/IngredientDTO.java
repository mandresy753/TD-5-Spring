package com.example.demo.entity;

import com.example.demo.enums.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDTO {

    private Integer id;
    private String name;
    private CategoryEnum category;
    private BigDecimal price;
}

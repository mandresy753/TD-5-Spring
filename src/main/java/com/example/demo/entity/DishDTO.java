package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishDTO {
    private Integer id;
    private String name;
    private BigDecimal price;
    private List<IngredientDTO> ingredients;

}

package com.example.demo.entity;

import com.example.demo.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishIngredient {

    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private BigDecimal quantityRequired;
    private UnitType unit;

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", dish=" + (dish != null ? dish.getName() : "null") +
                ", ingredient=" + (ingredient != null ? ingredient.getName() : "null") +
                ", quantityRequired=" + quantityRequired +
                ", unit=" + unit +
                '}';
    }
}

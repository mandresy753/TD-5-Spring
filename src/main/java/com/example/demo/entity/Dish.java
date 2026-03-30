package com.example.demo.entity;

import com.example.demo.enums.DishTypeEnum;
import com.example.demo.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dish {

    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private BigDecimal sellingPrice;
    private List<DishIngredient> dishIngredients = new ArrayList<>();

    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        if (dishIngredients != null) {
            for (DishIngredient dishIngredient : dishIngredients) {
                if (dishIngredient.getIngredient() != null) {
                    ingredients.add(dishIngredient.getIngredient());
                }
            }
        }
        return ingredients;
    }

    public BigDecimal getDishCost() {
        BigDecimal totalCost = BigDecimal.ZERO;

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        for (DishIngredient dishIngredient : dishIngredients) {
            if (dishIngredient.getQuantityRequired() != null &&
                    dishIngredient.getIngredient() != null &&
                    dishIngredient.getIngredient().getPrice() != null) {

                BigDecimal itemCost = dishIngredient.getIngredient().getPrice()
                        .multiply(dishIngredient.getQuantityRequired());
                totalCost = totalCost.add(itemCost);
            }
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossMargin() {
        if (sellingPrice == null) {
            throw new IllegalStateException("Le prix de vente n'est pas défini pour le plat : " + name);
        }

        BigDecimal cost = getDishCost();
        return sellingPrice.subtract(cost).setScale(2, RoundingMode.HALF_UP);
    }

    public void addIngredient(Ingredient ingredient, BigDecimal quantityRequired, UnitType unit) {
        if (ingredient == null || quantityRequired == null || unit == null) {
            throw new IllegalArgumentException("Tous les paramètres doivent être non nuls");
        }

        DishIngredient dishIngredient = new DishIngredient();
        dishIngredient.setDish(this);
        dishIngredient.setIngredient(ingredient);
        dishIngredient.setQuantityRequired(quantityRequired);
        dishIngredient.setUnit(unit);

        if (dishIngredients == null) {
            dishIngredients = new ArrayList<>();
        }

        dishIngredients.add(dishIngredient);
    }

    @Override
    public String toString() {
        BigDecimal cost = getDishCost();
        String marginInfo;

        try {
            marginInfo = ", marge=" + getGrossMargin();
        } catch (IllegalStateException e) {
            marginInfo = ", marge=EXCEPTION (prix null)";
        }

        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", sellingPrice=" + sellingPrice +
                ", dishIngredientsCount=" + (dishIngredients != null ? dishIngredients.size() : 0) +
                ", cost=" + cost +
                marginInfo +
                '}';
    }
}
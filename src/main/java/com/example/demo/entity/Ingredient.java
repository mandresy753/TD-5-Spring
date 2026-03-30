package com.example.demo.entity;

import com.example.demo.enums.CategoryEnum;
import com.example.demo.enums.MovementType;
import com.example.demo.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {

    private Integer id;
    private String name;
    private BigDecimal price;
    private CategoryEnum category;
    private Dish dish;
    private BigDecimal requiredQuantity;
    private UnitType unit;
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public void addStockMovement(StockMovement sm) {
        if (stockMovementList == null) {
            stockMovementList = new ArrayList<>();
        }
        stockMovementList.add(sm);
    }

    public StockValue getStockValueAt(Instant instant) {
        double quantity = 0.0;

        if (stockMovementList != null) {
            for (StockMovement sm : stockMovementList) {
                if (!sm.getCreationDateTime().isAfter(instant)) {
                    if (sm.getType() == MovementType.IN) {
                        quantity += sm.getValue().getQuantity();
                    } else if (sm.getType() == MovementType.OUT) {
                        quantity -= sm.getValue().getQuantity();
                    }
                }
            }
        }

        return new StockValue(quantity, UnitType.KG);
    }
}
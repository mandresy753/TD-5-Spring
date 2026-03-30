package com.example.demo.entity;

import com.example.demo.enums.UnitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockValue {
    private double quantity;
    private UnitType unit;
}

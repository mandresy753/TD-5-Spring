package com.example.demo.entity;

import com.example.demo.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockMovement {
    private int id;
    private StockValue value;
    private MovementType type;
    private Instant creationDateTime;
}
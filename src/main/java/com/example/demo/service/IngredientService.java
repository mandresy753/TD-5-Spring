package com.example.demo.service;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.entity.StockMovement;
import com.example.demo.entity.StockValue;
import com.example.demo.enums.MovementType;
import com.example.demo.enums.UnitType;
import com.example.demo.exception.IngredientNotFoundException;
import com.example.demo.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<IngredientDTO> getIngredients(int page, int size) {
        return ingredientRepository.findIngredients(page, size);
    }
    public IngredientDTO getIngredientById(int id) throws IngredientNotFoundException {
        IngredientDTO ingredient = ingredientRepository.findIngredientsById(id);
        if (ingredient.getId() == null) {
            throw new IngredientNotFoundException(id);
        }
        return ingredient;
    }

    public StockValue getIngredientStock(int id, String at, UnitType unit) {
        Ingredient ingredient = ingredientRepository.findIngredientWithStockMovementsById(id);
        if (ingredient.getId() == null){
            throw new IngredientNotFoundException(id);
        }
        Instant instantAt = Instant.parse(at);
        StockValue stockAt = ingredient.getStockValueAt(instantAt);
        return new StockValue(stockAt.getQuantity(), unit);
    }
}
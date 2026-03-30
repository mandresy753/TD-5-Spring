package com.example.demo.controller;

import com.example.demo.entity.IngredientDTO;
import com.example.demo.entity.StockValue;
import com.example.demo.enums.UnitType;
import com.example.demo.exception.IngredientNotFoundException;
import com.example.demo.exception.MissingQueryParameterException;
import com.example.demo.service.IngredientService;
import com.example.demo.validator.StockRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final StockRequestValidator stockRequestValidator;

    public IngredientController(IngredientService ingredientService,
                                StockRequestValidator stockRequestValidator) {
        this.ingredientService = ingredientService;
        this.stockRequestValidator = stockRequestValidator;
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getIngredients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<IngredientDTO> ingredients = ingredientService.getIngredients(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable int id) {
        try {
            IngredientDTO ing = ingredientService.getIngredientById(id);
            return ResponseEntity.status(HttpStatus.OK).body(ing);
        } catch (IngredientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable int id,
            @RequestParam(name = "at", required = false) String at,
            @RequestParam(name = "unit", required = false) UnitType unit) {

        try {
            stockRequestValidator.validate(at, unit);

            StockValue stock = ingredientService.getIngredientStock(id, at, unit);
            return ResponseEntity.status(HttpStatus.OK).body(stock);

        } catch (MissingQueryParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (IngredientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
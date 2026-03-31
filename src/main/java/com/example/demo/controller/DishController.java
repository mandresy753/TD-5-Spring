package com.example.demo.controller;

import com.example.demo.entity.DishDTO;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.exception.DishNotFoundException;
import com.example.demo.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishDTO>> getDishes() {
        return ResponseEntity.status(HttpStatus.OK).body(dishService.getDishes());
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<IngredientDTO> ingredients) {

        try {
            if (ingredients == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Request body is required");
            }

            dishService.updateDishIngredients(id, ingredients);

            return ResponseEntity.ok("Dish ingredients updated");

        } catch (DishNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}/ingredients")
    public ResponseEntity<?> getIngredientsByDishWithFilters(
            @PathVariable int id,
            @RequestParam(required = false) String ingredientName,
            @RequestParam(required = false) BigDecimal ingredientPriceAround) {

        try {
            List<IngredientDTO> ingredients =
                    dishService.getIngredientsByDishWithFilters(id, ingredientName, ingredientPriceAround);
            return ResponseEntity.ok(ingredients);
        } catch (DishNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
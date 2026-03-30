package com.example.demo.controller;

import com.example.demo.entity.IngredientDTO;
import com.example.demo.exception.IngredientNotFoundException;
import com.example.demo.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredient")
    public ResponseEntity<List<IngredientDTO>> getIngredients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ingredientService.getIngredients(page, size));
    }
    @GetMapping("/ingredient/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable int id) {
        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ingredientService.getIngredientById(id));
        }catch(IngredientNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
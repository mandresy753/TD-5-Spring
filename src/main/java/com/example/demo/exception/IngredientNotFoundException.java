package com.example.demo.exception;

public class IngredientNotFoundException extends RuntimeException {
    public IngredientNotFoundException(int id) {
        super("Ingredient.id=" + id + " is not found");
    }
}
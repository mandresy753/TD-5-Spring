package com.example.demo.service;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.repository.IngredientRepository;
import org.springframework.stereotype.Service;

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
}
package com.example.demo.service;

import com.example.demo.entity.DishDTO;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.exception.DishNotFoundException;
import com.example.demo.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDTO> getDishes() {
        return dishRepository.findAllDishes();
    }

    public void updateDishIngredients(int dishId, List<IngredientDTO> ingredients) {

        if (!dishRepository.existsDishById(dishId)) {
            throw new DishNotFoundException(dishId);
        }

        List<Integer> ids = ingredients.stream()
                .map(IngredientDTO::getId)
                .toList();

        Set<Integer> validIds = dishRepository.findExistingIngredientIds(ids);

        Set<Integer> currentIds = dishRepository.findIngredientIdsByDishId(dishId);

        Set<Integer> toAdd = validIds.stream()
                .filter(id -> !currentIds.contains(id))
                .collect(Collectors.toSet());

        Set<Integer> toRemove = currentIds.stream()
                .filter(id -> !validIds.contains(id))
                .collect(Collectors.toSet());

        for (Integer id : toAdd) {
            dishRepository.addIngredientToDish(dishId, id);
        }

        for (Integer id : toRemove) {
            dishRepository.removeIngredientFromDish(dishId, id);
        }
    }

    public List<IngredientDTO> getIngredientsByDishWithFilters(
            int dishId, String ingredientName, BigDecimal ingredientPriceAround) {

        if (!dishRepository.existsDishById(dishId)) {
            throw new DishNotFoundException(dishId);
        }

        return dishRepository.findIngredientsByDishIdWithFilters(dishId, ingredientName, ingredientPriceAround);
    }
}
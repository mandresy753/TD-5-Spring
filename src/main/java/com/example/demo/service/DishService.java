package com.example.demo.service;

import com.example.demo.entity.DishDTO;
import com.example.demo.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDTO> getDishes() {
        return dishRepository.findAllDishes();
    }
}
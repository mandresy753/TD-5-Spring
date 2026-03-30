package com.example.demo.controller;

import com.example.demo.entity.DishDTO;
import com.example.demo.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
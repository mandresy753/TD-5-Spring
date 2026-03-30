package com.example.demo.repository;

import com.example.demo.entity.IngredientDTO;
import com.example.demo.enums.CategoryEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {

    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<IngredientDTO> findIngredients(int page, int size) {
        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            ORDER BY id
            LIMIT ? OFFSET ?
            """;

        List<IngredientDTO> ingredients = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String categoryStr = rs.getString("category");
                    CategoryEnum category = (categoryStr != null) ? CategoryEnum.valueOf(categoryStr) : null;


                    IngredientDTO ing = new IngredientDTO();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setCategory(category);
                    ing.setPrice(rs.getBigDecimal("price"));

                    ingredients.add(ing);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }

        return ingredients;
    }
}
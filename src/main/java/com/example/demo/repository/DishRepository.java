package com.example.demo.repository;

import com.example.demo.entity.DishDTO;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.enums.CategoryEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Repository
public class DishRepository {

    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DishDTO> findAllDishes() {
        String sql = """
            SELECT d.id AS dish_id, d.name AS dish_name, d.selling_price AS dish_price,
                   i.id AS ing_id, i.name AS ing_name, i.price AS ing_price, i.category AS ing_category
            FROM dish d
            LEFT JOIN dish_ingredient di ON d.id = di.id_dish
            LEFT JOIN ingredient i ON di.id_ingredient = i.id
            ORDER BY d.id
        """;

        Map<Integer, DishDTO> dishMap = new LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int dishId = rs.getInt("dish_id");

                DishDTO dish = dishMap.get(dishId);
                if (dish == null) {
                    dish = new DishDTO();
                    dish.setId(dishId);
                    dish.setName(rs.getString("dish_name"));
                    dish.setPrice(rs.getBigDecimal("dish_price"));
                    dish.setIngredients(new ArrayList<>());
                    dishMap.put(dishId, dish);
                }

                int ingId = rs.getInt("ing_id");
                if (ingId != 0) {
                    IngredientDTO ingredient = new IngredientDTO();
                    ingredient.setId(ingId);
                    ingredient.setName(rs.getString("ing_name"));
                    ingredient.setPrice(rs.getBigDecimal("ing_price"));

                    String categoryStr = rs.getString("ing_category");
                    if (categoryStr != null) {
                        ingredient.setCategory(CategoryEnum.valueOf(categoryStr));
                    }

                    dish.getIngredients().add(ingredient);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération dishes", e);
        }

        return new ArrayList<>(dishMap.values());
    }

    public boolean existsDishById(int id) {
        String sql = "SELECT 1 FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> findExistingIngredientIds(List<Integer> ids) {
        if (ids.isEmpty()) return new HashSet<>();

        String sql = "SELECT id FROM ingredient WHERE id = ANY (?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Array array = conn.createArrayOf("int4", ids.toArray());
            ps.setArray(1, array);

            ResultSet rs = ps.executeQuery();

            Set<Integer> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getInt("id"));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> findIngredientIdsByDishId(int dishId) {
        String sql = "SELECT id_ingredient FROM dish_ingredient WHERE id_dish = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();

            Set<Integer> result = new HashSet<>();
            while (rs.next()) {
                result.add(rs.getInt("id_ingredient"));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addIngredientToDish(int dishId, int ingredientId) {
        String sql = "INSERT INTO dish_ingredient(id_dish, id_ingredient) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishId);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeIngredientFromDish(int dishId, int ingredientId) {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ? AND id_ingredient = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dishId);
            ps.setInt(2, ingredientId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
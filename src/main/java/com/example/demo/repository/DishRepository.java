package com.example.demo.repository;

import com.example.demo.entity.DishDTO;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.enums.CategoryEnum;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
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

    public List<IngredientDTO> findIngredientsByDishIdWithFilters(
            int dishId, String ingredientName, BigDecimal ingredientPriceAround) {

        if (!existsDishById(dishId)) {
            throw new RuntimeException("Dish.id=" + dishId + " is not found");
        }

        StringBuilder sql = new StringBuilder("""
        SELECT i.id, i.name, i.price, i.category
        FROM ingredient i
        JOIN dish_ingredient di ON i.id = di.id_ingredient
        WHERE di.id_dish = ?
    """);

        List<Object> params = new ArrayList<>();
        params.add(dishId);

        if (ingredientName != null && !ingredientName.isBlank()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (ingredientPriceAround != null) {
            sql.append(" AND i.price BETWEEN ? AND ?");
            params.add(ingredientPriceAround.subtract(BigDecimal.valueOf(50)));
            params.add(ingredientPriceAround.add(BigDecimal.valueOf(50)));
        }

        List<IngredientDTO> ingredients = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String s) {
                    ps.setString(i + 1, s);
                } else if (param instanceof BigDecimal bd) {
                    ps.setBigDecimal(i + 1, bd);
                } else if (param instanceof Integer n) {
                    ps.setInt(i + 1, n);
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                IngredientDTO ing = new IngredientDTO();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getBigDecimal("price"));

                String categoryStr = rs.getString("category");
                if (categoryStr != null) {
                    ing.setCategory(CategoryEnum.valueOf(categoryStr));
                }

                ingredients.add(ing);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération ingredients du plat", e);
        }

        return ingredients;
    }
}
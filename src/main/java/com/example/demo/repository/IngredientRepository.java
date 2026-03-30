package com.example.demo.repository;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.IngredientDTO;
import com.example.demo.entity.StockMovement;
import com.example.demo.entity.StockValue;
import com.example.demo.enums.CategoryEnum;
import com.example.demo.enums.MovementType;
import com.example.demo.enums.UnitType;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
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

    public IngredientDTO findIngredientsById(int id) {
        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            WHERE id = ?
            """;

        IngredientDTO  ingredient = new IngredientDTO();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String categoryStr = rs.getString("category");
                    CategoryEnum category = (categoryStr != null) ? CategoryEnum.valueOf(categoryStr) : null;


                    IngredientDTO ing = new IngredientDTO();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setCategory(category);
                    ing.setPrice(rs.getBigDecimal("price"));

                    ingredient = ing;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }

        return ingredient;
    }

    public List<StockMovement> findStockMovementsByIngredientId(int ingredientId) {
        String sql = "SELECT id, quantity, unit, type, creation_datetime FROM stock_movement WHERE id_ingredient = ? ORDER BY creation_datetime";
        List<StockMovement> movements = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double quantity = rs.getDouble("quantity");
                    UnitType unit = UnitType.valueOf(rs.getString("unit"));
                    MovementType type = MovementType.valueOf(rs.getString("type"));
                    Timestamp ts = rs.getTimestamp("creation_datetime");
                    if (ts == null) continue;
                    Instant creationDateTime = ts.toInstant();
                    StockValue value = new StockValue(quantity, unit);
                    StockMovement sm = new StockMovement(rs.getInt("id"), value, type, creationDateTime);
                    movements.add(sm);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movements;
    }

    public Ingredient findIngredientWithStockMovementsById(int id) {
        IngredientDTO dto = findIngredientsById(id);
        if (dto == null) return null;
        Ingredient ingredient = new Ingredient();
        ingredient.setId(dto.getId());
        ingredient.setName(dto.getName());
        ingredient.setPrice(dto.getPrice());
        ingredient.setCategory(dto.getCategory());
        List<StockMovement> movements = findStockMovementsByIngredientId(id);
        for (StockMovement sm : movements) {
            ingredient.addStockMovement(sm);
        }
        return ingredient;
    }
}
package nl.abn.assessment.recipesservice.common;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeMapperTest {

    private final RecipeMapper mapper = RecipeMapper.INSTANCE;

    @Test
    void stringToIngredients_NullInput_ReturnsEmptyList() {
        List<String> result = mapper.stringToIngredients(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void stringToIngredients_ValidInput_ReturnsList() {
        List<String> result = mapper.stringToIngredients("flour, sugar, eggs");
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(List.of("flour", "sugar", "eggs"), result);
    }

    @Test
    void ingredientsToString_NullInput_ReturnsEmptyString() {
        String result = mapper.ingredientsToString(null);
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void ingredientsToString_EmptyList_ReturnsEmptyString() {
        String result = mapper.ingredientsToString(Collections.emptyList());
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void ingredientsToString_ValidList_ReturnsString() {
        String result = mapper.ingredientsToString(List.of("flour", "sugar", "eggs"));
        assertNotNull(result);
        assertEquals("flour, sugar, eggs", result);
    }
}
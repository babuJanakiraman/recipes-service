package nl.abn.assessment.recipesservice.controller;

import nl.abn.assessment.recipesservice.model.RecipeDto;
import nl.abn.assessment.recipesservice.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRecipe_ValidRecipe_ReturnsCreatedStatus() {
        RecipeDto recipeDto = new RecipeDto();
        when(recipeService.addRecipe(any(RecipeDto.class))).thenReturn(recipeDto);

        ResponseEntity<RecipeDto> response = recipeController.addRecipe(recipeDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(recipeDto, response.getBody());
        verify(recipeService, times(1)).addRecipe(any(RecipeDto.class));
    }

    @Test
    void getRecipeById_ExistingId_ReturnsRecipe() {
        Long id = 1L;
        RecipeDto recipeDto = new RecipeDto();
        when(recipeService.getRecipeById(id)).thenReturn(recipeDto);

        ResponseEntity<RecipeDto> response = recipeController.getRecipeById(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipeDto, response.getBody());
        verify(recipeService, times(1)).getRecipeById(id);
    }

    @Test
    void updateRecipeById_ValidId_ReturnsUpdatedRecipe() {
        Long id = 1L;
        RecipeDto recipeDto = new RecipeDto();
        when(recipeService.updateRecipeById(anyLong(), any(RecipeDto.class))).thenReturn(recipeDto);

        ResponseEntity<RecipeDto> response = recipeController.updateRecipeById(id, recipeDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipeDto, response.getBody());
        verify(recipeService, times(1)).updateRecipeById(anyLong(), any(RecipeDto.class));
    }

    @Test
    void deleteRecipeById_ValidId_ReturnsSuccessMessage() {
        Long id = 1L;

        ResponseEntity<String> response = recipeController.deleteRecipeById(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{ \"message\": \"Recipe deleted successfully\" }", response.getBody());
        verify(recipeService, times(1)).deleteRecipeById(id);
    }

    @Test
    void searchRecipes_AllParameters_ReturnsRecipes() {
        Boolean vegetarian = true;
        Integer servings = 4;
        String instructions = "bake";
        List<String> includeIngredients = List.of("flour", "sugar");
        List<String> excludeIngredients = List.of("nuts");
        List<RecipeDto> recipes = List.of(new RecipeDto());
        when(recipeService.searchRecipes(anyBoolean(), anyInt(), anyString(), anyList(), anyList())).thenReturn(recipes);

        ResponseEntity<List<RecipeDto>> response = recipeController.searchRecipes(vegetarian, servings, includeIngredients, excludeIngredients, instructions);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(recipes, response.getBody());
        verify(recipeService, times(1)).searchRecipes(anyBoolean(), anyInt(), anyString(), anyList(), anyList());
    }
}
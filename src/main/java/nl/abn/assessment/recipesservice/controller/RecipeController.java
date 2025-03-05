package nl.abn.assessment.recipesservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.abn.assessment.recipesservice.api.RecipeApi;
import nl.abn.assessment.recipesservice.model.RecipeDto;
import nl.abn.assessment.recipesservice.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class RecipeController implements RecipeApi {

    private static final String DELETE_RECIPE_200_RESPONSE = "{ \"message\": \"Recipe deleted successfully\" }";

    private final RecipeService recipeService;

    public ResponseEntity<RecipeDto> addRecipe(RecipeDto recipeDto) {
        log.debug("Adding recipe: {}", recipeDto);
        RecipeDto recipeResponse = recipeService.addRecipe(recipeDto);
        return new ResponseEntity<>(recipeResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<RecipeDto> getRecipeById(Long id) {
        log.info("Getting recipe by id: {}", id);
        RecipeDto recipe = recipeService.getRecipeById(id);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    public ResponseEntity<RecipeDto> updateRecipeById(Long id, RecipeDto recipe) {
        log.info("Updating recipe with id: {}", id);
        RecipeDto recipeResponse = recipeService.updateRecipeById(id, recipe);
        return new ResponseEntity<>(recipeResponse, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteRecipeById(Long id) {
        log.info("Deleting recipe with id: {}", id);
        recipeService.deleteRecipeById(id);
        return ResponseEntity.ok(DELETE_RECIPE_200_RESPONSE);
    }

    public ResponseEntity<List<RecipeDto>> searchRecipes(Boolean vegetarian, Integer servings, List<String> includeIngredients, List<String> excludeIngredients, String instructions) {
        log.info("Searching recipes with vegetarian: {}, servings: {}, includeIngredients: {}, excludeIngredients: {}, instructions: {}", vegetarian, servings, includeIngredients, excludeIngredients, instructions);
        List<RecipeDto> recipes = recipeService.searchRecipes(vegetarian, servings, instructions, includeIngredients, excludeIngredients);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
}
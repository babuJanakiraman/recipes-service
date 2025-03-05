package nl.abn.assessment.recipesservice.service;

import nl.abn.assessment.recipesservice.exception.RecipeNotFoundException;
import nl.abn.assessment.recipesservice.model.Recipe;
import nl.abn.assessment.recipesservice.model.RecipeDto;
import nl.abn.assessment.recipesservice.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Recipe> criteriaQuery;

    @Mock
    private Root<Recipe> root;

    @Mock
    private TypedQuery<Recipe> typedQuery;

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Recipe.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Recipe.class)).thenReturn(root);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        recipe = new Recipe(1L, "cake", true, 4, "flour,sugar", "bake");
    }

    @Test
    void addRecipe() {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setIngredients(List.of("flour", "sugar"));
        recipeDto.servings(4);
        recipeDto.setName("cake");
        recipeDto.setInstructions("bake");
        recipeDto.setVegetarian(true);

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        RecipeDto result = recipeService.addRecipe(recipeDto);

        assertNotNull(result);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void getRecipeById() {
        Long id = 1L;
        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
        RecipeDto result = recipeService.getRecipeById(id);
        assertNotNull(result);
        verify(recipeRepository, times(1)).findById(id);
    }

    @Test
    void getRecipeById_NotFound() {
        Long id = 2L;
        when(recipeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById(id));
        verify(recipeRepository, times(1)).findById(id);
    }

    @Test
    void updateRecipeById() {
        Long id = 2L;
        RecipeDto recipeDto = new RecipeDto();
        Recipe existingRecipe = new Recipe(2L, "chocolate cake", true, 2, "chocolate, flour, sugar", "bake");
        when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        RecipeDto result = recipeService.updateRecipeById(id, recipeDto);

        assertNotNull(result);
        verify(recipeRepository, times(1)).findById(id);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void updateRecipeById_NotFound() {
        Long id = 1L;
        RecipeDto recipeDto = new RecipeDto();
        when(recipeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.updateRecipeById(id, recipeDto));
        verify(recipeRepository, times(1)).findById(id);
    }

    @Test
    void deleteRecipeById() {
        Long id =3L;
        recipeService.deleteRecipeById(id);
        verify(recipeRepository, times(1)).deleteById(id);
    }

    @Test
    void searchRecipes_AllParameters() {
        Boolean vegetarian = true;
        Integer servings = 4;
        String instructions = "bake";
        List<String> includeIngredients = List.of("flour", "sugar");
        List<String> excludeIngredients = List.of("nuts");

        when(criteriaBuilder.equal(root.get("vegetarian"), vegetarian)).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.equal(root.get("servings"), servings)).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(root.get("instructions"), "%" + instructions + "%")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(root.get("ingredients"), "%flour%")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(root.get("ingredients"), "%sugar%")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.notLike(root.get("ingredients"), "%nuts%")).thenReturn(mock(Predicate.class));
        when(typedQuery.getResultList()).thenReturn(List.of(recipe));

        List<RecipeDto> result = recipeService.searchRecipes(vegetarian, servings, instructions, includeIngredients, excludeIngredients);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    void searchRecipes_NoParameters() {
        when(typedQuery.getResultList()).thenReturn(List.of(new Recipe()));

        List<RecipeDto> result = recipeService.searchRecipes(null, null, null, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    void searchRecipes_OnlyVegetarian() {
        Boolean vegetarian = true;

        when(criteriaBuilder.equal(root.get("vegetarian"), vegetarian)).thenReturn(mock(Predicate.class));
        when(typedQuery.getResultList()).thenReturn(List.of(recipe));

        List<RecipeDto> result = recipeService.searchRecipes(vegetarian, null, null, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    void searchRecipes_OnlyServings() {
        Integer servings = 4;

        when(criteriaBuilder.equal(root.get("servings"), servings)).thenReturn(mock(Predicate.class));
        when(typedQuery.getResultList()).thenReturn(List.of(recipe));

        List<RecipeDto> result = recipeService.searchRecipes(null, servings, null, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    void searchRecipes_OnlyInstructions() {
        String instructions = "bake";

        when(criteriaBuilder.like(root.get("instructions"), "%" + instructions + "%")).thenReturn(mock(Predicate.class));
        when(typedQuery.getResultList()).thenReturn(List.of(recipe));

        List<RecipeDto> result = recipeService.searchRecipes(null, null, instructions, null, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    void searchRecipes_IncludeIngredients() {
        List<String> includeIngredients = List.of("flour", "sugar");

        when(criteriaBuilder.like(root.get("ingredients"), "%flour%")).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(root.get("ingredients"), "%sugar%")).thenReturn(mock(Predicate.class));
        when(typedQuery.getResultList()).thenReturn(List.of(new Recipe()));

        List<RecipeDto> result = recipeService.searchRecipes(null, null, null, includeIngredients, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }

    @Test
    void searchRecipes_ExcludeIngredients() {
        List<String> excludeIngredients = List.of("nuts");

        when(criteriaBuilder.notLike(root.get("ingredients"), "%nuts%")).thenReturn(mock(Predicate.class));
        when(typedQuery.getResultList()).thenReturn(List.of(recipe));

        List<RecipeDto> result = recipeService.searchRecipes(null, null, null, null, excludeIngredients);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(entityManager, times(1)).createQuery(any(CriteriaQuery.class));
    }
}
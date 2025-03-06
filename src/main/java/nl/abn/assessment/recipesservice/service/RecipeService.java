package nl.abn.assessment.recipesservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.abn.assessment.recipesservice.common.RecipeMapper;
import nl.abn.assessment.recipesservice.exception.RecipeNotFoundException;
import nl.abn.assessment.recipesservice.model.Recipe;
import nl.abn.assessment.recipesservice.model.RecipeDto;
import nl.abn.assessment.recipesservice.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public RecipeDto addRecipe(RecipeDto recipeDto) {
        log.info("Adding recipe");
        Recipe recipe = RecipeMapper.INSTANCE.toEntity(recipeDto);
        return RecipeMapper.INSTANCE.toDto(recipeRepository.save(recipe));
    }

    public RecipeDto getRecipeById(Long id) {
        log.info("Getting recipe by id: {}", id);
        return recipeRepository.findById(id)
                .map(RecipeMapper.INSTANCE::toDto)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + id));
    }

    public RecipeDto updateRecipeById(Long id, RecipeDto recipeDto) {
        log.info("Updating recipe by id: {}", id);
        return recipeRepository.findById(id)
                .map(existingRecipe -> {
                    Recipe updatedRecipe = RecipeMapper.INSTANCE.toEntity(recipeDto);
                    updatedRecipe.setId(existingRecipe.getId());
                    return RecipeMapper.INSTANCE.toDto(recipeRepository.save(updatedRecipe));
                })
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + id));
    }

    public void deleteRecipeById(Long id) {
        log.info("Deleting recipe by id: {}", id);
        recipeRepository.deleteById(id);
    }

    public List<RecipeDto> searchRecipes(Boolean vegetarian, Integer servings, String instructions, List<String> includeIngredients, List<String> excludeIngredients) {
        CriteriaQuery<Recipe> cq = getRecipeCriteriaQuery(vegetarian, servings, instructions, includeIngredients, excludeIngredients);
        TypedQuery<Recipe> query = entityManager.createQuery(cq);
        return query.getResultList().stream().map(RecipeMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    private CriteriaQuery<Recipe> getRecipeCriteriaQuery(Boolean vegetarian, Integer servings, String instructions, List<String> includeIngredients, List<String> excludeIngredients) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Recipe> cq = cb.createQuery(Recipe.class);
        Root<Recipe> recipe = cq.from(Recipe.class);

        List<Predicate> predicates = new ArrayList<>();

        if (vegetarian != null) {
            predicates.add(cb.equal(recipe.get("vegetarian"), vegetarian));
        }
        if (servings != null) {
            predicates.add(cb.equal(recipe.get("servings"), servings));
        }
        if (instructions != null && !instructions.isEmpty()) {
            predicates.add(cb.like(cb.lower(recipe.get("instructions")), "%" + instructions.toLowerCase() + "%"));
        }
        if (includeIngredients != null && !includeIngredients.isEmpty()) {
            List<Predicate> includePredicates = includeIngredients.stream()
                    .map(ingredient -> cb.like(cb.lower(recipe.get("ingredients")), "%" + ingredient.toLowerCase() + "%"))
                    .toList();
            predicates.add(cb.or(includePredicates.toArray(new Predicate[0])));
        }

        if (excludeIngredients != null && !excludeIngredients.isEmpty()) {
            predicates.add(cb.and(excludeIngredients.stream()
                    .map(ingredient -> cb.notLike(cb.lower(recipe.get("ingredients")), "%" + ingredient.toLowerCase() + "%"))
                    .toArray(Predicate[]::new)));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return cq;
    }

}

package nl.abn.assessment.recipesservice.common;

import nl.abn.assessment.recipesservice.model.Recipe;
import nl.abn.assessment.recipesservice.model.RecipeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper
public interface RecipeMapper {
    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "vegetarian", target = "vegetarian")
    @Mapping(source = "servings", target = "servings")
    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "ingredientsToString")
    @Mapping(source = "instructions", target = "instructions")
    Recipe toEntity(RecipeDto recipeDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "vegetarian", target = "vegetarian")
    @Mapping(source = "servings", target = "servings")
    @Mapping(source = "ingredients", target = "ingredients", qualifiedByName = "stringToIngredients")
    @Mapping(source = "instructions", target = "instructions")
    RecipeDto toDto(Recipe recipe);

    @Named("ingredientsToString")
    default String ingredientsToString(List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return "";
        }
        return String.join(", ", ingredients);
    }

    @Named("stringToIngredients")
    default List<String> stringToIngredients(String ingredients) {
        if (ingredients == null) {
            return Collections.emptyList();
        }
        return List.of(ingredients.split(", "));
    }
}
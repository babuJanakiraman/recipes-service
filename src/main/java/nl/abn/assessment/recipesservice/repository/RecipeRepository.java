package nl.abn.assessment.recipesservice.repository;

import nl.abn.assessment.recipesservice.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {


}
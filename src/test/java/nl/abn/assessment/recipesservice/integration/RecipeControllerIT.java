package nl.abn.assessment.recipesservice.integration;

import nl.abn.assessment.recipesservice.model.RecipeDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/data.sql")
public class RecipeControllerIT extends IntegrationTestConfig {

    @Test
    void addRecipe_success(){
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                        {
                          "name": "Pancakes",
                          "vegetarian": true,
                          "servings": 4,
                          "ingredients": ["flour", "sugar", "eggs"],
                          "instructions": "Mix all ingredients together and bake in a pan"
                        }
                        """)
                .when()
                .post("/recipe")
                .then()
                .statusCode(201)
                .extract().as(RecipeDto.class);
    }

    @Test
    void addRecipe_missingName_returnsBadRequest() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                    {
                      "vegetarian": true,
                      "servings": 4,
                      "ingredients": ["flour", "sugar", "eggs"],
                      "instructions": "Mix all ingredients together and bake in a pan"
                    }
                    """)
                .when()
                .post("/recipe")
                .then()
                .statusCode(400);
    }

    @Test
    void addRecipe_negativeServings_returnsBadRequest() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                    {
                      "name": "Pancakes",
                      "vegetarian": true,
                      "servings": -1,
                      "ingredients": ["flour", "sugar", "eggs"],
                      "instructions": "Mix all ingredients together and bake in a pan"
                    }
                    """)
                .when()
                .post("/recipe")
                .then()
                .statusCode(400);
    }

    @Test
    void addRecipe_emptyIngredients_returnsBadRequest() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                    {
                      "name": "Pancakes",
                      "vegetarian": true,
                      "servings": 4,
                      "ingredients": [],
                      "instructions": "Mix all ingredients together and bake in a pan"
                    }
                    """)
                .when()
                .post("/recipe")
                .then()
                .statusCode(400);
    }

    @Test
    void addRecipe_missingInstructions_returnsBadRequest() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                    {
                      "name": "Pancakes",
                      "vegetarian": true,
                      "servings": 4,
                      "ingredients": ["flour", "sugar", "eggs"]
                    }
                    """)
                .when()
                .post("/recipe")
                .then()
                .statusCode(400);
    }

    @Test
    void getRecipeById_success() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .when()
                .get("/recipe/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Pasta"))
                .body("vegetarian", equalTo(true))
                .body("servings", equalTo(4))
                .body("ingredients.size()", equalTo(3));
    }

    @Test
    void getRecipeById_notFound() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .when()
                .get("/recipe/999")
                .then()
                .statusCode(404);
    }

    @Test
    void updateRecipeById_success() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                    {
                      "name": "Updated Pancakes",
                      "vegetarian": true,
                      "servings": 4,
                      "ingredients": ["flour", "sugar", "eggs"],
                      "instructions": "Mix all ingredients together and bake in a pan"
                    }
                    """)
                .when()
                .put("/recipe/3")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Pancakes"))
                .body("vegetarian", equalTo(true))
                .body("servings", equalTo(4))
                .body("ingredients.size()", equalTo(3));
    }

    @Test
    void updateRecipeById_notFound() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .body("""
                    {
                      "name": "Updated Pancakes",
                      "vegetarian": true,
                      "servings": 4,
                      "ingredients": ["flour", "sugar", "eggs"],
                      "instructions": "Mix all ingredients together and bake in a pan"
                    }
                    """)
                .when()
                .put("/recipe/999")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteRecipeById_success() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .when()
                .delete("/recipe/999")
                .then()
                .statusCode(200)
                .body("message", equalTo("Recipe deleted successfully"));
    }

    @Test
    void searchRecipes_success() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .queryParam("vegetarian", true)
                .queryParam("servings", 4)
                .queryParam("includeIngredients", "pasta")
                .queryParam("excludeIngredients", "meat")
                .queryParam("instructions", "cook")
                .when()
                .get("/recipes")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].name", notNullValue())
                .body("[0].vegetarian", equalTo(true))
                .body("[0].ingredients", hasItem("pasta"))
                .body("[0].servings", equalTo(4));
    }

    @Test
    void searchRecipes_invalidVegetarianParam_returnsBadRequest() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .queryParam("vegetarian", "invalid")
                .when()
                .get("/recipes")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", equalTo("Invalid input"));
    }

    @Test
    void searchRecipes_invalidServingsParam_returnsBadRequest() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .queryParam("servings", -1)
                .when()
                .get("/recipes")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("message", equalTo("Invalid input"));
    }

    @Test
    void searchRecipes_noMatchingRecipes_returnsEmptyList() {
        given()
                .auth()
                .preemptive()
                .basic("user", "userpass")
                .contentType("application/json")
                .queryParam("vegetarian", true)
                .queryParam("servings", 10)
                .queryParam("includeIngredients", "nonexistentIngredient")
                .queryParam("excludeIngredients", "anotherNonexistentIngredient")
                .queryParam("instructions", "nonexistentInstruction")
                .when()
                .get("/recipes")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }
}
package nl.abn.assessment.recipesservice.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.web.server.LocalServerPort;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestConfig {

    @LocalServerPort
    protected int port;

    @BeforeAll
    public void setUp() {
        RestAssured.port = port;
    }
}
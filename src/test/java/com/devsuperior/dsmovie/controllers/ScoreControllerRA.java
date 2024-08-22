package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtilRestAssured;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ScoreControllerRA {
    private String clientUsername, clientPassword;
    private String clientToken;

    private Map<String,Object> putScoreInstance;

    @BeforeEach
    public void setUp(){
        baseURI = "http://localhost:8080";

        clientUsername = "joaquim@gmail.com";
        clientPassword = "123456";

        clientToken = TokenUtilRestAssured.obtainAccessToken(clientUsername,clientPassword);

        putScoreInstance = new HashMap<>();
        putScoreInstance.put("movieId",1);
        putScoreInstance.put("score",4);
    }

    @Test
    public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
        putScoreInstance.put("movieId",9999);
        JSONObject jsonObject = new JSONObject(putScoreInstance);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(404);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
        putScoreInstance.put( "movieId",null);
        JSONObject jsonObject = new JSONObject(putScoreInstance);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("errors.message", hasItem("Required field"))
                .body("errors.fieldName", hasItems("movieId"));
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
        putScoreInstance.put( "score",-4);
        JSONObject jsonObject = new JSONObject(putScoreInstance);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422)
                .body("errors.message", hasItem("Score should be greater than or equal to zero"))
                .body("errors.fieldName", hasItems("score"));
    }
}

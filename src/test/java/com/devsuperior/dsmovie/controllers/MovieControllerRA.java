package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtilRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

    private String clientUsername, clientPassword,adminUsername,adminPassword;
    private String clientToken,adminToken,invalidToken;
    private String movieTitle;
    private Long existingMovieId,nonExistingId;

    private Map<String,Object> postMovieInstance;

    @BeforeEach
    public void setUp(){
        baseURI = "http://localhost:8080";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
        clientUsername = "joaquim@gmail.com";
        clientPassword = "123456";

        existingMovieId = 1L;
        nonExistingId = 9999L;

        clientToken = TokenUtilRestAssured.obtainAccessToken(clientUsername,clientPassword);
        adminToken = TokenUtilRestAssured.obtainAccessToken(adminUsername,adminPassword);
        invalidToken = clientToken +"invalid";

        postMovieInstance = new HashMap<>();
        postMovieInstance.put("title","Meu Novo Filme");
        postMovieInstance.put("score",4.3);
        postMovieInstance.put("count",2);
        postMovieInstance.put("image","https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
    }

    @Test
    public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
        given()
                .get("/movies")
                .then()
                .statusCode(200);
    }

    @Test
    public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
        movieTitle = "Vingadores";
        given()
                .get("/movies?title={Vingadores}",movieTitle)
                .then()
                .statusCode(200)
                .body("content[0].id",is(13))
                .body("content[0].title",equalTo("Vingadores: Ultimato"));
    }

    @Test
    public void findByIdShouldReturnMovieWhenIdExists() {
        given()
                .get("/movies/{id}",existingMovieId)
                .then()
                .statusCode(200)
                .body("id",is(1))
                .body("title",equalTo("The Witcher"))
                .body("score",is(4.5F))
                .body("count",is(2))
                .body("image",equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
        given()
                .get("/movies/{id}", nonExistingId)
                .then()
                .statusCode(404)
                .body("error", equalTo("Recurso não encontrado"))
                .body("status", equalTo(404));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
        postMovieInstance.put("title","");
        JSONObject jsonObject = new JSONObject(postMovieInstance);
        ValidatableResponse response = given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(422)
                .body("errors.message", hasItems(
                        "Required field",
                        "Title must be between 5 and 80 characters"))
                .body("errors.fieldName", hasItems("title"));
        System.out.println(response.extract().body().asString());
    }

    @Test
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
        JSONObject jsonObject = new JSONObject(postMovieInstance);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        JSONObject jsonObject = new JSONObject(postMovieInstance);

        given()
                .header("Content-type","application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(jsonObject)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(401);
    }
}

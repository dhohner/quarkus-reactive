package org.dah;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class UserResourceTest {

  static final String INVALID_EMAIL_ERROR = "email must contain @";

  @Test
  void getUserByEmailIsNoContentForNonExistentEmail() {
    given()
        .pathParam("email", "non-existing-email@example.com")
        .when().get("/users/{email}")
        .then()
        .statusCode(NO_CONTENT.code())
        .body(emptyString());
  }

  @Test
  void getUserByEmailFailsForEmailWithoutAt() {
    given()
        .pathParam("email", "non-existing-email")
        .when().get("/users/{email}")
        .then()
        .statusCode(UNPROCESSABLE_ENTITY.code())
        .body("exceptionType", equalTo("InvalidEmailException"))
        .body("error", equalTo(INVALID_EMAIL_ERROR));
  }

  @Test
  void getUserByEmailFailsForEmptyEmail() {
    given()
        .pathParam("email", " ")
        .when().get("/users/{email}")
        .then()
        .statusCode(UNPROCESSABLE_ENTITY.code())
        .body("exceptionType", equalTo("InvalidEmailException"))
        .body("error", equalTo(INVALID_EMAIL_ERROR));
  }

  @Test
  void getAllUsersIsBadRequestForNonExistentEmail() {
    given()
        .queryParam("page", "-1")
        .when().get("/users/all")
        .then()
        .statusCode(BAD_REQUEST.code())
        .body("exceptionType", equalTo("BadRequestException"))
        .body("error", equalTo("page must be a positive integer"));
  }
}
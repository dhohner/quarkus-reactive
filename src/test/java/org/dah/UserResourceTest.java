package org.dah;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NO_CONTENT;

@QuarkusTest
class UserResourceTest {

  @Test
  void getUserByEmailIsNoContentForNonExistentEmail() {
    given()
        .pathParam("email", "non-existing-email@example.com")
        .when().get("/users/{email}")
        .then()
        .statusCode(NO_CONTENT)
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
        .body("error", equalTo("Email containing @ is Required"));
  }

  @Test
  void getUserByEmailFailsForEmptyEmail() {
    given()
        .pathParam("email", " ")
        .when().get("/users/{email}")
        .then()
        .statusCode(UNPROCESSABLE_ENTITY.code())
        .body("exceptionType", equalTo("InvalidEmailException"))
        .body("error", equalTo("Email containing @ is Required"));
  }
}
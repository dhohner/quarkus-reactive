package org.dah;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;

import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import org.dah.entities.User;
import org.junit.jupiter.api.Test;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class UserResourceTest {

  static final String INVALID_EMAIL_ERROR = "email must contain @";

  @Test
  void getUserByEmailIsNoContentForNonExistentEmail() {
    given()
        .pathParam("email", "j.hetfield@example.com")
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
        .body("error", equalTo(INVALID_EMAIL_ERROR));
  }

  @Test
  void getUserByEmailFailsForEmptyEmail() {
    given()
        .pathParam("email", " ")
        .when().get("/users/{email}")
        .then()
        .statusCode(UNPROCESSABLE_ENTITY.code())
        .body("error", equalTo(INVALID_EMAIL_ERROR));
  }

  @Test
  void getAllUsersIsBadRequestForNonExistentEmail() {
    given()
        .queryParam("page", "-1")
        .when().get("/users/all")
        .then()
        .statusCode(BAD_REQUEST.code())
        .body("error", equalTo("page must be a positive integer"));
  }

  @Test
  @RunOnVertxContext
  void isStatusCreatedForCreateNonExistentEmail(UniAsserter uniAsserter) {
    User user = createUserToSave("j.hetfield@example.com", "James", "Hetfield");
    given()
        .body(user)
        .contentType("application/json")
        .post("/users")
        .then()
        .statusCode(201)
        .body("id", notNullValue(Long.class))
        .body("email", equalTo(user.email))
        .body("firstname", equalTo(user.firstname))
        .body("lastname", equalTo(user.lastname));

    // Clean up after ourselves
    // This is a bit of a hack, but it works
    // ToDo:Find a better way to do this
    uniAsserter.execute(() -> Panache.withTransaction(() -> User.findByEmail(user.email).chain(User::delete)));
  }

  private User createUserToSave(String email, String firstname, String lastname) {
    User user = new User();
    user.email = email;
    user.firstname = firstname;
    user.lastname = lastname;
    return user;
  }
}
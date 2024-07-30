package org.dah.services;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.BadRequestException;
import org.dah.entities.User;
import org.dah.exceptions.InvalidEmailException;
import org.jboss.resteasy.reactive.RestResponse;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.OK;

@ApplicationScoped
public class UserService {
  public Uni<User> getUserByEmail(String email, boolean withException) {
    return normalizeUserInput(email)
        .map(normalizedEmail -> User.findByEmail(email))
        .orElseThrow(InvalidEmailException::new);
  }

  public Uni<RestResponse<User.Page>> getAllUsers(int page, boolean withException) {
    boolean isErrorState = page < 0;
    if (isErrorState && withException) {
      throw new BadRequestException("page must be a positive integer");
    }
    if (isErrorState) {
      User.Page empty = new User.Page(Collections.emptyList(), page);
      return Uni.createFrom().item(RestResponse.status(BAD_REQUEST, empty));
    }
    return User.count()
        .chain(count -> getUserPage(page)
            .onItem()
            .ifNotNull()
            .transform(u -> RestResponse.status(OK, new User.Page(u, count / 50)))
        );
  }

  private Uni<List<User>> getUserPage(int page) {
    return User.findAll().page(Page.of(page, 50)).list();
  }

  private Optional<String> normalizeUserInput(String input) {
    return input == null || input.isBlank() || !input.contains("@")
        ? Optional.empty()
        : Optional.of(input.strip().toLowerCase());
  }

  public Uni<RestResponse<User>> createUser(User user, boolean withException) {
    return Panache.withTransaction(user::persist)
        .replaceWith(RestResponse.status(CREATED, user));
  }
}



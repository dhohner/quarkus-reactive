package org.dah.services;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.dah.entities.User;
import org.dah.exceptions.InvalidEmailException;
import org.jboss.resteasy.reactive.RestResponse;

import static jakarta.ws.rs.core.Response.Status.CREATED;


@ApplicationScoped
public class UserService {
  public Uni<User> getUserByEmail(String email) {
    return normalizeUserInput(email)
        .map(normalizedEmail -> User.findByEmail(email))
        .orElseThrow(InvalidEmailException::new);
  }

  public Uni<User.Page> getAllUsers(int page) {
    return User.count()
        .chain(count -> getUserPage(page)
            .onItem()
            .ifNotNull()
            .transform(u -> new User.Page(u, count))
        );
  }

  private Uni<List<User>> getUserPage(int page) {
    return User.findAll().page(page, 50).list();
  }

  private Optional<String> normalizeUserInput(String input) {
    return input == null || input.isBlank() || !input.contains("@")
        ? Optional.empty()
        : Optional.of(input.strip().toLowerCase());
  }

  public Uni<RestResponse<User>> createUser(User user) {
    return Panache.withTransaction(user::persist)
        .replaceWith(RestResponse.status(CREATED, user));
  }
}



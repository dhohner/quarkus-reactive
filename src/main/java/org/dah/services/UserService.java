package org.dah.services;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.dah.entities.User;
import org.dah.exceptions.InvalidEmailException;

import java.util.Optional;

@ApplicationScoped
public class UserService {
  public Uni<User> getUserByEmail(String email) {
    return normalizeUserInput(email)
        .map(normalizedEmail -> User.findByEmail(email))
        .orElseThrow(InvalidEmailException::new);
  }

  private Optional<String> normalizeUserInput(String input) {
    return input == null || input.isBlank() || !input.contains("@")
        ? Optional.empty()
        : Optional.of(input.strip().toLowerCase());
  }
}



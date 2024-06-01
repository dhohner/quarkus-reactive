package org.dah.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties(value = {"id"})
public class User extends PanacheEntity {
  @Column(unique = true, length = 50)
  public String email;

  @Column(length = 50)
  public String firstname;

  @Column(length = 50)
  public String lastname;

  @CreationTimestamp
  LocalDateTime created;

  @UpdateTimestamp
  LocalDateTime modified;

  public static Uni<User> findByEmail(String email) {
    return find("email", email).firstResult();
  }

  public record Page(List<User> users, long pageNumber) {}
}

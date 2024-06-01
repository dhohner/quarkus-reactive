package org.dah.resources;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.dah.entities.User;
import org.dah.services.UserService;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.CREATED;

@Path("/users")
@ApplicationScoped
@Consumes("application/json")
@Produces("application/json")
public class UserResource {

  @Inject
  UserService userService;

  @GET
  @Path("/all")
  public Uni<List<User>> getAllUsers() {
    return User.listAll(Sort.by("email"));
  }

  @GET
  @Path("/{email}")
  public Uni<User> getUserByEmail(@PathParam("email") String email) {
    return userService.getUserByEmail(email);
  }

  @POST
  public Uni<RestResponse<User>> createUser(User user) {
    return Panache.withTransaction(user::persist)
        .replaceWith(RestResponse.status(CREATED, user));
  }

}

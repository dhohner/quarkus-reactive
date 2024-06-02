package org.dah.resources;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.dah.entities.User;
import org.dah.services.UserService;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/users")
@ApplicationScoped
@Consumes("application/json")
@Produces("application/json")
public class UserResource {

  @Inject
  UserService userService;

  @GET
  @Path("/all")
  public Uni<User.Page> getAllUsers(@QueryParam("page") int page) {
    return userService.getAllUsers(page);
  }

  @GET
  @Path("/{email}")
  public Uni<User> getUserByEmail(@PathParam("email") String email) {
    return userService.getUserByEmail(email);
  }

  @POST
  public Uni<RestResponse<User>> createUser(User user) {
    return userService.createUser(user);
  }

}

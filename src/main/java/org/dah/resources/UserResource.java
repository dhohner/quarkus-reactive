package org.dah.resources;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.dah.entities.User;
import org.dah.services.UserService;
import org.jboss.resteasy.reactive.Cache;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/v1/users")
@ApplicationScoped
@Consumes("application/json")
@Produces("application/json")
public class UserResource {

  @Inject
  UserService userService;

  @GET
  @Path("/all")
  public Uni<RestResponse<User.Page>> getAllUsers(@QueryParam("page") int page) {
    return userService.getAllUsers(page, false);
  }

}

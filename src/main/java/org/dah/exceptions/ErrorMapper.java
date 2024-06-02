package org.dah.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.mutiny.CompositeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
@ApplicationScoped
public class ErrorMapper implements ExceptionMapper<Exception> {
  private static final Logger LOGGER = Logger.getLogger(ErrorMapper.class.getName());

  @Inject
  ObjectMapper objectMapper;

  @Override
  public Response toResponse(Exception exception) {
    Throwable throwable = exception;
    int code = 500;
    if (throwable instanceof ClientErrorException t) {
      code = t.getResponse().getStatus();
    }

    // This is a Mutiny exception, and it happens,
    // for example, when we try to insert a new user
    // but the name is already in the database
    if (throwable instanceof CompositeException t) {
      throwable = t.getCause();
    }

    LOGGER.errorf("Status: [%d], Exception: [%s], Message: [%s]",
        code,
        throwable.getClass().getSimpleName(),
        throwable.getMessage());

    ObjectNode exceptionJson = objectMapper.createObjectNode();
    exceptionJson.put("code", code);

    if (exception.getMessage() != null) {
      exceptionJson.put("error", throwable.getMessage());
    }

    return Response.status(code)
        .entity(exceptionJson)
        .build();
  }

}
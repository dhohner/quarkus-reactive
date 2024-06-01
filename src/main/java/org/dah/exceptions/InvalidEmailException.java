package org.dah.exceptions;

import jakarta.ws.rs.WebApplicationException;

import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;

public class InvalidEmailException extends WebApplicationException {
  public InvalidEmailException() {
    super("Email containing @ is Required", UNPROCESSABLE_ENTITY.code());
  }
}

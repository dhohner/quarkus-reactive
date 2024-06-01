package org.dah.exceptions;

import jakarta.ws.rs.ClientErrorException;

import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY;

public class InvalidEmailException extends ClientErrorException {
  public InvalidEmailException() {
    super("email must contain @", UNPROCESSABLE_ENTITY.code());
  }
}

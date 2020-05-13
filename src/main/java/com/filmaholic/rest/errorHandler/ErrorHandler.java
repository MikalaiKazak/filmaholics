package com.filmaholic.rest.errorHandler;

import com.filmaholic.model.error.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

@Component
@Slf4j
public class ErrorHandler {

  public ErrorHandler() {
    log.info("Initialising error handler class");
  }

  public static Mono<ServerResponse> handleError(Throwable throwable, ServerRequest request) {

    if (throwable instanceof NoSuchElementException) {
      return handleNotFound(request, throwable);
    }

    return ServerResponse
        .badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .build();
  }

  private static Mono<ServerResponse> handleNotFound(ServerRequest request, Throwable throwable) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
    String errorDate = dateFormat.format(new Date());
    Error errorResponse = Error.builder()
        .status(404)
        .path(request.path())
        .error("Video not found. It most likely does not exist")
        .timestamp(errorDate)
        .build();

    log.error("The video at [" + request.path() + "] could not be found");
    return ServerResponse.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(errorResponse), Error.class);
  }
}

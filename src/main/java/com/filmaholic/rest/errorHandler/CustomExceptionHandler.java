package com.filmaholic.rest.errorHandler;

import com.filmaholic.model.error.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@Order(-2)
@Slf4j
public class CustomExceptionHandler extends WebFluxResponseStatusExceptionHandler {

  public CustomExceptionHandler() {
    log.info("Initialising custom exception handler");
  }

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    HttpStatus status = super.determineStatus(ex);
    SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");

    Error error = Error.builder()
        .timestamp(simpleDate.format(new Date()))
        .status(status != null ? status.value() : 0)
        .path(exchange.getRequest().getPath().toString())
        .build();

    if (status != null && exchange.getResponse().setStatusCode(status)) {
      if (status == HttpStatus.NOT_FOUND) {
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        error.setError("Route Not Found");
        log.error(buildResponse(exchange, ex));
        log.info(ex.getMessage());
      } else if (status == HttpStatus.BAD_REQUEST) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        error.setError("Bad Request");
        log.warn(buildResponse(exchange, ex));
      } else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        error.setError("The Server Encountered an error");
        log.trace(buildResponse(exchange, ex));
      }
    }

    byte[] bytes = error.toString().getBytes();
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

    return exchange
        .getResponse()
        .writeWith(Mono.just(buffer));
  }

  private String buildResponse(ServerWebExchange exchange, Throwable ex) {
    return ("Using Custom Handler - Failed to handle request [" + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI() + "]: " + ex.getMessage());
  }
}

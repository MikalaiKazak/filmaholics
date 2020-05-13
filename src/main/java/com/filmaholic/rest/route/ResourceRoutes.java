package com.filmaholic.rest.route;

import com.filmaholic.rest.errorHandler.ErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ResourceRoutes {

  @Bean
  RouterFunction<ServerResponse> videoEndPoint(ResourceRouteHandler resourceRouteHandler) {
    return route(GET("/api/v1/files/{resourceName}"), resourceRouteHandler::getResource)
        .andRoute(GET("/api/v1/files").and(RequestPredicates.accept(APPLICATION_JSON))
            .and(contentType(APPLICATION_JSON)), resourceRouteHandler::findAll)
        .filter((request, next) -> next.handle(request)
            .onErrorResume(throwable -> ErrorHandler.handleError(throwable, request)));
  }
}

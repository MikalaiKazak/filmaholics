package com.filmaholic.rest.route;

import com.filmaholic.model.pagination.Pagination;
import com.filmaholic.model.resource.ResourceMetaData;
import com.filmaholic.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

@Component
public class ResourceRouteHandler {

  private final ResourceService resourceService;

  @Autowired
  public ResourceRouteHandler(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  public Mono<ServerResponse> getResource(ServerRequest request) {
    String fileName = request.pathVariable("resourceName");
    HttpHeaders requestHeaders = request.headers().asHttpHeaders();
    Mono<UrlResource> videoResource = resourceService.getResourceByName(fileName);
    Mono<ResourceRegion> resourceRegion = resourceService.getRegion(videoResource, requestHeaders);
    return resourceRegion.flatMap(r -> ServerResponse
        .status(HttpStatus.PARTIAL_CONTENT)
        .contentLength(r.getCount())
        .headers(headers -> headers.setCacheControl(CacheControl.noCache()))
        .header("Access-Control-Allow-Origin", "*")
        .body(resourceRegion, ResourceRegion.class))
        .doOnError(throwable -> {
          throw Exceptions.propagate(throwable);
        });
  }

  public Mono<ServerResponse> findAll(ServerRequest request) {
    Mono<Pagination> pagination = request.bodyToMono(Pagination.class);
    Mono<Page<ResourceMetaData>> metaData = resourceService.findAll(pagination)
        .doOnError(t -> {
          throw Exceptions.propagate(t);
        });

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .cacheControl(CacheControl.noCache())
        .location(request.uri())
        .body(metaData, ResourceMetaData.class);
  }
}

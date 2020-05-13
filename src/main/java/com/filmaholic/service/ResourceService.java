package com.filmaholic.service;

import com.filmaholic.model.pagination.Pagination;
import com.filmaholic.model.resource.ResourceMetaData;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface ResourceService {

  void uploadResource(FilePart resource, String resourceName);

  Mono<UrlResource> getResourceByName(String fileName);

  Mono<ResourceRegion> getRegion(Mono<UrlResource> resource, HttpHeaders headers);

  Boolean deleteResource(String resourceName);

  long lengthOf(UrlResource urlResource);

  Mono<Page<ResourceMetaData>> findAll(Mono<Pagination> pagination);
}

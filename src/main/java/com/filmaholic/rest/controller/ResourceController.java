package com.filmaholic.rest.controller;

import com.filmaholic.service.ResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/files")
public class ResourceController {

  private final ResourceService resourceService;

  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @PostMapping(
      value = "upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadResource(
      @RequestPart("resource") final FilePart resource,
      @RequestPart("resourceName") final String resourceName) {
    resourceService.uploadResource(resource, resourceName);
  }

  @DeleteMapping("{resourceName}")
  @ResponseStatus(HttpStatus.OK)
  public Boolean deleteResource(@PathVariable("resourceName") final String resourceName) {
    return resourceService.deleteResource(resourceName);
  }
}

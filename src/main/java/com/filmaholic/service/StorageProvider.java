package com.filmaholic.service;

import com.filmaholic.model.resource.ResourceMetaData;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;

import java.io.File;
import java.nio.file.Path;

public interface StorageProvider {

  File saveResource(FilePart resource, String resourceName);

  UrlResource getResource(ResourceMetaData metaData);

  Boolean deleteResource(ResourceMetaData metaData);

  Path getRootFolder();
}

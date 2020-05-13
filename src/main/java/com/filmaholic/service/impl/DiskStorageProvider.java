package com.filmaholic.service.impl;

import com.filmaholic.exception.LocationNotFoundException;
import com.filmaholic.exception.ServerException;
import com.filmaholic.model.resource.ResourceMetaData;
import com.filmaholic.service.StorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class DiskStorageProvider implements StorageProvider {

  private Path rootFolder;

  @Autowired
  public void DiskStorageProvider(@Value("${server.file.location}") String rootFolder) {
    this.rootFolder = Paths.get(rootFolder);
  }

  @Override
  public File saveResource(FilePart resource, String resourceName) throws ServerException {
    try {
      createRootDirectoriesIfNotExist();
      File file = new File(rootFolder + File.separator + resourceName + "." + FilenameUtils.getExtension(resource.filename()));
      if (!file.createNewFile()) {
        throw new ServerException("Could not store file");
      }
      resource.transferTo(file);
      return file;
    } catch (IOException | LocationNotFoundException exc) {
      throw new ServerException(exc.getMessage());
    }
  }

  @Override
  public UrlResource getResource(ResourceMetaData metaData) throws ServerException {
    try {
      return new UrlResource("file:" + metaData.getPath());
    } catch (MalformedURLException e) {
      throw new ServerException(e.getMessage());
    }
  }

  @Override
  public Boolean deleteResource(ResourceMetaData metaData) {
    return new File(metaData.getPath()).delete();
  }

  private void createRootDirectoriesIfNotExist() throws IOException {
    if (!Files.exists(rootFolder)) {
      Files.createDirectories(rootFolder);
    }
  }

  @Override
  public Path getRootFolder() {
    return rootFolder;
  }
}

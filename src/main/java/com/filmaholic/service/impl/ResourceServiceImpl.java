package com.filmaholic.service.impl;

import com.filmaholic.model.pagination.Pagination;
import com.filmaholic.model.resource.ResourceMetaData;
import com.filmaholic.repository.FileMetadataRepository;
import com.filmaholic.service.ResourceService;
import com.filmaholic.service.StorageProvider;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import static java.lang.Long.min;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final StorageProvider storageProvider;
  private final FileMetadataRepository fileMetadataRepository;
  private Long chunkSize;

  public ResourceServiceImpl(@Value("${server.chunk.size}") String chunkSize,
                             StorageProvider storageProvider,
                             FileMetadataRepository fileMetadataRepository) {
    this.chunkSize = Long.parseLong(chunkSize);
    this.storageProvider = storageProvider;
    this.fileMetadataRepository = fileMetadataRepository;
  }

  @Override
  public void uploadResource(FilePart resource, String resourceName) {
    fileMetadataRepository.findById(resourceName)
        .ifPresent(resourceMetaData -> {
          throw new IllegalArgumentException("Resource with name " + resourceName + " already uploaded");
        });

    File file = storageProvider.saveResource(resource, resourceName);
    fileMetadataRepository.save(extractMetadata(file, resourceName));
  }


  @Override
  public Mono<UrlResource> getResourceByName(String resourceName) {
    return Mono.create(monoSink -> {
      ResourceMetaData metaData = fileMetadataRepository.findById(resourceName)
          .orElseThrow(() -> new NoSuchElementException("File with name " + resourceName + " not exist"));
      UrlResource video = storageProvider.getResource(metaData);
      if (!video.exists()) {
        fileMetadataRepository.delete(metaData);
        monoSink.error(new NoSuchElementException("File with name " + resourceName + " was deleted"));
      } else {
        monoSink.success(video);
      }
    });
  }

  @Override
  public Mono<ResourceRegion> getRegion(Mono<UrlResource> resource, HttpHeaders headers) {
    HttpRange range = headers.getRange().size() != 0 ? headers.getRange().get(0) : null;
    return resource.map(urlResource -> {
      long contentLength = lengthOf(urlResource);
      if (range != null) {
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long resourceLength = end - start + 1;
        long rangeLength = min(chunkSize, resourceLength);
        return new ResourceRegion(urlResource, start, rangeLength);
      } else {
        long rangeLength = min(chunkSize, contentLength);
        return new ResourceRegion(urlResource, 0, rangeLength);
      }
    });
  }

  @Override
  public Boolean deleteResource(String resourceName) {
    ResourceMetaData metaData = fileMetadataRepository.findById(resourceName)
        .orElseThrow(() -> new NoSuchElementException("File with name " + resourceName + " not exist"));
    boolean deleteResult = storageProvider.deleteResource(metaData);
    if (deleteResult) {
      fileMetadataRepository.delete(metaData);
    }
    return deleteResult;
  }

  @Override
  public long lengthOf(UrlResource urlResource) {
    long fileLength;
    try {
      fileLength = urlResource.contentLength();
    } catch (IOException e) {
      throw Exceptions.propagate(new NoSuchElementException());
    }
    return fileLength;
  }

  @Override
  public Mono<Page<ResourceMetaData>> findAll(Mono<Pagination> pagination) {
    return pagination.map(p -> PageRequest.of(p.getPage(), p.getSize(), Sort.by(p.getDirection(), p.getSort())))
        .map(fileMetadataRepository::findAll);
  }

  private ResourceMetaData extractMetadata(File file, String resourceName) {
    return ResourceMetaData.builder()
        .name(resourceName)
        .createdAt(System.currentTimeMillis())
        .contentSize(file.length())
        .extension(FilenameUtils.getExtension(file.getName()))
        .path(file.getAbsolutePath())
        .build();
  }
}

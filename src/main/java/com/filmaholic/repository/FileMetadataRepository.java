package com.filmaholic.repository;

import com.filmaholic.model.resource.ResourceMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends MongoRepository<ResourceMetaData, String> {
}

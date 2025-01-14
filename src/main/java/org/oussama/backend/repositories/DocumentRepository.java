package org.oussama.backend.repositories;


import org.oussama.backend.models.DocumentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentRepository extends MongoRepository<DocumentModel, String> {

}
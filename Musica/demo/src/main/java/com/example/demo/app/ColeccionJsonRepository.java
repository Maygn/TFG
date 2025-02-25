package com.example.demo.app;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ColeccionJsonRepository extends MongoRepository<ColeccionJson, String>{
	public Optional<ColeccionJson> findById(String id);
}

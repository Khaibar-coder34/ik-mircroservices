package com.indiasekeukenservices.productservice.data;

import com.indiasekeukenservices.productservice.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}

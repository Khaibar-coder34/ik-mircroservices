package com.indiasekeukenservices.productservice.application;

import com.indiasekeukenservices.productservice.data.ProductRepository;
import com.indiasekeukenservices.productservice.domain.Product;
import com.indiasekeukenservices.productservice.event.ProductCreatedEvent;
import com.indiasekeukenservices.productservice.event.ProductDeletedEvent;
import com.indiasekeukenservices.productservice.event.ProductUpdatedEvent;
import com.indiasekeukenservices.productservice.presentation.dto.request.ProductRequest;
import com.indiasekeukenservices.productservice.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
//    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public ProductResponse createProduct(ProductRequest productRequest) {
        // Create a new product from the request
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .productType(productRequest.getProductType())
                .build();

        // Save the product to the database
        product = productRepository.save(product);
        log.info("Product {} is saved", product.getId());

        // Create and publish the ProductCreatedEvent
        ProductCreatedEvent event = new ProductCreatedEvent(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getProductType()
        );

        // Send the event to the Kafka topic
        kafkaTemplate.send("productCreatedTopic", event);
        log.info("Product {} is SENT to INVENTORY-SERVICE", product.getId());

        // Convert the product to a ProductResponse and return it
        return mapToProductResponse(product);
    }


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
        return mapToProductResponse(product);
    }

//    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
//
//        product.setName(productRequest.getName());
//        product.setDescription(productRequest.getDescription());
//        product.setPrice(productRequest.getPrice());
//        product.setProductType(productRequest.getProductType());
//
//        product = productRepository.save(product);
//        return mapToProductResponse(product);
//    }

    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setProductType(productRequest.getProductType());

        product = productRepository.save(product);

        ProductUpdatedEvent updatedEvent = new ProductUpdatedEvent(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getProductType()
        );

        kafkaTemplate.send("productUpdatedTopic", updatedEvent);
        log.info("Product {} is updated and sent to INVENTORY-SERVICE", product.getId());

        return mapToProductResponse(product);
    }

//    public void deleteProduct(String productId) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
//        productRepository.delete(product);
//        log.info("Product {} is deleted", productId);
//    }

    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
        productRepository.delete(product);

        ProductDeletedEvent deletedEvent = new ProductDeletedEvent(product.getId());
        kafkaTemplate.send("productDeletedTopic", deletedEvent);

        log.info("Product {} is deleted and sent to INVENTORY-SERVICE", productId);
    }




    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .productType(product.getProductType())
                .build();
    }
}

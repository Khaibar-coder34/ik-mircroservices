package com.indiasekeukenservices.productservice.application;

import com.indiasekeukenservices.productservice.data.ProductRepository;
import com.indiasekeukenservices.productservice.domain.Product;
import com.indiasekeukenservices.productservice.event.ProductCreatedEvent;
import com.indiasekeukenservices.productservice.presentation.dto.request.ProductRequest;
import com.indiasekeukenservices.productservice.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;


//    public void createProduct(ProductRequest productRequest) {
//        Product product = Product.builder()
//                .name(productRequest.getName())
//                .description(productRequest.getDescription())
//                .price(productRequest.getPrice())
//                .build();
//
//        productRepository.save(product);
//        log.info("Product {} is saved", product.getId());
//    }

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        product = productRepository.save(product);
        log.info("Product {} is saved", product.getId());

        // Maak en publiceer het ProductCreatedEvent
        ProductCreatedEvent event = new ProductCreatedEvent(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );

        kafkaTemplate.send("productCreatedTopic", event);
        log.info("Product {} is SENDED to INVENTORY-SERVICE", product.getId());
    }





    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}

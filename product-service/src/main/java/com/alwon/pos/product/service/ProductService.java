package com.alwon.pos.product.service;

import com.alwon.pos.product.dto.*;
import com.alwon.pos.product.model.Category;
import com.alwon.pos.product.model.Product;
import com.alwon.pos.product.repository.CategoryRepository;
import com.alwon.pos.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getActiveProducts() {
        log.debug("Fetching active products");
        return productRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return convertToDto(product);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductBySku(String sku) {
        log.debug("Fetching product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return convertToDto(product);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductByBarcode(String barcode) {
        log.debug("Fetching product by barcode: {}", barcode);
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return convertToDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String search) {
        log.debug("Searching products with term: {}", search);
        return productRepository.searchProducts(search).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        log.debug("Fetching products by category ID: {}", categoryId);
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts() {
        log.debug("Fetching low stock products");
        return productRepository.findLowStockProducts().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        // Check if SKU already exists
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new DuplicateResourceException("Product with SKU " + request.getSku() + " already exists");
        }

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setMinStock(request.getMinStock());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.getActive());
        product.setTaxable(request.getTaxable());
        product.setTaxRate(request.getTaxRate());
        product.setBarcode(request.getBarcode());
        product.setBrand(request.getBrand());
        product.setUnit(request.getUnit());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        return convertToDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (request.getName() != null)
            product.setName(request.getName());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getStock() != null)
            product.setStock(request.getStock());
        if (request.getMinStock() != null)
            product.setMinStock(request.getMinStock());
        if (request.getImageUrl() != null)
            product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null)
            product.setActive(request.getActive());
        if (request.getTaxable() != null)
            product.setTaxable(request.getTaxable());
        if (request.getTaxRate() != null)
            product.setTaxRate(request.getTaxRate());
        if (request.getBarcode() != null)
            product.setBarcode(request.getBarcode());
        if (request.getBrand() != null)
            product.setBrand(request.getBrand());
        if (request.getUnit() != null)
            product.setUnit(request.getUnit());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        Product updated = productRepository.save(product);
        log.info("Product updated successfully");
        return convertToDto(updated);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product ID: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully");
    }

    @Transactional
    public ProductDto updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product ID: {} to quantity: {}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setStock(quantity);
        Product updated = productRepository.save(product);
        log.info("Stock updated successfully");
        return convertToDto(updated);
    }

    @Transactional
    public ProductDto adjustStock(Long id, Integer adjustment) {
        log.info("Adjusting stock for product ID: {} by: {}", id, adjustment);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        int newStock = product.getStock() + adjustment;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        product.setStock(newStock);
        Product updated = productRepository.save(product);
        log.info("Stock adjusted successfully. New stock: {}", newStock);
        return convertToDto(updated);
    }

    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setMinStock(product.getMinStock());
        dto.setImageUrl(product.getImageUrl());
        dto.setActive(product.getActive());
        dto.setTaxable(product.getTaxable());
        dto.setTaxRate(product.getTaxRate());
        dto.setBarcode(product.getBarcode());
        dto.setBrand(product.getBrand());
        dto.setUnit(product.getUnit());
        dto.setLowStock(product.isLowStock());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        return dto;
    }
}

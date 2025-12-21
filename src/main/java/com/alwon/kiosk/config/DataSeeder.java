package com.alwon.kiosk.config;

import com.alwon.kiosk.model.Product;
import com.alwon.kiosk.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            seedProducts();
            System.out.println("✓ Database seeded with sample products");
        }
    }

    private void seedProducts() {
        List<Product> products = Arrays.asList(
                createProduct("Leche Entera Alquería 1L", new BigDecimal("4500"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Leche", "Lácteos", 50),
                createProduct("Pan Integral Bimbo", new BigDecimal("3200"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Pan", "Panadería", 30),
                createProduct("Arroz Premium 500g", new BigDecimal("5500"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Arroz", "Granos", 40),
                createProduct("Aceite Girasol 1L", new BigDecimal("8900"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Aceite", "Aceites", 25),
                createProduct("Huevos AA x30", new BigDecimal("12000"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Huevos", "Lácteos", 20),
                createProduct("Azúcar 1kg", new BigDecimal("3800"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Azúcar", "Granos", 35),
                createProduct("Café Colombiano 250g", new BigDecimal("9500"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Café", "Bebidas", 15),
                createProduct("Chocolate Jet 500g", new BigDecimal("6200"),
                        "https://placehold.co/200x200/00BFFF/FFF?text=Chocolate", "Bebidas", 22));

        productRepository.saveAll(products);
    }

    private Product createProduct(String name, BigDecimal price, String imageUrl, String category, int stock) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setCategory(category);
        product.setStock(stock);
        return product;
    }
}

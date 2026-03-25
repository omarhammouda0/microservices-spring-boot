package com.productservice;

import com.productservice.entity.Product;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamPractice {
    public static void main(String[] args) {
        Product p1 = new Product (1L, "Laptop", 1299.99, 1L, Instant.now() ) ;
        Product p2 = new Product(2L, "Mouse", 29.99, 1L, Instant.now());
        Product p3 = new Product(3L, "hhh", 229.99, 1L, Instant.now());
        Product p4 = new Product(4L, "iii", 279.99, 1L, Instant.now());
        Product p5 = new Product(5L, "ppp", 39.99, 1L, Instant.now());


        List<Product> products = List.of(p1, p2, p3 ,p4 , p5);

        Map<String, List<Product>> collect = products.stream ( )
                .collect ( Collectors.groupingBy (
                        product -> product.getPrice () > 100 ? "expensive" : "cheap"
                ));

        collect.forEach((k,v)->{
            System.out.println ( k + " : " + v );
        });


    }


}
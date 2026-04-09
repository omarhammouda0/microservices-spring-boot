package com.productservice;

import com.productservice.entity.Product;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamPractice {

    public static void main(String[] args) {
        Product p1 = new Product (1L, "Laptop", 1299.99, 1L , true , Instant.now()  ) ;
        Product p2 = new Product(2L, "Mouse", 29.99, 1L, true , Instant.now());
        Product p3 = new Product(3L, "hhh", 229.99, 1L, true , Instant.now());
        Product p4 = new Product(4L, "iii", 279.99, 1L, true , Instant.now());
        Product p5 = new Product(5L, "ppp", 39.99, 1L,true , Instant.now());

        List<Product> products = List.of(p1, p2, p3 ,p4 , p5);

        StreamPractice p = new StreamPractice ( );
        System.out.println ( p.getSumPricesForEachCategory ( products ) );

    }

    Map <String , Double> getAveragePriceByCategory (List<Product> products) {

        return products
                .stream ()
                .collect ( Collectors.groupingBy
                        ( product -> product.getPrice () > 100 ? "expensive" : "cheap" ,
                                Collectors.averagingDouble ( Product::getPrice )
                                )  );


    }

    Map<String, Optional<Map<String, Double>>> getMostExpensiveProductByCategory(List<Product> products) {
        return products.stream ( )
                .collect ( Collectors.groupingBy (
                        product -> product.getPrice ( ) > 100 ? "expensive" : "cheap" ,
                        Collectors.collectingAndThen (
                                Collectors.maxBy ( Comparator.comparing ( Product::getPrice ) ) ,
                                optional -> optional.map ( product -> Map.of ( product.getName() , product.getPrice ( ) ) )
                        )
                ) );
    }

    Map <String , Double> getSumPricesForEachCategory (List<Product> products) {

        return products.stream ()
                .collect ( Collectors.groupingBy (
                        product -> product.getPrice () > 100 ? "expensive" : "cheap" ,
                        Collectors.summingDouble ( Product::getPrice )
                        )
                );

    }

//    Exercise 10
//    From the product list, return a single Map<String, Double> that contains the total price of all products in each category (expensive/cheap).
//    Go.

//    From the product list, group products by category (expensive/cheap)
//    and return the most expensive product in each group.








//        Map<String, List<Product>> collect = products.stream ( )
//                .collect ( Collectors.groupingBy (
//                        product -> product.getPrice () > 100 ? "expensive" : "cheap"
//                ));
//
//        collect.forEach((k,v)->{
//            System.out.println ( k + " : " + v );
//        });


}
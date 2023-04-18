package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository // JpaRepository를 상속받으면 필요없음
public interface ProductRepository extends JpaRepository<Product, Long> {
}
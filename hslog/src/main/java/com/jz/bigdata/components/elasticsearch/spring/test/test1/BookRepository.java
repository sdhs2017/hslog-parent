package com.jz.bigdata.components.elasticsearch.spring.test.test1;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.Repository;

//public interface BookRepository extends Repository<Book, String> {
public interface BookRepository extends ElasticsearchCrudRepository<Book, String> {

    List<Book> findByNameAndPrice(String name, Integer price);

    List<Book> findByNameOrPrice(String name, Integer price);
    
    Page<Book> findByName(String name,Pageable page);

    Page<Book> findByNameNot(String name,Pageable page);

    Page<Book> findByPriceBetween(int price,Pageable page);

    Page<Book> findByNameLike(String name,Pageable page);

    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"message\" : \"?0\"}}}}")
    Page<Book> findByMessage(String message, Pageable pageable);
}
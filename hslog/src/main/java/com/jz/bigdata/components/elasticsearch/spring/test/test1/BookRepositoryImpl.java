package com.jz.bigdata.components.elasticsearch.spring.test.test1;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
public class BookRepositoryImpl implements BookRepository {

	@Override
	public <S extends Book> S index(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Book> search(QueryBuilder query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> search(QueryBuilder query, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> search(SearchQuery searchQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> searchSimilar(Book entity, String[] fields, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<Book> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Book> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Book> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Book> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Book> findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Book> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Book> findAllById(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Book entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends Book> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Book> findByNameAndPrice(String name, Integer price) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Book> findByNameOrPrice(String name, Integer price) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> findByName(String name, Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> findByNameNot(String name, Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> findByPriceBetween(int price, Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> findByNameLike(String name, Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Book> findByMessage(String message, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}

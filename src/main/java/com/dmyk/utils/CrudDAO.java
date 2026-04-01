package com.dmyk.utils;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T> {

	Optional<T> findById(int id);

	List<T> findAll();

	T create(T dto);

	void update(T dto);

}

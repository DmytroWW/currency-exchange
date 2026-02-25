package com.dmyk.utils;

import java.sql.Connection;
import java.util.List;

public abstract class DataAccessObject<T extends DataTransferObject> {

	protected final Connection connection;

	public DataAccessObject(Connection connection) {
		this.connection = connection;
	}

	public abstract T findById(int id);

	public abstract List<T> findAll();

	public abstract T create(T dto);

	public abstract T update(T dto);

}

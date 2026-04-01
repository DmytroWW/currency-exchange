package com.dmyk.utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {
	private static final HikariConfig config = new HikariConfig();
	private static final HikariDataSource ds;

	static {
		config.setJdbcUrl("jdbc:sqlite:D:/Eclipse workspace/CurrencyExchange/db/currency.db");
		config.setDriverClassName("org.sqlite.JDBC");

		config.setMaximumPoolSize(10);
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		ds = new HikariDataSource(config);

	}

	private DataSource() {
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

}

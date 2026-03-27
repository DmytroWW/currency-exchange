package com.dmyk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dmyk.model.Currency;
import com.dmyk.utils.DataAccessObject;

public class CurrencyDAO extends DataAccessObject<Currency> {

	private static final String FIND_ALL = "SELECT id, code, full_name, sign FROM Currencies";
	private static final String INSERT = "INSERT INTO Currencies (code, full_name, sign) VALUES (?, ?, ?)";
	private static final String FIND_BY_ID = "SELECT id, code, full_name, sign FROM Currencies WHERE id = ?";
	private static final String FIND_BY_CODE = "SELECT id, code, full_name, sign FROM Currencies WHERE code = ?";

	public CurrencyDAO(Connection connection) {
		super(connection);

	}

	@Override
	public Currency findById(int id) {
		try (PreparedStatement statement = this.connection.prepareStatement(FIND_BY_ID)) {
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return new Currency(rs.getInt("id"), rs.getString("code"), rs.getString("full_name"),
						rs.getString("sign"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Currency> findAll() {
		List<Currency> currencies = new ArrayList<>();

		try (PreparedStatement statement = this.connection.prepareStatement(FIND_ALL)) {
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				currencies.add(mapResultSetToCurrency(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Помилка при отриманні списку валют з БД", e);
		}
		return currencies;
	}

	public Optional<Currency> findByCode(String code) {
		try (PreparedStatement statement = this.connection.prepareStatement(FIND_BY_CODE)) {
			statement.setString(1, code);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return Optional.of(mapResultSetToCurrency(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Помилка при пошуку валюти за кодом: " + code, e);
		}
		return Optional.empty();
	}

	private Currency mapResultSetToCurrency(ResultSet rs) throws SQLException {
		return new Currency(rs.getInt("id"), rs.getString("code"), rs.getString("full_name"), rs.getString("sign"));
	}

	@Override
	public Currency create(Currency dto) {
		try (PreparedStatement statement = this.connection.prepareStatement(INSERT)) {
			statement.setString(1, dto.getCode());
			statement.setString(2, dto.getFullName());
			statement.setString(3, dto.getSign());

			statement.executeUpdate();

			try (Statement idStatement = this.connection.createStatement()) {
				ResultSet rs = idStatement.executeQuery("SELECT last_insert_rowid()");
				if (rs.next()) {
					int generatedId = rs.getInt(1);
					return this.findById(generatedId);
				}
			}
		} catch (SQLException e) {

			throw new RuntimeException(e);
		}
		return dto;
	}

	@Override
	public Currency update(Currency dto) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.dmyk.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dmyk.exception.DatabaseException;
import com.dmyk.model.Currency;
import com.dmyk.utils.CrudDAO;
import com.dmyk.utils.DataSource;

public class CurrencyDAO implements CrudDAO<Currency> {
	private static final CurrencyDAO INSTANCE = new CurrencyDAO();

	private CurrencyDAO() {
	}

	public static CurrencyDAO getInstance() {
		return INSTANCE;
	}

	private static final String FIND_ALL = "SELECT id, code, full_name, sign FROM Currencies";
	private static final String INSERT = "INSERT INTO Currencies (code, full_name, sign) VALUES (?, ?, ?)";
	private static final String FIND_BY_ID = "SELECT id, code, full_name, sign FROM Currencies WHERE id = ?";
	private static final String FIND_BY_CODE = "SELECT id, code, full_name, sign FROM Currencies WHERE code = ?";

	@Override
	public Optional<Currency> findById(int id) {

		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

			statement.setInt(1, id);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToCurrency(rs));
				}
			}

		} catch (SQLException e) {
			throw new DatabaseException("Error finding currency by id", e);
		}
		return Optional.empty();
	}

	@Override
	public List<Currency> findAll() {
		List<Currency> currencies = new ArrayList<>();

		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {

			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					currencies.add(mapResultSetToCurrency(rs));
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("Error finding all currencies", e);
		}
		return currencies;
	}

	public Optional<Currency> findByCode(String code) {
		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE)) {
			statement.setString(1, code);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToCurrency(rs));
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private Currency mapResultSetToCurrency(ResultSet rs) throws SQLException {
		return new Currency(rs.getInt("id"), rs.getString("code"), rs.getString("full_name"), rs.getString("sign"));
	}

	@Override
	public Currency create(Currency dto) {
		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT)) {

			statement.setString(1, dto.getCode());
			statement.setString(2, dto.getFullName());
			statement.setString(3, dto.getSign());
			statement.executeUpdate();

			try (Statement idStatement = connection.createStatement();
					ResultSet rs = idStatement.executeQuery("SELECT last_insert_rowid()")) {

				if (rs.next()) {
					int generatedId = rs.getInt(1);
					return this.findById(generatedId)
							.orElseThrow(() -> new DatabaseException("Failed to retrieve created currency from DB"));
				}
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 19) {
				throw new DatabaseException("Currency code already exists", e);
			}
			throw new DatabaseException("Error creating currency", e);
		}
		return dto;
	}

	@Override
	public void update(Currency dto) {
		// TODO Auto-generated method stub
	}

}

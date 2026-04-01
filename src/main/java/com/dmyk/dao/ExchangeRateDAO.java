package com.dmyk.dao;

import java.math.BigDecimal;
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
import com.dmyk.model.ExchangeRate;
import com.dmyk.utils.CrudDAO;
import com.dmyk.utils.DataSource;

public class ExchangeRateDAO implements CrudDAO<ExchangeRate> {
	// 1. Singleton
	private static final ExchangeRateDAO INSTANCE = new ExchangeRateDAO();

	private ExchangeRateDAO() {
	}

	public static ExchangeRateDAO getInstance() {
		return INSTANCE;
	}

	// SQL запити виносимо в константи (static final)
	private static final String FIND_ALL = "SELECT er.id AS id, "
			+ "bc.id AS base_id, bc.code AS base_code, bc.full_name AS base_name, bc.sign AS base_sign, "
			+ "tc.id AS target_id, tc.code AS target_code, tc.full_name AS target_name, tc.sign AS target_sign, "
			+ "er.rate AS rate FROM ExchangeRates er " + "JOIN Currencies bc ON er.base_currency_id = bc.id "
			+ "JOIN Currencies tc ON er.target_currency_id = tc.id";

	private static final String FIND_BY_ID = FIND_ALL + " WHERE er.id = ?";
	private static final String INSERT = "INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
	private static final String UPDATE_BY_CODES = "UPDATE ExchangeRates SET rate = ? "
			+ "WHERE base_currency_id = (SELECT id FROM Currencies WHERE code = ?) "
			+ "AND target_currency_id = (SELECT id FROM Currencies WHERE code = ?)";
	private static final String FIND_BY_CODES = FIND_ALL + " WHERE bc.code = ? AND tc.code = ?";

	@Override
	public Optional<ExchangeRate> findById(int id) {
		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToExchangeRate(rs));
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("Database error during findById for ExchangeRate", e);
		}
		return Optional.empty();
	}

	@Override
	public List<ExchangeRate> findAll() {

		List<ExchangeRate> exchangeRates = new ArrayList<>();

		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_ALL);
				ResultSet rs = statement.executeQuery()) {

			while (rs.next()) {
				exchangeRates.add(mapResultSetToExchangeRate(rs));
			}

		} catch (SQLException e) {
			throw new DatabaseException("Database error during findAll exchange rates", e);
		}

		return exchangeRates;
	}

	@Override
	public ExchangeRate create(ExchangeRate dto) {
		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, dto.getBaseCurrency().getId());
			statement.setInt(2, dto.getTargetCurrency().getId());
			statement.setBigDecimal(3, dto.getRate());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new DatabaseException("Error in ExchangeRate creating. No added rows");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					dto.setId(generatedKeys.getInt(1));
				} else {
					throw new DatabaseException("Error in ExchangeRate creating. Dont get ID");
				}
			}
		} catch (SQLException e) {

			if (e.getErrorCode() == 19) {
				throw new DatabaseException("Currency pair already exist", e);
			}
			throw new DatabaseException("Data base error in process Exchange Rate creatings", e);
		}

		return dto;
	}

	@Override
	public void update(ExchangeRate dto) {

	}

	public Optional<ExchangeRate> updateByCodes(String baseCode, String targetCode, BigDecimal rate) {

		try (Connection connection = DataSource.getConnection();
				PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BY_CODES)) {
			updateStatement.setBigDecimal(1, rate);
			updateStatement.setString(2, baseCode);
			updateStatement.setString(3, targetCode);

			int affectedRows = updateStatement.executeUpdate();

			if (affectedRows == 0) {
				return Optional.empty();
			}
		} catch (SQLException e) {
			throw new DatabaseException("Database error while updating rate for: " + baseCode + targetCode, e);
		}

		return findByCodes(baseCode, targetCode);
	}

	public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
		try (Connection connection = DataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(FIND_BY_CODES)) {
			statement.setString(1, baseCode);
			statement.setString(2, targetCode);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToExchangeRate(rs));
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(
					"Database error while fetching exchange rate for codes: " + baseCode + " and " + targetCode, e);
		}

		return Optional.empty();

	}

	private ExchangeRate mapResultSetToExchangeRate(ResultSet rs) throws SQLException {
		Currency base = new Currency(rs.getInt("base_id"), rs.getString("base_code"), rs.getString("base_name"),
				rs.getString("base_sign"));
		Currency target = new Currency(rs.getInt("target_id"), rs.getString("target_code"), rs.getString("target_name"),
				rs.getString("target_sign"));
		return new ExchangeRate(rs.getInt("id"), base, target, rs.getBigDecimal("rate"));
	}
}

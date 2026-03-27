package com.dmyk.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.dmyk.model.Currency;
import com.dmyk.model.ExchangeRate;
import com.dmyk.utils.DataAccessObject;

public class ExchangeRateDAO extends DataAccessObject<ExchangeRate> {
	String FIND_ALL = "SELECT " + "er.id AS id, "
			+ "bc.id AS base_id, bc.code AS base_code, bc.full_name AS base_name, bc.sign AS base_sign, "
			+ "tc.id AS target_id, tc.code AS target_code, tc.full_name AS target_name, tc.sign AS target_sign, "
			+ "er.rate AS rate " + "FROM ExchangeRates er " + "JOIN Currencies bc ON er.base_currency_id = bc.id "
			+ "JOIN Currencies tc ON er.target_currency_id = tc.id";

	String FIND_BY_ID = "SELECT " + "er.id AS id, "
			+ "bc.id AS base_id, bc.code AS base_code, bc.full_name AS base_name, bc.sign AS base_sign, "
			+ "tc.id AS target_id, tc.code AS target_code, tc.full_name AS target_name, tc.sign AS target_sign, "
			+ "er.rate AS rate " + "FROM ExchangeRates er " + "JOIN Currencies bc ON er.base_currency_id = bc.id "
			+ "JOIN Currencies tc ON er.target_currency_id = tc.id " + "WHERE er.id = ?";

	String INSERT = "INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

	String UPDATE = "UPDATE ExchangeRates SET rate = ? WHERE id = ?";

	String UPDATE_BY_CODES = "UPDATE ExchangeRates SET rate = ? "
			+ "WHERE base_currency_id = (SELECT id FROM Currencies WHERE code = ?) "
			+ "AND target_currency_id = (SELECT id FROM Currencies WHERE code = ?)";

	String FIND_BY_CODES = FIND_ALL + " WHERE bc.code = ? AND tc.code = ?";

	public ExchangeRateDAO(Connection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExchangeRate findById(int id) {

		try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {

			statement.setInt(1, id);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					Currency baseCurrency = mapCurrency(rs, "base_");
					Currency targetCurrency = mapCurrency(rs, "target_");

					return new ExchangeRate(rs.getInt("id"), baseCurrency, targetCurrency, rs.getBigDecimal("rate"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error during findById", e);
		}

		return null;
	}

	@Override
	public List<ExchangeRate> findAll() {

		List<ExchangeRate> exchangeRates = new ArrayList<>();

		try (PreparedStatement statement = connection.prepareStatement(FIND_ALL);
				ResultSet rs = statement.executeQuery()) {

			while (rs.next()) {
				Currency baseCurrency = mapCurrency(rs, "base_");
				Currency targetCurrency = mapCurrency(rs, "target_");

				ExchangeRate exchangeRate = new ExchangeRate(rs.getInt("id"), baseCurrency, targetCurrency,
						rs.getBigDecimal("rate"));

				exchangeRates.add(exchangeRate);
			}

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException("Database error during findAll exchange rates", e);
		}

		return exchangeRates;
	}

	@Override
	public ExchangeRate create(ExchangeRate dto) {
		try (PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, dto.getBaseCurrency().getId());
			statement.setInt(2, dto.getTargetCurrency().getId());
			statement.setBigDecimal(3, dto.getRate());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Error in ExchangeRate creating. No added rows");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					dto.setId(generatedKeys.getInt(1));
				} else {
					throw new SQLException("Error in ExchangeRate creating. Dont get ID");
				}
			}
		} catch (SQLException e) {

			if (e.getErrorCode() == 19) {
				throw new RuntimeException("Currency pair already exist", e);
			}
			throw new RuntimeException("Data base error in process Exchange Rate creatings", e);
		}

		return dto;
	}

	@Override
	public ExchangeRate update(ExchangeRate dto) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExchangeRate updateByCodes(String baseCode, String targetCode, BigDecimal rate) {
		try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BY_CODES)) {
			updateStatement.setBigDecimal(1, rate);
			updateStatement.setString(2, baseCode);
			updateStatement.setString(3, targetCode);

			int affectedRows = updateStatement.executeUpdate();
			if (affectedRows == 0) {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Database error while updating rate for " + baseCode + targetCode, e);
		}

		try (PreparedStatement findByCodesStatement = connection.prepareStatement(FIND_BY_CODES)) {
			findByCodesStatement.setString(1, baseCode);
			findByCodesStatement.setString(2, targetCode);

			try (ResultSet rs = findByCodesStatement.executeQuery()) {
				if (rs.next()) {
					Currency base = mapCurrency(rs, "base_");
					Currency target = mapCurrency(rs, "target_");
					return new ExchangeRate(rs.getInt("id"), base, target, rs.getBigDecimal("rate"));
				}

			}

		} catch (SQLException e) {
			throw new RuntimeException("Error fetching updated rate from database for " + baseCode + targetCode, e);
		}

		return null;
	}

	private Currency mapCurrency(ResultSet resultSet, String prefix) throws SQLException {
		return new Currency(resultSet.getInt(prefix + "id"), resultSet.getString(prefix + "code"),
				resultSet.getString(prefix + "name"), resultSet.getString(prefix + "sign"));
	}
}

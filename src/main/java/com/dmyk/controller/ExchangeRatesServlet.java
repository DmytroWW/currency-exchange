package com.dmyk.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.model.Currency;
import com.dmyk.model.ExchangeRate;
import com.dmyk.utils.DataSource;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		try (Connection connection = DataSource.getConnection()) {
			ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO(connection);
			List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
			String exchangeRatesInJson = gson.toJson(exchangeRates);
			resp.getWriter().write(exchangeRatesInJson);

		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"message\": \"Database error\"}");
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String baseCode = req.getParameter("baseCurrencyCode");
		String targetCode = req.getParameter("targetCurrencyCode");
		String rateString = req.getParameter("rate");

		if (baseCode == null || targetCode == null || rateString == null || baseCode.isBlank() || targetCode.isBlank()
				|| rateString.isBlank()) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing form fields");
			return;
		}

		try (Connection connection = DataSource.getConnection()) {
			ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO(connection);
			CurrencyDAO currencyDAO = new CurrencyDAO(connection);

			Optional<Currency> baseOptional = currencyDAO.findByCode(baseCode);
			Optional<Currency> targetOptional = currencyDAO.findByCode(targetCode);

			if (baseOptional.isEmpty() || targetOptional.isEmpty()) {
				sendError(resp, HttpServletResponse.SC_NOT_FOUND, "One or both currencies not found");
				return;
			}

			Currency base = baseOptional.get();
			Currency target = targetOptional.get();
			BigDecimal rate = new BigDecimal(rateString);
			ExchangeRate newRate = new ExchangeRate(base, target, rate);

			ExchangeRate savedRate = exchangeRateDAO.create(newRate);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(gson.toJson(savedRate));

		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid rate format");
		} catch (SQLException e) {
			e.printStackTrace();
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
		} catch (RuntimeException e) {
			if (e.getMessage().contains("вже існує")) {
				sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
			} else {
				sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database unavailable");
			}
		}

	}

	private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
		resp.setStatus(code);
		resp.setContentType("application/json");
		resp.getWriter().write(gson.toJson(Collections.singletonMap("message", message)));
	}

}

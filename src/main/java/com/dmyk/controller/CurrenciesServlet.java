package com.dmyk.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.model.Currency;
import com.dmyk.utils.DataSource;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
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
			CurrencyDAO currencyDAO = new CurrencyDAO(connection);
			List<Currency> currencies = currencyDAO.findAll();
			String currenciesInJson = gson.toJson(currencies);
			resp.getWriter().write(currenciesInJson);

		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"message\": \"Database error\"}");
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String code = req.getParameter("code");
		String sign = req.getParameter("sign");

		if (name == null || code == null || sign == null || name.isBlank() || code.isBlank() || sign.isBlank()) {
			sendError(resp, 400, "Відсутнє потрібне поле форми");
			return;
		}

		try (Connection connection = DataSource.getConnection()) {

			CurrencyDAO dao = new CurrencyDAO(connection);

			Currency newCurrency = new Currency(0, code.toUpperCase(), name, sign);

			Currency created = dao.create(newCurrency);

			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.setContentType("application/json");
			resp.getWriter().write(gson.toJson(created));

		} catch (SQLException e) {
			e.printStackTrace();
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error");
		} catch (RuntimeException e) {

			if (e.getMessage().contains("вже існує") || e.getMessage().contains("UNIQUE")) {
				sendError(resp, HttpServletResponse.SC_CONFLICT, "Currency with this code already exists");
			} else {
				sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			e.printStackTrace();
		}
	}

	private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
		resp.setStatus(code);
		resp.setContentType("application/json");
		resp.getWriter().write(gson.toJson(Collections.singletonMap("message", message)));
	}
}

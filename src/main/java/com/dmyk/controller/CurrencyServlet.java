package com.dmyk.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.model.Currency;
import com.dmyk.utils.DataSource;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\": \"Adress dont have currency code\"}");
			return;
		}

		String currencyCode = pathInfo.replace("/", "").toUpperCase();

		try (Connection connection = DataSource.getConnection()) {
			CurrencyDAO currencyDAO = new CurrencyDAO(connection);
			Optional<Currency> currency = currencyDAO.findByCode(currencyCode);

			if (currency.isPresent()) {
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.getWriter().write(gson.toJson(currency.get()));
			} else {

				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter().write("{\"message\": \"Cant find currency\"}");
			}

		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"message\": \"Database error\"}");
			e.printStackTrace();
		}

	}
}

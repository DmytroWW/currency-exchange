package com.dmyk.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;

import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.dto.ExchangeResponseDTO;
import com.dmyk.service.ExchangeService;
import com.dmyk.utils.DataSource;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExchangeService exchangeService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String from = req.getParameter("from");
		String to = req.getParameter("to");
		String amountStr = req.getParameter("amount");

		if (from == null || to == null || amountStr == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}

		try (Connection connection = DataSource.getConnection()) {
			ExchangeRateDAO dao = new ExchangeRateDAO(connection);
			exchangeService = new ExchangeService(dao);
			BigDecimal amount = new BigDecimal(amountStr);
			ExchangeResponseDTO responseDTO = exchangeService.convert(from, to, amount);

			if (responseDTO == null) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
				return;
			}

			String json = new Gson().toJson(responseDTO);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(json);

		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}

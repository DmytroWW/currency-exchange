package com.dmyk.controller;

import java.io.IOException;
import java.math.BigDecimal;

import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.dto.ExchangeResponseDTO;
import com.dmyk.service.ExchangeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchange")
public class ExchangeServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private final ExchangeService exchangeService = new ExchangeService(ExchangeRateDAO.getInstance());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!areParametersValid(req, "from", "to", "amount")) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing or empty parameters: from, to, amount");
			return;
		}

		try {

			ExchangeResponseDTO responseDTO = exchangeService.convert(req.getParameter("from").toUpperCase(),
					req.getParameter("to").toUpperCase(), new BigDecimal(req.getParameter("amount")));

			if (responseDTO == null) {
				sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found for the given pair");
				return;
			}

			sendJson(resp, responseDTO);

		} catch (NumberFormatException e) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid amount format");
		}
	}
}

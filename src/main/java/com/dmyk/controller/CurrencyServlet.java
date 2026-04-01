package com.dmyk.controller;

import java.io.IOException;
import java.util.Optional;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.dto.CurrencyDTO;
import com.dmyk.model.Currency;
import com.dmyk.service.DTOMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/currency/*")
public class CurrencyServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Address doesn't have currency code");
			return;
		}

		String currencyCode = pathInfo.replace("/", "").toUpperCase();

		Optional<Currency> currency = currencyDAO.findByCode(currencyCode);

		if (currency.isPresent()) {

			CurrencyDTO currencyDTO = DTOMapper.toDTO(currency.get());
			sendJson(resp, currencyDTO);
		} else {
			sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Currency not found");
		}
	}
}
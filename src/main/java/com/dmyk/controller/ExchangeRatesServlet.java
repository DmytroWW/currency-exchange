package com.dmyk.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.dto.ExchangeRateDTO;
import com.dmyk.model.Currency;
import com.dmyk.model.ExchangeRate;
import com.dmyk.service.DTOMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private final ExchangeRateDAO exchangeRateDAO = ExchangeRateDAO.getInstance();
	private final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<ExchangeRateDTO> rates = exchangeRateDAO.findAll().stream().map(DTOMapper::toDTO).toList();

		sendJson(resp, rates);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String baseCode = req.getParameter("baseCurrencyCode");
		String targetCode = req.getParameter("targetCurrencyCode");
		String rateString = req.getParameter("rate");
		if (isAnyBlank(baseCode, targetCode, rateString)) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing form fields");
			return;
		}

		Optional<Currency> base = currencyDAO.findByCode(baseCode.toUpperCase());
		Optional<Currency> target = currencyDAO.findByCode(targetCode.toUpperCase());
		if (base.isEmpty() || target.isEmpty()) {
			sendError(resp, HttpServletResponse.SC_NOT_FOUND, "One or both currencies not found");
			return;
		}

		BigDecimal rate = new BigDecimal(rateString);
		ExchangeRate newRate = new ExchangeRate(base.get(), target.get(), rate);
		ExchangeRate savedRate = exchangeRateDAO.create(newRate);
		sendCreatedJson(resp, DTOMapper.toDTO(savedRate));
	}

	private boolean isAnyBlank(String... fields) {
		for (String field : fields) {
			if (field == null || field.isBlank()) {
				return true;
			}
		}
		return false;
	}

}

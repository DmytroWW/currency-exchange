package com.dmyk.controller;

import java.io.IOException;
import java.util.List;

import com.dmyk.dao.CurrencyDAO;
import com.dmyk.dto.CurrencyDTO;
import com.dmyk.model.Currency;
import com.dmyk.service.DTOMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private final CurrencyDAO currencyDAO = CurrencyDAO.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Currency> currencies = currencyDAO.findAll();
		List<CurrencyDTO> dtos = currencies.stream().map(DTOMapper::toDTO).toList();
		sendJson(resp, dtos);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String code = req.getParameter("code");
		String sign = req.getParameter("sign");

		if (isInvalid(name) || isInvalid(code) || isInvalid(sign)) {
			sendError(resp, 400, "Відсутнє потрібне поле форми");
			return;
		}

		Currency newCurrency = new Currency(0, code.toUpperCase(), name, sign);

		Currency created = currencyDAO.create(newCurrency);

		CurrencyDTO createdCurrencyDTO = DTOMapper.toDTO(created);

		sendCreatedJson(resp, createdCurrencyDTO);
	}

	private boolean isInvalid(String value) {
		return value == null || value.isBlank();
	}
}

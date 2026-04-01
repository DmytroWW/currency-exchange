package com.dmyk.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.model.ExchangeRate;
import com.dmyk.service.DTOMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private final ExchangeRateDAO exchangeRateDAO = ExchangeRateDAO.getInstance();

	private record CurrencyPair(String base, String target) {
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getMethod();
		if (method.equals("PATCH")) {
			doPatch(req, resp);
		} else {
			super.service(req, resp);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CurrencyPair pair = getCurrencyPair(req, resp);
		if (pair == null) {
			return;
		}
		Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByCodes(pair.base(), pair.target());

		if (exchangeRate.isPresent()) {
			sendJson(resp, DTOMapper.toDTO(exchangeRate.get()));
		} else {
			sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Exchange rate for the pair not found");
		}
	}

	protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CurrencyPair pair = getCurrencyPair(req, resp);
		if (pair == null) {
			return;
		}

		Map<String, String> params = parseFormBody(req);
		String rateValue = params.get("rate");

		if (rateValue == null || rateValue.isBlank()) {
			sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing form parameter: rate");
			return;
		}

		BigDecimal rate = new BigDecimal(rateValue);
		Optional<ExchangeRate> updatedRate = exchangeRateDAO.updateByCodes(pair.base(), pair.target(), rate);

		if (updatedRate.isEmpty()) {
			sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Exchange rate does not exist");
			return;
		}

		sendJson(resp, DTOMapper.toDTO(updatedRate.get()));

	}

	private CurrencyPair getCurrencyPair(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.replace("/", "").length() != 6) {
			sendError(resp, 400, "Currency codes are missing or invalid (expected format: USDEUR)");
			return null;
		}
		String codes = pathInfo.replace("/", "").toUpperCase();
		return new CurrencyPair(codes.substring(0, 3), codes.substring(3, 6));
	}

}

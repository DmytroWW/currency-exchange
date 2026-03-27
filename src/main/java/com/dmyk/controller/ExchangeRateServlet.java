package com.dmyk.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.model.ExchangeRate;
import com.dmyk.utils.DataSource;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Gson gson = new Gson();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getMethod();
		if (method.equals("PATCH")) {
			doPatch(req, resp);
		} else {
			super.service(req, resp);
		}
	}

	protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null || pathInfo.length() != 7) {
			sendError(resp, 400, "Коди валют вказані невірно");
			return;

		}
		String codes = pathInfo.replace("/", "").toUpperCase();
		String baseCode = codes.substring(0, 3);
		String targetCode = codes.substring(3, 6);

		Map<String, String> params = parseFormBody(req);
		String rateValue = params.get("rate");

		if (rateValue == null) {
			sendError(resp, 400, "Missing parameter: rate");
			return;
		}

		try (Connection connection = DataSource.getConnection()) {
			BigDecimal rate = new BigDecimal(rateValue);
			ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO(connection);
			ExchangeRate updatedRate = exchangeRateDAO.updateByCodes(baseCode, targetCode, rate);
			if (updatedRate == null) {
				sendError(resp, 404, "ExchangeRate not exist in database");
				return;
			}
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			String exchangeRateInJson = gson.toJson(updatedRate);
			resp.getWriter().write(exchangeRateInJson);

		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			sendError(resp, 400, "Некоректне значення rate");
		} catch (RuntimeException e) {
			sendError(resp, 500, "Помилка бази даних: " + e.getMessage());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.getWriter().write("{\"message\": \"" + message + "\"}");
	}

	private Map<String, String> parseFormBody(HttpServletRequest req) throws IOException {
		String body = req.getReader().lines().collect(Collectors.joining());
		Map<String, String> params = new HashMap<>();

		if (body == null || body.isBlank()) {
			return params;
		}

		String[] pairs = body.split("&");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue.length == 2) {
				params.put(keyValue[0], keyValue[1]);
			}
		}
		return params;
	}
}

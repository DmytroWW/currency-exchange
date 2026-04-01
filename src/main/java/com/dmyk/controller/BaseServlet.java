package com.dmyk.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.dmyk.exception.DatabaseException;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected final Gson gson = new Gson();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			super.service(req, resp);
		} catch (DatabaseException e) {

			if (e.getMessage().contains("already exists")) {
				sendError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
			} else {
				sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} catch (Exception e) {
			// Всі інші непередбачувані помилки
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(gson.toJson(Map.of("message", message)));
	}

	// 2. Для звичайних успішних відповідей (200 OK) - наприклад, список валют
	protected void sendJson(HttpServletResponse resp, Object data) throws IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(gson.toJson(data));
	}

	// 3. Спеціально для POST запитів (201 Created) - те, що ти просив
	protected void sendCreatedJson(HttpServletResponse resp, Object data) throws IOException {
		resp.setStatus(HttpServletResponse.SC_CREATED); // Ставить 201
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(gson.toJson(data));
	}

	protected Map<String, String> parseFormBody(HttpServletRequest req) throws IOException {

		String body = req.getReader().lines().collect(Collectors.joining());
		Map<String, String> params = new HashMap<>();

		if (body.isEmpty()) {
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

	protected boolean areParametersValid(HttpServletRequest req, String... parameterNames) {
		for (String name : parameterNames) {
			String value = req.getParameter(name);
			if (value == null || value.isBlank()) {
				return false;
			}
		}
		return true;
	}

}

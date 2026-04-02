package com.dmyk.controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/error-handler", name = "ErrorHandlerServlet")
public class ErrorHandlerServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String message = (String) req.getAttribute("jakarta.servlet.error.message");
		if (message == null || message.isEmpty()) {
			message = "An unexpected error occurred on the server side";
		}

		sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
	}
}
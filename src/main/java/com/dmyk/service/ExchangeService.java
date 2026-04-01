package com.dmyk.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.dmyk.dao.ExchangeRateDAO;
import com.dmyk.dto.ExchangeResponseDTO;
import com.dmyk.model.ExchangeRate;

public class ExchangeService {
	private final ExchangeRateDAO exchangeRateDAO;

	public ExchangeService(ExchangeRateDAO exchangeRateDAO) {
		this.exchangeRateDAO = exchangeRateDAO;
	}

	public ExchangeResponseDTO convert(String from, String to, BigDecimal amount) {
		ExchangeRate rateModel = getRate(from, to);

		if (rateModel == null) {
			return null;
		}

		BigDecimal convertedAmount = amount.multiply(rateModel.getRate()).setScale(2, RoundingMode.HALF_UP);

		return new ExchangeResponseDTO(DTOMapper.toDTO(rateModel.getBaseCurrency()),
				DTOMapper.toDTO(rateModel.getTargetCurrency()), rateModel.getRate(), amount, convertedAmount);
	}

	private ExchangeRate getRate(String from, String to) {
		// Сценарій 1: Прямий курс
		ExchangeRate directRate = exchangeRateDAO.findByCodes(from, to).orElse(null);
		if (directRate != null) {
			return directRate;
		}

		// Сценарій 2: Зворотний курс (1 / rate)
		ExchangeRate reverseRate = exchangeRateDAO.findByCodes(to, from).orElse(null);
		if (reverseRate != null) {
			BigDecimal calculatedRate = BigDecimal.ONE.divide(reverseRate.getRate(), 6, RoundingMode.HALF_UP);
			return new ExchangeRate(0, reverseRate.getTargetCurrency(), reverseRate.getBaseCurrency(), calculatedRate);
		}

		// Сценарій 3: Крос-курс через USD
		ExchangeRate usdToBase = exchangeRateDAO.findByCodes("USD", from).orElse(null);
		ExchangeRate usdToTarget = exchangeRateDAO.findByCodes("USD", to).orElse(null);

		if (usdToBase != null && usdToTarget != null) {
			BigDecimal calculatedRate = usdToTarget.getRate().divide(usdToBase.getRate(), 6, RoundingMode.HALF_UP);
			return new ExchangeRate(0, usdToBase.getTargetCurrency(), usdToTarget.getTargetCurrency(), calculatedRate);
		}

		return null;
	}
}

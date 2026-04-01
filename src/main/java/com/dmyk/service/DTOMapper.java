package com.dmyk.service;

import com.dmyk.dto.CurrencyDTO;
import com.dmyk.dto.ExchangeRateDTO;
import com.dmyk.model.Currency;
import com.dmyk.model.ExchangeRate;

public class DTOMapper {

	public static CurrencyDTO toDTO(Currency currency) {
		if (currency == null) {
			return null;
		}
		return new CurrencyDTO(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
	}

	public static ExchangeRateDTO toDTO(ExchangeRate rate) {

		return new ExchangeRateDTO(rate.getId(), toDTO(rate.getBaseCurrency()), toDTO(rate.getTargetCurrency()),
				rate.getRate());
	}

}

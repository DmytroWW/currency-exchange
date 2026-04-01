package com.dmyk.dto;

import java.math.BigDecimal;

public class ExchangeRateDTO {
	private int id;
	private CurrencyDTO baseCurrency;
	private CurrencyDTO targetCurrency;
	private BigDecimal rate;

	public ExchangeRateDTO(int id, CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal rate) {
		super();
		this.id = id;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CurrencyDTO getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(CurrencyDTO baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public CurrencyDTO getTargetCurrency() {
		return targetCurrency;
	}

	public void setTargetCurrency(CurrencyDTO targetCurrency) {
		this.targetCurrency = targetCurrency;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

}

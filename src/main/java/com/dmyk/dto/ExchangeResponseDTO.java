package com.dmyk.dto;

import java.math.BigDecimal;

public class ExchangeResponseDTO {
	private CurrencyDTO baseCurrency;
	private CurrencyDTO targetCurrency;
	private BigDecimal rate;
	private BigDecimal amount;
	private BigDecimal convertedAmount;

	public ExchangeResponseDTO(CurrencyDTO base, CurrencyDTO target, BigDecimal rate, BigDecimal amount,
			BigDecimal convertedAmount) {
		this.baseCurrency = base;
		this.targetCurrency = target;
		this.rate = rate;
		this.amount = amount;
		this.convertedAmount = convertedAmount;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getConvertedAmount() {
		return convertedAmount;
	}

	public void setConvertedAmount(BigDecimal convertedAmount) {
		this.convertedAmount = convertedAmount;
	}

}

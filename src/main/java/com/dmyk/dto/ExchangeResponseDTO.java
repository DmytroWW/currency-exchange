package com.dmyk.dto;

import java.math.BigDecimal;

import com.dmyk.model.Currency;

public class ExchangeResponseDTO {
	private Currency baseCurrency;
	private Currency targetCurrency;
	private BigDecimal rate;
	private BigDecimal amount;
	private BigDecimal convertedAmount;

	public ExchangeResponseDTO(Currency base, Currency target, BigDecimal rate, BigDecimal amount,
			BigDecimal converted) {
		this.baseCurrency = base;
		this.targetCurrency = target;
		this.rate = rate;
		this.amount = amount;
		this.convertedAmount = converted;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Currency getTargetCurrency() {
		return targetCurrency;
	}

	public void setTargetCurrency(Currency targetCurrency) {
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

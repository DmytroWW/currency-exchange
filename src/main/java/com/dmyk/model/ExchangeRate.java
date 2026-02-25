package com.dmyk.model;

import java.math.BigDecimal;

import com.dmyk.utils.DataTransferObject;

public class ExchangeRate implements DataTransferObject {

	private int id;
	private Currency baseCurrency;
	private Currency targetCurrency;
	private BigDecimal rate;

	public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
		super();
		this.id = id;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
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

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String toString() {
		return "ExchangeRate [id=" + id + ", baseCurrency=" + baseCurrency + ", targetCurrency=" + targetCurrency
				+ ", rate=" + rate + "]";
	}

}

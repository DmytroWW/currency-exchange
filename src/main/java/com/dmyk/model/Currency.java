package com.dmyk.model;

import com.dmyk.utils.DataTransferObject;
import com.google.gson.annotations.SerializedName;

public class Currency implements DataTransferObject {

	private int id;
	@SerializedName("name")
	private String fullName;
	private String code;
	private String sign;

	public Currency(int id, String code, String fullName, String sign) {
		super();
		this.id = id;
		this.code = code;
		this.fullName = fullName;
		this.sign = sign;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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
		return "Currency [id=" + id + ", code=" + code + ", fullName=" + fullName + ", sign=" + sign + "]";
	}

}

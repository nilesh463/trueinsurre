package com.trueinsurre.dto;

public class CsvValidate {

	private String invoiceDate;
	private String currency;
	private String billedTo;
	private String clientEmail;

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBilledTo() {
		return billedTo;
	}

	public void setBilledTo(String billedTo) {
		this.billedTo = billedTo;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	@Override
	public String toString() {
		return "CsvValidate [invoiceDate=" + invoiceDate + ", currency=" + currency + ", billedTo=" + billedTo
				+ ", clientEmail=" + clientEmail + "]";
	}

}

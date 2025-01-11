package com.trueinsurre.dto;

import java.util.List;

public class CsvValidateResponce {

	private long csvCount;
	private boolean uploaded;
	private long duplicateCount;
	private boolean dataValidated;
	private List<CsvValidate> csvValidate;
	private List<CsvValidate> duplicateData;

	public long getCsvCount() {
		return csvCount;
	}

	public void setCsvCount(long csvCount) {
		this.csvCount = csvCount;
	}

	public boolean isDataValidated() {
		return dataValidated;
	}

	public void setDataValidated(boolean dataValidated) {
		this.dataValidated = dataValidated;
	}

	public List<CsvValidate> getCsvValidate() {
		return csvValidate;
	}

	public void setCsvValidate(List<CsvValidate> csvValidate) {
		this.csvValidate = csvValidate;
	}

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	public List<CsvValidate> getDuplicateData() {
		return duplicateData;
	}

	public void setDuplicateData(List<CsvValidate> duplicateData) {
		this.duplicateData = duplicateData;
	}

	public long getDuplicateCount() {
		return duplicateCount;
	}

	public void setDuplicateCount(long duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	@Override
	public String toString() {
		return "CsvValidateResponce [csvCount=" + csvCount + ", uploaded=" + uploaded + ", duplicateCount="
				+ duplicateCount + ", dataValidated=" + dataValidated + ", csvValidate=" + csvValidate
				+ ", duplicateData=" + duplicateData + "]";
	}
	
}

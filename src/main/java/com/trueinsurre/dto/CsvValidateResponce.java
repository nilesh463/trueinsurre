package com.trueinsurre.dto;

import java.util.List;

public class CsvValidateResponce {

	private long csvCount;
	private boolean uploaded;
	private long duplicateCount;
	private boolean dataValidated;
	private List<TaskDto> csvValidate;
	private List<TaskDto> duplicateData;

	public long getCsvCount() {
		return csvCount;
	}

	public void setCsvCount(long csvCount) {
		this.csvCount = csvCount;
	}

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	public long getDuplicateCount() {
		return duplicateCount;
	}

	public void setDuplicateCount(long duplicateCount) {
		this.duplicateCount = duplicateCount;
	}

	public boolean isDataValidated() {
		return dataValidated;
	}

	public void setDataValidated(boolean dataValidated) {
		this.dataValidated = dataValidated;
	}

	public List<TaskDto> getCsvValidate() {
		return csvValidate;
	}

	public void setCsvValidate(List<TaskDto> csvValidate) {
		this.csvValidate = csvValidate;
	}

	public List<TaskDto> getDuplicateData() {
		return duplicateData;
	}

	public void setDuplicateData(List<TaskDto> duplicateData) {
		this.duplicateData = duplicateData;
	}

	@Override
	public String toString() {
		return "CsvValidateResponce [csvCount=" + csvCount + ", uploaded=" + uploaded + ", duplicateCount="
				+ duplicateCount + ", dataValidated=" + dataValidated + ", csvValidate=" + csvValidate
				+ ", duplicateData=" + duplicateData + "]";
	}

}

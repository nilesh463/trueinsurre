package com.trueinsurre.dto;

import java.time.LocalDate;
import java.util.List;

public class TaskHistoryDto {

	private LocalDate date;
	private List<UpdatedFieldDto> updatedFields;

	public TaskHistoryDto(LocalDate date, List<UpdatedFieldDto> updatedFields) {
		super();
		this.date = date;
		this.updatedFields = updatedFields;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<UpdatedFieldDto> getUpdatedFields() {
		return updatedFields;
	}

	public void setUpdatedFields(List<UpdatedFieldDto> updatedFields) {
		this.updatedFields = updatedFields;
	}

	@Override
	public String toString() {
		return "TaskHistoryDto [date=" + date + ", updatedFields=" + updatedFields + "]";
	}

}

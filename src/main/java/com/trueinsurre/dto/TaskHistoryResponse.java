package com.trueinsurre.dto;

import java.util.List;

public class TaskHistoryResponse {
	private Long taskId;
	private List<TaskHistoryDto> taskHistory;

	public TaskHistoryResponse(Long taskId, List<TaskHistoryDto> taskHistory) {
		super();
		this.taskId = taskId;
		this.taskHistory = taskHistory;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public List<TaskHistoryDto> getTaskHistory() {
		return taskHistory;
	}

	public void setTaskHistory(List<TaskHistoryDto> taskHistory) {
		this.taskHistory = taskHistory;
	}

	@Override
	public String toString() {
		return "TaskHistoryResponse [taskId=" + taskId + ", taskHistory=" + taskHistory + "]";
	}

}

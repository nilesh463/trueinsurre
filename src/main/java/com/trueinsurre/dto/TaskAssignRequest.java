package com.trueinsurre.dto;

import java.util.List;

public class TaskAssignRequest {

	private List<Long> taskList;
	private List<Long> userList;

	public List<Long> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Long> taskList) {
		this.taskList = taskList;
	}

	public List<Long> getUserList() {
		return userList;
	}

	public void setUserList(List<Long> userList) {
		this.userList = userList;
	}

	@Override
	public String toString() {
		return "TaskAssignRequest [taskList=" + taskList + ", userList=" + userList + "]";
	}

}

package com.trueinsurre.service;

import org.springframework.stereotype.Service;

import com.trueinsurre.dto.TaskHistoryResponse;
import com.trueinsurre.modal.Task;

@Service
public interface TaskHistoryService {

	void createHistory(String fieldName, String oldValue, String newValue, Task task);

	TaskHistoryResponse getTaskHistory(Long taskId);
	

}

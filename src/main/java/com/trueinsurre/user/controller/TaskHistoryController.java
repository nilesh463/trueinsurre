package com.trueinsurre.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trueinsurre.dto.TaskHistoryResponse;
import com.trueinsurre.service.TaskHistoryService;

@RequestMapping("/task-history")
@RestController
public class TaskHistoryController {
	
	@Autowired
	TaskHistoryService taskHistoryService;
	
	@GetMapping("/{taskId}")
	public ResponseEntity<TaskHistoryResponse> getAllTasks(@PathVariable Long taskId) {


		return ResponseEntity.ok(taskHistoryService.getTaskHistory(taskId));
	}

}

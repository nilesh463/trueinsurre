package com.trueinsurre.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.trueinsurre.dto.CsvValidateResponce;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.TaskAssignRequest;
import com.trueinsurre.dto.TaskDto;
import com.trueinsurre.service.TaskService;
@RequestMapping("/task")
@RestController
public class TaskController {
	
	@Autowired
	TaskService taskService;
	
	@PostMapping("/upload")
	public ResponseEntity<CsvValidateResponce> taskUpload(@RequestParam(required = false) Long userId, @RequestParam("file") MultipartFile file) throws ParseException {
		
		return ResponseEntity.ok(taskService.saveCsvData(file, userId));
	}
	
	@GetMapping("/all-active")
	public ResponseEntity<Map<String, Object>> getAllActiveTask(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<TaskDto> taskPage = taskService.getAllTaskByIsAssignAndByIsCompletedANdByIsDeleted(false, false, false,
				page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", taskPage.getContent());
		response.put("currentPage", taskPage.getNumber());
		response.put("totalPages", taskPage.getTotalPages());
		response.put("totalElements", taskPage.getTotalElements());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/all")
	public ResponseEntity<Map<String, Object>> getAllTasks(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<TaskDto> taskPage = taskService.getAllTaskByIsAssignAndByIsCompletedANdByIsDeleted(false, false, false,
				page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", taskPage.getContent());
		response.put("currentPage", taskPage.getNumber());
		response.put("totalPages", taskPage.getTotalPages());
		response.put("totalElements", taskPage.getTotalElements());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/all-completed")
	public ResponseEntity<Map<String, Object>> getAllCompletedTask(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<TaskDto> taskPage = taskService.getAllTaskByIsAssignAndByIsCompletedANdByIsDeleted(false, true, false,
				page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", taskPage.getContent());
		response.put("currentPage", taskPage.getNumber());
		response.put("totalPages", taskPage.getTotalPages());
		response.put("totalElements", taskPage.getTotalElements());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/emp-all/{userId}")
	public ResponseEntity<Map<String, Object>> getAllTaskByUser(@PathVariable Long userId,@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<TaskDto> taskPage = taskService.getAllTaskByUserAndByIsAssignANdByIsDeleted(userId, true, false, page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", taskPage.getContent());
		response.put("currentPage", taskPage.getNumber());
		response.put("totalPages", taskPage.getTotalPages());
		response.put("totalElements", taskPage.getTotalElements());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/emp-active/{userId}")
	public ResponseEntity<Map<String, Object>> getTaskByUser(@PathVariable Long userId,@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<TaskDto> taskPage = taskService.getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeleted(userId, true, false, false, page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", taskPage.getContent());
		response.put("currentPage", taskPage.getNumber());
		response.put("totalPages", taskPage.getTotalPages());
		response.put("totalElements", taskPage.getTotalElements());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/emp-completed/{userId}")
	public ResponseEntity<Map<String, Object>> getCompletedTaskByUser(@PathVariable Long userId,@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<TaskDto> taskPage = taskService.getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeleted(userId, true, true, false, page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("tasks", taskPage.getContent());
		response.put("currentPage", taskPage.getNumber());
		response.put("totalPages", taskPage.getTotalPages());
		response.put("totalElements", taskPage.getTotalElements());

		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/assign")
	public ResponseEntity<Responce> taskAssign(@RequestBody TaskAssignRequest request){
		Responce response = new Responce();
		List<Long> taskList = request.getTaskList();
	    List<Long> userList = request.getUserList();
		taskService.taskAssign(taskList,userList);
		response.setStatus(200L);
		response.setMessage("Selected task is assign to the selected employee.");
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/de-assign")
	public ResponseEntity<Responce> taskDeAssign(@RequestBody TaskAssignRequest request){
		Responce response = new Responce();
		List<Long> taskList = request.getTaskList();
//	    List<Long> userList = request.getUserList();
		taskService.taskDeAssign(taskList);
		response.setStatus(200L);
		response.setMessage("Selected task is assign to the admin employee.");
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/mark-all-completed")
	public ResponseEntity<Responce> markAllCompleted(@RequestBody TaskAssignRequest request){
		Responce response = new Responce();
	    List<Long> userList = request.getUserList();
		taskService.markAllCompleted(userList);
		response.setStatus(200L);
		response.setMessage("Selected task is assign to the selected employee.");
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/add")
	public ResponseEntity<Responce> addNewTask(@RequestBody TaskDto taskDto){
		Responce response = new Responce();
		taskService.addTask(taskDto);
		response.setStatus(200L);
		response.setMessage("New added.");
		return ResponseEntity.ok(response);
	}
	
}

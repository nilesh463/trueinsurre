package com.trueinsurre.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trueinsurre.dto.CsvValidateResponce;
import com.trueinsurre.dto.TaskDto;
import com.trueinsurre.modal.Task;

@Service
public interface TaskService {

	public Task addTask(Task task);
	public CsvValidateResponce saveCsvData(MultipartFile file, Long userId)throws ParseException;
	public Page<TaskDto> getAllTaskByIsAssignANdByIsDeleted(boolean isAssign, boolean isDeleted, int page, int size);
	public Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeleted(boolean isAssign, boolean isCompleted, boolean isDeleted, int page, int size);
	public Page<TaskDto> getAllTaskByUserAndByIsAssignANdByIsDeleted(Long userId, boolean isAssign, boolean isDeleted, int page, int size);
	public Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeleted(Long userId, boolean isAssign, boolean isCompleted, boolean isDeleted, int page, int size);
	public void taskAssign(List<Long> taskList, List<Long> userList);
	public void markAllCompleted(List<Long> taskList);
	void taskDeAssign(List<Long> taskList);
	public Task addTask(TaskDto taskDto);
	
}

package com.trueinsurre.service;

import java.io.Reader;
import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trueinsurre.dto.CsvValidateResponce;
import com.trueinsurre.dto.FilterDto;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.StatusDto;
import com.trueinsurre.dto.TaskDto;
import com.trueinsurre.modal.Task;

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
	public Task addTask(TaskDto taskDto)throws ParseException;
	public Responce statusUpdate(StatusDto statusDto);
	public Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeletedAndStatus(boolean b, boolean c, boolean d,
			String message, int page, int size);
	Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeletedAndDisposition(boolean isAssign,
			boolean isCompleted, boolean isDeleted, String message, int page, int size);
	Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeletedAndDispositionAndStatus(boolean isAssign,
			boolean isCompleted, boolean isDeleted, String disposition, String status, int page,
			int size);
	
	public Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeletedAndStatus(Long userId, boolean b, boolean c, boolean d,
			String message, int page, int size);
	Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeletedAndDisposition(Long userId, boolean isAssign,
			boolean isCompleted, boolean isDeleted, String message, int page, int size);
	Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeletedAndDispositionAndStatus(Long userId, boolean isAssign,
			boolean isCompleted, boolean isDeleted, String disposition, String status, int page,
			int size);
	TaskDto getEdit(Long id);
	public Responce updateCommentAndMessage(StatusDto statusDto);
	Page<TaskDto> getFilteredTasks(FilterDto filterDto, int page, int size);
	
}

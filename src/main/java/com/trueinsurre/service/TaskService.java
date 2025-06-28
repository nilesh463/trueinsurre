package com.trueinsurre.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
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
	public void taskAssign(List<Long> taskList, List<Long> userList);
	public void markAllCompleted(List<Long> taskList);
	void taskDeAssign(List<Long> taskList);
	public Task addTask(TaskDto taskDto)throws ParseException;
	public Responce statusUpdate(StatusDto statusDto);

	TaskDto getEdit(Long id);
	public Responce updateCommentAndMessage(StatusDto statusDto);
	Page<TaskDto> getFilteredTasks(FilterDto filterDto, int page, int size);
	byte[] generateExcel(FilterDto filterDto) throws IOException;
	
}

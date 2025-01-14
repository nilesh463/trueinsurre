package com.trueinsurre.serviceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trueinsurre.dto.CsvValidate;
import com.trueinsurre.dto.CsvValidateResponce;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.StatusDto;
import com.trueinsurre.dto.TaskDto;
import com.trueinsurre.exceptionHandeler.InvalidData;
import com.trueinsurre.exceptionHandeler.NotFound;
import com.trueinsurre.modal.Task;
import com.trueinsurre.modal.User;
import com.trueinsurre.repository.TaskRepository;
import com.trueinsurre.service.TaskService;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.utilityServices.DateUtility;

@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	TaskRepository taskRepository;
	@Autowired
	UserRepository userRepository;

	public static List<String> REQUIREDFIELDS = Arrays.asList("Vehicle Number", "Partner Number", "Agent Name",
			"Driver Name", "City", "	last year Policy issued by", "	Partner rate", "	New Expiry date", "Message",
			"Message link", "Status", "PolicyIssuedDate", "Message status", "Disposition", "Next Follow up Date",
			"comments");

	@Override
	public CsvValidateResponce saveCsvData(MultipartFile file, Long userId) throws ParseException {

		CsvValidateResponce responce = new CsvValidateResponce();
		List<CsvValidate> csvDataList = new ArrayList<>(); // To store the extracted data
		int dataRowCount = 0; // To count rows containing valid data
		int duplicateCount = 0;
		User user = null;
		Set<User> setUser = new HashSet<User>();
		if (Objects.nonNull(userId)) {
			user = userRepository.findById(userId).orElse(null);
			setUser.add(user);
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;
			String[] headers = null;
			boolean headerRow = true;
			responce.setDataValidated(true);

			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				Task task = new Task();
				if (headerRow) {
					headers = values;
					// Validate header row
					if (!Arrays.asList(headers).containsAll(REQUIREDFIELDS)) {
						responce.setDataValidated(false);
					}

					// Validate header sequence
					for (int i = 0; i < REQUIREDFIELDS.size(); i++) {
						if (i >= headers.length || !REQUIREDFIELDS.get(i).equals(headers[i])) {
							responce.setDataValidated(false);
						}
					}

					headerRow = false;
				} else {

					CsvValidate csvValidate = new CsvValidate();

					for (int i = 0; i < headers.length; i++) {
						if (i >= values.length) {
							continue; // Skip if the value is missing in the CSV
						}

						String header = headers[i];
						String value = values[i].trim(); // trim to avoid any leading/trailing spaces

						switch (header) {

						case "Vehicle Number":
							task.setVehicleNumber(value);
							break;
						case "Partner Number":
							task.setPartnerNumber(value);
							break;
						case "Agent Name":
							task.setAgentName(value);
							break;
						case "Driver Name":
							task.setDriverName(value);
							break;
						case "City":
							task.setCity(value);
							break;
						case "last year Policy issued by":
							task.setLastYearPolicyIssuedBy(value);
							break;
						case "Partner rate":
							task.setPartnerRate(value);
							break;
						case "New Expiry date":
							task.setNewExpiryDate(value);
							break;
						case "Message":
							task.setMessage(value);
							break;
						case "Message link":
							task.setMessageLink(value);
							break;
						case "Status":
							task.setStatus(value);
							break;
						case "PolicyIssuedDate":
							task.setPolicyIssuedDate(value);
							break;
						case "Message status":
							task.setMessageStatus(value);
							break;
						case "Disposition":
							task.setDisposition(value);
							break;
						case "Next Follow up Date":
							task.setNextFollowUpDate(value);
							break;
						case "comments":
							task.setComments(value);
							break;
						default:
							// Ignore other columns
							break;
						}
					}

					if (user != null) {
						task.setUsers(setUser);
						task.setAssign(true);
					}

					task = addTask(task);
				}
			}

			responce.setDuplicateCount(duplicateCount);
			responce.setCsvValidate(csvDataList);
			responce.setCsvCount(dataRowCount);
			responce.setUploaded(true);
			return responce;
		} catch (IOException e) {
			throw new InvalidData("Error reading CSV file.", e);
		}
	}

	@Override
	public Task addTask(Task task) {
		task.setCompleted(false);
		task.setDeleted(false);
		task = taskRepository.save(task);
		return task;
	}

	@Override
	public Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeleted(boolean isAssign, boolean isCompleted,
			boolean isDeleted, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByIsAssignAndIsCompletedAndIsDeleted(isAssign, isCompleted, isDeleted,
				pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public Page<TaskDto> getAllTaskByIsAssignANdByIsDeleted(boolean isAssign, boolean isDeleted, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByIsAssignAndIsDeleted(isAssign, isDeleted, pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public Page<TaskDto> getAllTaskByUserAndByIsAssignANdByIsDeleted(Long userId, boolean isAssign, boolean isDeleted,
			int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByUsers_IdAndIsAssignAndIsDeleted(userId, isAssign, isDeleted,
				pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeleted(Long userId, boolean isAssign,
			boolean isCompleted, boolean isDeleted, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByUsers_IdAndIsAssignAndIsCompletedAndIsDeleted(userId, isAssign,
				isCompleted, isDeleted, pageable);

		return taskPage.map(this::convertToDto);
	}
	
	@Override
	public TaskDto getEdit(Long id) {
		Task task = taskRepository.findById(id).orElseThrow(()-> new NotFound("No Task Found By id: "+id));
		TaskDto taskdto = convertToDto(task);
		try {
			taskdto.setNewExpiryDate(DateUtility.convertToDDMMYYYY(taskdto.getNewExpiryDate()));
			taskdto.setNextFollowUpDate(DateUtility.convertToDDMMYYYY(taskdto.getNextFollowUpDate()));
			taskdto.setPolicyIssuedDate(DateUtility.convertToDDMMYYYY(taskdto.getPolicyIssuedDate()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return taskdto;
	}

	private TaskDto convertToDto(Task task) {
		TaskDto taskDto = new TaskDto();

		// Map all fields
		taskDto.setId(task.getId());
		taskDto.setVehicleNumber(task.getVehicleNumber());
		taskDto.setPartnerNumber(task.getPartnerNumber());
		taskDto.setAgentName(task.getAgentName());
		taskDto.setDriverName(task.getDriverName());
		taskDto.setCity(task.getCity());
		taskDto.setLastYearPolicyIssuedBy(task.getLastYearPolicyIssuedBy());
		taskDto.setPartnerRate(task.getPartnerRate());
		taskDto.setNewExpiryDate(task.getNewExpiryDate());
		taskDto.setMessage(task.getMessage());
		taskDto.setMessageLink(task.getMessageLink());
		taskDto.setPolicyIssuedDate(task.getPolicyIssuedDate());
		taskDto.setMessageStatus(task.getMessageStatus());
		taskDto.setDisposition(task.getDisposition());
		taskDto.setNextFollowUpDate(task.getNextFollowUpDate());
		taskDto.setComments(task.getComments());

		// Map boolean fields
		taskDto.setAssign(task.isAssign());
		taskDto.setCompleted(task.isCompleted());
		taskDto.setDeleted(task.isDeleted());

		// Map status
		taskDto.setStatus(task.getStatus());

		return taskDto;
	}

	@Override
	public Task addTask(TaskDto taskDto) throws ParseException {
		Task taskObj = new Task();

		if (Objects.nonNull(taskDto.getId())) {
			Set<User> userSet = new HashSet<User>();
			User user = userRepository.findById(taskDto.getId()).orElse(null);
			userSet.add(user);
			taskObj.setUsers(userSet);
		}

		// Map all fields
		taskObj.setId(taskDto.getId());
		taskObj.setVehicleNumber(taskDto.getVehicleNumber());
		taskObj.setPartnerNumber(taskDto.getPartnerNumber());
		taskObj.setAgentName(taskDto.getAgentName());
		taskObj.setDriverName(taskDto.getDriverName());
		taskObj.setCity(taskDto.getCity());
		taskObj.setLastYearPolicyIssuedBy(taskDto.getLastYearPolicyIssuedBy());
		taskObj.setPartnerRate(taskDto.getPartnerRate());
		taskObj.setNewExpiryDate(DateUtility.convertToMMDDYYYY(taskDto.getNewExpiryDate()));
		taskObj.setMessage(taskDto.getMessage());
		taskObj.setMessageLink(taskDto.getMessageLink());
		taskObj.setPolicyIssuedDate(DateUtility.convertToMMDDYYYY(taskDto.getPolicyIssuedDate()));
		taskObj.setMessageStatus(taskDto.getMessageStatus());
		taskObj.setDisposition(taskDto.getDisposition());
		taskObj.setNextFollowUpDate(DateUtility.convertToMMDDYYYY(taskDto.getNextFollowUpDate()));
		taskObj.setComments(taskDto.getComments());

		// Map boolean fields
		taskObj.setAssign(taskDto.isAssign());
		taskObj.setCompleted(taskDto.isCompleted());
		taskObj.setDeleted(taskDto.isDeleted());

		// Map status
		taskObj.setStatus(taskDto.getStatus());

		taskObj = taskRepository.save(taskObj);

		return taskObj;
	}

	@Override
	public void taskAssign(List<Long> taskList, List<Long> userList) {
		if (Objects.nonNull(userList) && userList.get(0) > 0) {
			
			List<Task> list = new ArrayList<Task>();
			Task task = null;
			Set<User> userSet = new HashSet<User>();
			for (Long userId : userList) {
				User user = userRepository.findByIdAndStatusFalse(userId);
				userSet.add(user);
			}

			for (Long taskId : taskList) {
				task = taskRepository.findById(taskId).orElse(null);
				if (Objects.nonNull(task)) {
					task.setAssign(true);
					task.setUsers(userSet);
					list.add(task);
				}
			}
			taskRepository.saveAll(list);
		} else {
			
			taskDeAssign(taskList);
		}
	}

	@Override
	public void taskDeAssign(List<Long> taskList) {
		List<Task> list = new ArrayList<Task>();
		Task task = null;
		for (Long taskId : taskList) {
			task = taskRepository.findById(taskId).orElse(null);
			if (Objects.nonNull(task)) {
				task.setAssign(false);
				task.setUsers(null);
				list.add(task);
			}
		}
		taskRepository.saveAll(list);
	}

	@Override
	public void markAllCompleted(List<Long> taskList) {
		// TODO Auto-generated method stub
		List<Task> list = new ArrayList<Task>();
		Task task = null;
		for (Long taskId : taskList) {
			task = taskRepository.findById(taskId).orElse(null);
			if (Objects.nonNull(task)) {
				task.setCompleted(true);
				list.add(task);
			}
		}
		taskRepository.saveAll(list);
	}

	@Override
	public Responce statusUpdate(StatusDto statusDto) {
		Responce response = new Responce();
		response.setStatus(200L);
		response.setMessage(statusDto.getValidateKey()+"  status updated.");
		
		Task task = taskRepository.findById(statusDto.getId()).orElse(null);
		
		if(statusDto.getValidateKey().equalsIgnoreCase("Status")) {
			task.setStatus(statusDto.getMessage());
		} else if(statusDto.getValidateKey().equalsIgnoreCase("MessageStatus")) {
			task.setMessageStatus(statusDto.getMessage());
		} else if(statusDto.getValidateKey().equalsIgnoreCase("Disposition")) {
			task.setDisposition(statusDto.getMessage());
		}
		taskRepository.save(task);
		return response;
	}

	@Override
	public Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeletedAndStatus(boolean isAssign, boolean isCompleted, boolean isDeleted,
			String message, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByIsAssignAndIsCompletedAndIsDeletedAndStatus(isAssign,
				isCompleted, isDeleted, message, pageable);

		return taskPage.map(this::convertToDto);
	}
	
	@Override
	public Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeletedAndDisposition(boolean isAssign, boolean isCompleted, boolean isDeleted,
			String message, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByIsAssignAndIsCompletedAndIsDeletedAndDisposition(isAssign,
				isCompleted, isDeleted, message, pageable);

		return taskPage.map(this::convertToDto);
	}
	
	@Override
	public Page<TaskDto> getAllTaskByIsAssignAndByIsCompletedANdByIsDeletedAndDispositionAndStatus(boolean isAssign, boolean isCompleted, boolean isDeleted,
			String disposition, String status, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByIsAssignAndIsCompletedAndIsDeletedAndDispositionAndStatus(isAssign,
				isCompleted, isDeleted, disposition, status, pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeletedAndStatus(Long userId, boolean isAssign, boolean isCompleted, boolean isDeleted,
			String message, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByUsers_IdAndIsAssignAndIsCompletedAndIsDeletedAndStatus(userId, isAssign,
				isCompleted, isDeleted, message, pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeletedAndDisposition(Long userId,boolean isAssign, boolean isCompleted, boolean isDeleted,
			String message, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByUsers_IdAndIsAssignAndIsCompletedAndIsDeletedAndDisposition(userId, isAssign,
				isCompleted, isDeleted, message, pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public Page<TaskDto> getAllTaskByUserAndByIsAssignAndByIsCompletedANdByIsDeletedAndDispositionAndStatus(Long userId,
			boolean isAssign, boolean isCompleted, boolean isDeleted,
			String disposition, String status, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findByUsers_IdAndIsAssignAndIsCompletedAndIsDeletedAndDispositionAndStatus(userId, isAssign,
				isCompleted, isDeleted, disposition, status, pageable);

		return taskPage.map(this::convertToDto);
	}

}

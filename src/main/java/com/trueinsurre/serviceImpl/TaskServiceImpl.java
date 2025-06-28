package com.trueinsurre.serviceImpl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trueinsurre.dto.CsvValidate;
import com.trueinsurre.dto.CsvValidateResponce;
import com.trueinsurre.dto.FilterDto;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.StatusDto;
import com.trueinsurre.dto.TaskDto;
import com.trueinsurre.exceptionHandeler.InvalidData;
import com.trueinsurre.exceptionHandeler.NotFound;
import com.trueinsurre.modal.Task;
import com.trueinsurre.modal.User;
import com.trueinsurre.repository.TaskRepository;
import com.trueinsurre.service.TaskHistoryService;
import com.trueinsurre.service.TaskService;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.utilityServices.DateUtility;

@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	TaskRepository taskRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	TaskHistoryService taskHistoryService;
	


	public static List<String> REQUIREDFIELDS = Arrays.asList("Vehicle Number", "Partner Number", "Agent Name",
			"Driver Name", "City", "last year Policy issued by", "Partner rate", "New Expiry date", "Message",
			"Message link", "Status", "PolicyIssuedDate", "Message status", "Disposition", "Next Follow up Date",
			"comments");

	@Override
	public CsvValidateResponce saveCsvData(MultipartFile file, Long userId) {
		CsvValidateResponce response = new CsvValidateResponce();
		List<TaskDto> csvDataList = new ArrayList<>();
		List<TaskDto> duplicateTasks = new ArrayList<>();
		Set<String> vehicleNumbers = new HashSet<>();
		int dataRowCount = 0;
		int duplicateCount = 0;

		User user = null;
		Set<User> setUser = new HashSet<>();
		if (Objects.nonNull(userId)) {
			user = userRepository.findById(userId).orElse(null);
			setUser.add(user);
		}

		List<Task> taskForDb = new ArrayList<>();

		try (BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

			for (CSVRecord record : csvParser) {
				Task task = new Task();

				task.setVehicleNumber(record.get("Vehicle Number"));
				task.setPartnerNumber(record.get("Partner Number"));
				task.setAgentName(record.get("Agent Name"));
				task.setDriverName(record.get("Driver Name"));
				task.setCity(record.get("City"));
				task.setLastYearPolicyIssuedBy(record.get("last year Policy issued by"));
				task.setPartnerRate(record.get("Partner rate"));
				task.setNewExpiryDate(parseDateString(record.get("New Expiry date")));
				task.setMessage(removeLeadingQuote(record.get("Message")));
				task.setMessageLink(record.get("Message link"));
				task.setStatus(record.get("Status"));
				task.setPolicyIssuedDate(parseDateString(record.get("PolicyIssuedDate")));
				task.setMessageStatus(record.get("Message status"));
				task.setDisposition(record.get("Disposition"));
				task.setNextFollowUpDate(parseDateString(record.get("Next Follow up Date")));
				task.setComments(record.get("comments"));

				if (!vehicleNumbers.add(task.getVehicleNumber())) {
					duplicateCount++;
					duplicateTasks.add(convertToDto(task));
				} else {
					if (taskRepository.existsByVehicleNumber(task.getVehicleNumber())) {
						duplicateCount++;
						duplicateTasks.add(convertToDto(task));
					} else {
						if (user != null) {
							task.setUsers(setUser);
							task.setAssign(true);
						}
						task.setCompleted(false);
						task.setDeleted(false);
						taskForDb.add(task);
						csvDataList.add(convertToDto(task));
						dataRowCount++;
					}
				}
			}

			taskRepository.saveAll(taskForDb);

		} catch (Exception e) {
			e.printStackTrace();
			response.setUploaded(false);
			return response;
		}

		response.setDuplicateCount(duplicateCount);
		response.setCsvValidate(csvDataList);
		response.setDuplicateData(duplicateTasks);
		response.setCsvCount(dataRowCount);
		response.setUploaded(true);
		return response;
	}

	public static String removeQuotedText(String text) {
		if (text.startsWith("\"") && text.endsWith("\"")) {
			return text.substring(1, text.length() - 1);
		}
		return text;
	}

//	@Override
	public CsvValidateResponce saveCsvDatas(MultipartFile file, Long userId) throws ParseException {

		CsvValidateResponce responce = new CsvValidateResponce();
		List<TaskDto> csvDataList = new ArrayList<>();
		int dataRowCount = 0;
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
							value = "";
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
							task.setNewExpiryDate(parseDateString(value));
							break;
						case "Message":
							task.setMessage(removeLeadingQuote(value));
							break;
						case "Message link":
							task.setMessageLink(value);
							break;
						case "Status":
							task.setStatus(value);
							break;
						case "PolicyIssuedDate":
							task.setPolicyIssuedDate(parseDateString(value));
							break;
						case "Message status":
							task.setMessageStatus(value);
							break;
						case "Disposition":
							task.setDisposition(value);
							break;
						case "Next Follow up Date":
							task.setNextFollowUpDate(parseDateString(value));
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
					task.setMessage(task.getMessage() + task.getMessageLink());
					task = addTask(task);
				}
			}

			responce.setDuplicateCount(duplicateCount);
			// responce.setCsvValidate(csvDataList);
			responce.setCsvCount(dataRowCount);
			responce.setUploaded(true);
			return responce;
		} catch (IOException e) {
			throw new InvalidData("Error reading CSV file.", e);
		}
	}

	@Override
	public Task addTask(Task task) {
		if (task.getVehicleNumber() != null) {
			task.setCompleted(false);
			task.setDeleted(false);
			task = taskRepository.save(task);
		}
		return task;
	}

	public static String removeLeadingQuote(String input) {
		if (input != null && input.startsWith("\"")) {
			return input.substring(1);
		}
		return input;
	}

	@Override
	public TaskDto getEdit(Long id) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new NotFound("No Task Found By id: " + id));
		TaskDto taskdto = convertToDto(task);
//			taskdto.setNewExpiryDate(DateUtility.convertToDDMMYYYY(taskdto.getNewExpiryDate()));
//			taskdto.setNextFollowUpDate(DateUtility.convertToDDMMYYYY(taskdto.getNextFollowUpDate()));
//			taskdto.setPolicyIssuedDate(DateUtility.convertToDDMMYYYY(taskdto.getPolicyIssuedDate()));

		taskdto.setNewExpiryDate(taskdto.getNewExpiryDate());
		taskdto.setNextFollowUpDate(taskdto.getNextFollowUpDate());
		taskdto.setPolicyIssuedDate(taskdto.getPolicyIssuedDate());

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
		if(task.getNewExpiryDate()!= null) {
			String extDate = formatLocalDateToString(task.getNewExpiryDate());
			taskDto.setNewExpiryDate(formatLocalDateToString(task.getNewExpiryDate()));
		}
		taskDto.setMessage(task.getMessage());
		taskDto.setMessageLink(task.getMessageLink());
		if(task.getPolicyIssuedDate()!= null) {
			taskDto.setPolicyIssuedDate(formatLocalDateToString(task.getPolicyIssuedDate()));
		}
		taskDto.setMessageStatus(task.getMessageStatus());
		taskDto.setDisposition(task.getDisposition());
		if(task.getNextFollowUpDate()!= null) {
			taskDto.setNextFollowUpDate(formatLocalDateToString(task.getNextFollowUpDate()));
		}
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
			taskObj.setAssign(true);
		} else {
			taskObj.setAssign(taskDto.isAssign());
		}

		// Map all fields
		// taskObj.setId(taskDto.getId());
		taskObj.setVehicleNumber(taskDto.getVehicleNumber());
		taskObj.setPartnerNumber(taskDto.getPartnerNumber());
		taskObj.setAgentName(taskDto.getAgentName());
		taskObj.setDriverName(taskDto.getDriverName());
		taskObj.setCity(taskDto.getCity());
		taskObj.setLastYearPolicyIssuedBy(taskDto.getLastYearPolicyIssuedBy());
		taskObj.setPartnerRate(taskDto.getPartnerRate());

		taskObj.setMessage(taskDto.getMessage());
		taskObj.setMessageLink(taskDto.getMessageLink());

		taskObj.setMessageStatus(taskDto.getMessageStatus());
		taskObj.setDisposition(taskDto.getDisposition());
		taskObj.setComments(taskDto.getComments());

		taskObj.setPolicyIssuedDate(parseDateString(DateUtility.convertToMMDDYYYY(taskDto.getPolicyIssuedDate())));
		taskObj.setNewExpiryDate(parseDateString(DateUtility.convertToMMDDYYYY(taskDto.getNewExpiryDate())));
		taskObj.setNextFollowUpDate(parseDateString(DateUtility.convertToMMDDYYYY(taskDto.getNextFollowUpDate())));
		

		// Map boolean fields

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

		Task task = taskRepository.findById(statusDto.getId()).orElse(null);
		String key = statusDto.getValidateKey();
		String oldValue = null;
		String newValue = statusDto.getMessage();
		if (key.equalsIgnoreCase("Status")) {
			oldValue = task.getStatus();
			task.setStatus(newValue);
			response.setMessage("Status updated.");
		} else if (key.equalsIgnoreCase("MessageStatus")) {
			oldValue = task.getMessageStatus();
			task.setMessageStatus(newValue);
			response.setMessage("Message status updated.");
		} else if (key.equalsIgnoreCase("Disposition")) {
			oldValue = task.getDisposition();
			task.setDisposition(newValue);
			response.setMessage("Disposition updated.");
		}
		task = taskRepository.save(task);
		taskHistoryService.createHistory(key, oldValue, newValue, task);
		return response;
	}

	@Override
	public Responce updateCommentAndMessage(StatusDto statusDto) {
		Responce response = new Responce();
		response.setStatus(200L);
		response.setMessage("Your " + statusDto.getValidateKey() + " is updated.");

		// Fetch the task from the database
		Task task = taskRepository.findById(statusDto.getId()).orElseThrow(() -> new NotFound("No Task Found."));

		String fieldName = statusDto.getValidateKey();
		String oldValue = null;
		String newValue = statusDto.getMessage();

		// Determine which field to update and track the old value
		switch (fieldName) {
		case "Comment":
			oldValue = task.getComments();
			task.setComments(newValue);
			break;
		case "Message":
			oldValue = task.getMessage();
			task.setMessage(newValue);
			break;
		case "Agent Name":
			oldValue = task.getAgentName();
			task.setAgentName(newValue);
			break;
		case "Driver Name":
			oldValue = task.getDriverName();
			task.setDriverName(newValue);
			break;
		case "City":
			oldValue = task.getCity();
			task.setCity(newValue);
			break;
		case "Last Year Policy IssuedBy":
			oldValue = task.getLastYearPolicyIssuedBy();
			task.setLastYearPolicyIssuedBy(newValue);
			break;
		case "Partner Rate":
			oldValue = task.getPartnerRate();
			task.setPartnerRate(newValue);
			break;
		case "New Expiry Date":
			if(task.getNewExpiryDate() != null)
				oldValue = formatLocalDateToString(task.getNewExpiryDate());
			task.setNewExpiryDate(parseDateString(newValue));
			break;
		case "Vehicle Number":
			oldValue = task.getVehicleNumber();
			task.setVehicleNumber(newValue);
			break;
		case "Partner Number":
			oldValue = task.getPartnerNumber();
			task.setPartnerNumber(newValue);
			break;
		case "Policy Issued Date":
			if(task.getPolicyIssuedDate() != null)
				oldValue = formatLocalDateToString(task.getPolicyIssuedDate());
			task.setPolicyIssuedDate(parseDateString(newValue));
			break;
		case "Next FollowUp Date":
			
			if(task.getNextFollowUpDate() != null)
				oldValue = formatLocalDateToString(task.getNextFollowUpDate());
			task.setNextFollowUpDate(parseDateString(newValue));
			break;
		default:
			response.setMessage("Key not found.");
			return response;
		}
		taskHistoryService.createHistory(fieldName, oldValue, newValue, task);
		return response;
	}

	@Override
	public Page<TaskDto> getFilteredTasks(FilterDto filterDto, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Task> taskPage = taskRepository.findTasksByFilter(filterDto, pageable);

		return taskPage.map(this::convertToDto);
	}

	@Override
	public byte[] generateExcel(FilterDto filterDto) throws IOException {
		
		System.out.println(filterDto);
		
		List<Task> taskList = taskRepository.getTasksByFilter(filterDto);
		System.out.println(taskList);
		List<TaskDto> tasks = taskList.parallelStream().map(this::convertToDto).collect(Collectors.toList());

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Tasks");

		Row headerRow = sheet.createRow(0);
		String[] headers = { "ID", "Vehicle Number", "Partner Number", "Agent Name", "Driver Name", "City",
				"Last Year Policy Issued By", "Partner Rate", "New Expiry Date", "Message", "Message Link",
				"Policy Issued Date", "Message Status", "Disposition", "Next Follow-Up Date", "Comments", "Status" };

		CellStyle headerStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		headerStyle.setFont(font);

		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		ExecutorService executorService = Executors.newFixedThreadPool(5);

		List<Future<Object>> futures = tasks.stream().map(task -> executorService.submit(() -> {
			int rowIdx = tasks.indexOf(task) + 1;
			Row row = sheet.createRow(rowIdx);

			row.createCell(0).setCellValue(task.getId());
			row.createCell(1).setCellValue(task.getVehicleNumber());
			row.createCell(2).setCellValue(task.getPartnerNumber());
			row.createCell(3).setCellValue(task.getAgentName());
			row.createCell(4).setCellValue(task.getDriverName());
			row.createCell(5).setCellValue(task.getCity());
			row.createCell(6).setCellValue(task.getLastYearPolicyIssuedBy());
			row.createCell(7).setCellValue(task.getPartnerRate());
			row.createCell(8).setCellValue(task.getNewExpiryDate());
			row.createCell(9).setCellValue(task.getMessage());
			row.createCell(10).setCellValue(task.getMessageLink());
			row.createCell(11).setCellValue(task.getPolicyIssuedDate());
			row.createCell(12).setCellValue(task.getMessageStatus());
			row.createCell(13).setCellValue(task.getDisposition());
			row.createCell(14).setCellValue(task.getNextFollowUpDate());
			row.createCell(15).setCellValue(task.getComments());
			row.createCell(16).setCellValue(task.getStatus());

			return null;
		})).collect(Collectors.toList());

		for (Future<Object> future : futures) {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		executorService.shutdown();

		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		return outputStream.toByteArray();
	}
	
	private static final DateTimeFormatter MM_DD_YYYY_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private LocalDate parseDateString(String dateString) {
	    if (dateString != null && !dateString.isBlank()) {
	        try {
	            return LocalDate.parse(dateString, MM_DD_YYYY_FORMATTER);
	        } catch (DateTimeParseException e) {
	            System.err.println("Error parsing date: " + dateString + " - " + e.getMessage());
	        }
	    }
	    return null;
	}

	
	private String formatLocalDateToString(LocalDate date) {
	    if (date != null) {
	        return MM_DD_YYYY_FORMATTER.format(date);
	    }
	    return null;
	}


}

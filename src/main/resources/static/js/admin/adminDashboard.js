
let currentPage = 0;
const pageSize = 50;
let currentSize = 50;
let sortOrder = 1; // 1 for ascending, -1 for descending
//Employee List url
const API_URL = window.location.origin + "/task/all";
// Initial fetch
//fetchTasks(currentPage);

//add new Employee Start
function addTask() {
	document.getElementById("taskTitle").textContent = "Add Task"
	document.getElementById("taskModal").style.display = "block";
}

function closeEmployeePopup() {
	document.getElementById("taskModal").style.display = "none";
}

function submitForm() {
	const form = document.getElementById("taskForm");
	const formData = new FormData(form);
	const userDto = Object.fromEntries(formData.entries());

	fetch("/task/add", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(userDto),
	})
		.then((response) => response.json())
		.then((data) => {
			alert(data.message);
			if (data.status === 200) {
				fetchTasks(currentPage);
				closeEmployeePopup();
				form.reset();
			}
		})
		.catch((error) => {
			console.error("Error:", error);
		});
}
//add new Employee End//

async function fetchTasks(page = currentPage, size = currentSize) {
	try {

		var taskType = document.getElementById('taskType');
		if (taskType != null) {
			taskType = taskType.value;
		}

		var userId = document.getElementById('userList').value;
		var url = `/task/all-active?page=${page}&size=${size}`;
		const baseUrl = `?page=${page}&size=${size}`
		if (taskType == 'active') {
			url = url = `/task/all-active`;
			if (userId != '') {
				url = url = `/task/emp-active/${userId}`;
			}
		} else if (taskType == 'completed') {
			url = url = `/task/all-completed`;
			if (userId != '') {
				url = url = `/task/emp-completed/${userId}`;
			}
		} else if (taskType == 'all') {
			url = url = `/task/all`;
			if (userId != '') {
				url = url = `/task/emp-all/${userId}`;
			}
		}


		const response = await fetch(url + baseUrl);
		const data = await response.json();

		// Populate table with task data
		const userTableBody = document.getElementById("userTableBody");
		userTableBody.innerHTML = ""; // Clear existing rows

		data.tasks.forEach((task, index) => {
			const row = document.createElement("tr");
			row.setAttribute("data-row", JSON.stringify(task)); // Store row data for filtering
			row.innerHTML = `
        <td>
            <input type="checkbox" class="rowCheckbox direction-left" data-task-id="${task.id}" />
            <span class="serialNumber">${page * size + index + 1}</span>
        </td>
        <td data-column="vehicleNumber">${task.vehicleNumber}</td>
        <td data-column="partnerNumber">${task.partnerNumber}</td>
        <td data-column="agentName">${task.agentName || "-"}</td>
        <td data-column="driverName">${task.driverName || "-"}</td>
        <td data-column="city">${task.city}</td>
        <td data-column="lastYearPolicyIssuedBy">${task.lastYearPolicyIssuedBy || "-"}</td>
        <td data-column="partnerRate">${task.partnerRate || "-"}</td>
        
        <td>
            <button class="tableBtn" onclick="editEmployee(${task.id})" title="Edit">
                <i class="fa fa-pencil-square" aria-hidden="true"></i>
            </button>
            <button class="tableBtn" onclick="confirmDelete(${task.id})" title="Delete">
                <i class="fa fa-trash-o" aria-hidden="true"></i>
            </button>
        </td>
      `;
			userTableBody.appendChild(row);
		});

		// Update pagination buttons
		setupPagination(data.currentPage, data.totalPages);
	} catch (error) {
		console.error("Error fetching tasks:", error);
	}
}


/* <td data-column="newExpiryDate">${task.newExpiryDate || "-"}</td>
 <td data-column="message">${task.message || "-"}</td>
 <td data-column="messageLink">${task.messageLink || "-"}</td>
 <td data-column="status">${task.status || "-"}</td>
 <td data-column="policyIssuedDate">${task.policyIssuedDate || "-"}</td>
 <td data-column="messageStatus">${task.messageStatus}</td>
 <td data-column="disposition">${task.disposition || "-"}</td>
 <td data-column="nextFollowUpDate">${task.nextFollowUpDate || "-"}</td>
 <td data-column="comments">${task.comments || "-"}</td>*/

function updateEntriesPerPage(size) {
	currentSize = parseInt(size, 10); // Update entries per page
	currentPage = 0; // Reset to the first page
	fetchTasks(); // Fetch tasks with the new page size
}

function setupPagination(current, totalPages) {

	const paginationContainer = document.getElementById("paginationButtons");
	paginationContainer.innerHTML = ""; // Clear existing buttons

	// Previous button
	const prevButton = document.createElement("button");
	prevButton.textContent = "Previous";
	prevButton.className = current === 0 ? "disabled" : "";
	prevButton.disabled = current === 0;
	prevButton.addEventListener("click", () => fetchTasks(current - 1));
	paginationContainer.appendChild(prevButton);

	// Page numbers
	for (let i = 0; i < totalPages; i++) {
		const pageButton = document.createElement("button");
		pageButton.textContent = i + 1;
		pageButton.className = i === current ? "disabled" : "";
		pageButton.disabled = i === current;
		pageButton.addEventListener("click", () => fetchTasks(i));
		paginationContainer.appendChild(pageButton);
	}

	// Next button
	const nextButton = document.createElement("button");
	nextButton.textContent = "Next";
	nextButton.className = current === totalPages - 1 ? "disabled" : "";
	nextButton.disabled = current === totalPages - 1;
	nextButton.addEventListener("click", () => fetchTasks(current + 1));
	paginationContainer.appendChild(nextButton);
}

//filter Table
function filterTable(searchValue) {
	const userTableBody = document.getElementById("userTableBody");
	const rows = Array.from(userTableBody.rows);

	if (searchValue.trim() === "") {
		// If search is empty, re-fetch and reload the default table data
		fetchTasks();
		return;
	}

	const lowerCaseSearch = searchValue.toLowerCase();

	rows.forEach((row) => {
		const rowData = row.getAttribute("data-row");
		if (rowData && rowData.toLowerCase().includes(lowerCaseSearch)) {
			row.style.display = ""; // Show matching row
		} else {
			row.style.display = "none"; // Hide non-matching row
		}
	});
}


// Table check box
function toggleSelectAll(selectAllCheckbox) {
	const checkboxes = document.querySelectorAll(".rowCheckbox");
	checkboxes.forEach(checkbox => {
		checkbox.checked = selectAllCheckbox.checked;
	});
}

function getSelectedRows() {
	const selectedCheckboxes = document.querySelectorAll(".rowCheckbox:checked");
	const selectedIds = Array.from(selectedCheckboxes).map(checkbox => checkbox.dataset.taskId);
	console.log("Selected Task IDs:", selectedIds);
}

//Table Filter



function sortTable(column) {
	const userTableBody = document.getElementById("userTableBody");
	const rows = Array.from(userTableBody.rows);

	// Check if the column is numeric or date
	const isNumericColumn = ['partnerRate', 'partnerNumber'].includes(column);
	const isDateColumn = ['newExpiryDate', 'policyIssuedDate'].includes(column);

	rows.sort((a, b) => {
		const cellA = a.querySelector(`[data-column="${column}"]`).innerText.trim();
		const cellB = b.querySelector(`[data-column="${column}"]`).innerText.trim();

		if (isNumericColumn) {
			// Numeric sorting
			return sortOrder * (parseFloat(cellA) - parseFloat(cellB));
		} else if (isDateColumn) {
			// Date sorting
			const dateA = new Date(cellA); // Convert to Date object
			const dateB = new Date(cellB);
			return sortOrder * (dateA - dateB); // Sort based on date comparison
		} else {
			// Alphabetic sorting
			return sortOrder * cellA.localeCompare(cellB);
		}
	});

	// Rebuild the table body with sorted rows
	userTableBody.innerHTML = "";
	rows.forEach((row, index) => {
		row.querySelector(".serialNumber").innerText = index + 1; // Update serial number
		userTableBody.appendChild(row);
	});

	// Toggle sort order for next click
	sortOrder *= -1;
}


//Edit Employee

function editEmployee(id) {
	fetch("/emp/edit/" + id, {
		method: "GET",
		headers: {
			"Content-Type": "application/json",
		},
	})
		.then((response) => {
			if (!response.ok) {
				throw new Error("Network response was not ok " + response.statusText);
			}
			return response.json();
		})
		.then((data) => {

			addTask();
			// Populate the form fields with the response data empTitle
			document.getElementById("taskTitle").textContent = "Update task deatils"
			document.getElementById("firstName").value = data.firstName || "";
			document.getElementById("lastName").value = data.lastName || "";
			document.getElementById("phone").value = data.phone || "";
			document.getElementById("email").value = data.email || "";
			document.getElementById("password").value = data.password || "";
			document.getElementById("country").value = data.country || "";
			//document.getElementById("countryCallingCode").value = data.countryCallingCode || "";
			document.getElementById("aadharNumber").value = data.aadharNumber || "";
			document.getElementById("empNo").value = data.empNo || "";
			document.getElementById("id").value = data.id || "";
		})
		.catch((error) => {
			console.error("Error fetching employee data:", error);
			alert("Failed to load employee details. Please try again.");
		});
}

//Delete Task
function confirmDelete(id) {

	const popup = document.getElementById("taskDelPopup");
	const message = document.getElementById("taskDelMessage");
	popup.style.display = "flex";


	//message.textContent = ;
	message.innerHTML = `Are you sure you want to remove?`;

	const yesButton = document.getElementById("taskDelYes");
	const noButton = document.getElementById("taskDelNo");


	yesButton.replaceWith(yesButton.cloneNode(true));
	noButton.replaceWith(noButton.cloneNode(true));

	document.getElementById("taskDelYes").addEventListener("click", function() {
		deleteTask(id);
		popup.style.display = "none";
	});

	document.getElementById("taskDelNo").addEventListener("click", function() {
		popup.style.display = "none";
	});
}

function deleteTask(id) {
	fetch("/task/delete/" + id, {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
	})
		.then((response) => response.json())
		.then((data) => {
			alert(data.message);
			if (data.status === 200) {
				fetchUsers(currentPage); // Refresh the user list
			}
		})
		.catch((error) => {
			console.error("Error:", error);
		});
}

// upload Task
function bulckUpload() {

	const popup = document.getElementById("taskPopup");
	const message = document.getElementById("taskMessage");
	popup.style.display = "flex";




	const yesButton = document.getElementById("upload");
	const noButton = document.getElementById("uploadCancle");


	yesButton.replaceWith(yesButton.cloneNode(true));
	noButton.replaceWith(noButton.cloneNode(true));
	const taskFile = document.getElementById("taskFile");
	document.getElementById("upload").addEventListener("click", function() {

		if (taskFile.value == null || taskFile.value == '') {
			message.textContent = `Please Select File`;
			message.style.backgroundColor = 'red';
			return;
		}

		const allowedExtensions = ["xls", "xlsx", "csv", "excel"];
		const file = taskFile.files[0];
		const fileExtension = file.name.split(".").pop().toLowerCase();

		if (!allowedExtensions.includes(fileExtension)) {
			alert("Only Excel files (.xls, .xlsx, .csv, .excel) are allowed.");
			return;
		}

		taskUpload(file);
		popup.style.display = "none";
	});

	document.getElementById("uploadCancle").addEventListener("click", function() {
		message.textContent = ``;
		message.style.backgroundColor = 'white';
		taskFile.value = '';
		popup.style.display = "none";
	});
}

function taskUpload(file) {
	const formData = new FormData();
	formData.append("file", file);

	const assignToDropdown = document.getElementById("assignTo");
	const userId = assignToDropdown.value;

	if (userId) {
		formData.append("userId", userId);
	}

	fetch("/task/upload", {
		method: "POST",
		body: formData,
	})
		.then((response) => {
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			return response.text();
		})
		.then((data) => {
			fetchTasks(currentPage);
			alert(data);
		})
		.catch((error) => {
			console.error("Error:", error);
		});
}



//Employee List
async function populateEmployeeDropdown() {
	try {

		const response = await fetch('/emp/select?page=0&size=10');
		if (!response.ok) {
			throw new Error('Failed to fetch employee data');
		}

		const employees = await response.json();

		const userList = document.getElementById('userList');
		userList.innerHTML = `<option class="dropdown-item form-control" value="">Select Employee</option>`;

		employees.forEach(employee => {
			const option = document.createElement('option');
			option.className = 'dropdown-item form-control';
			option.value = employee.id;
			option.textContent = employee.emp;
			userList.appendChild(option);
		});


		const assignTo = document.getElementById('assignTo');
		assignTo.innerHTML = `<option class="dropdown-item form-control" value="">Select Employee</option>`;

		employees.forEach(employee => {
			const option = document.createElement('option');
			option.className = 'dropdown-item form-control';
			option.value = employee.id;
			option.textContent = employee.emp;
			assignTo.appendChild(option);
		});

		const assignListTo = document.getElementById('assignListTo');
		assignListTo.innerHTML = `<option class="dropdown-item form-control" value="">Select Employee</option>`;

		employees.forEach(employee => {
			const option = document.createElement('option');
			option.className = 'dropdown-item form-control';
			option.value = employee.id;
			option.textContent = employee.emp;
			assignListTo.appendChild(option);
		});

	} catch (error) {
		console.error('Error populating employee dropdown:', error);
	}
}

function assignTasks(selectedTasks, selectedUsers) {
	const payload = {
		taskList: selectedTasks.map(Number), // Convert to numbers if IDs are numeric
		userList: selectedUsers.map(Number)
	};

	fetch('/task/assign', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(payload)
	})
		.then(response => response.json())
		.then(data => {
			fetchTasks(currentPage);
			alert(data.message);
		})
		.catch(error => {
			console.error('Error:', error);
		});
}


function taskAssignPopup() {

	const selectedTasks = Array.from(document.querySelectorAll('.rowCheckbox:checked'))
		.map(checkbox => checkbox.dataset.taskId);

	if (selectedTasks.length === 0) {
		alert("Please select at least one task before Assign");
		return;
	}

	const popup = document.getElementById("taskAssignPopup");
	const message = document.getElementById("taskAssignMessage");
	popup.style.display = "flex";

	const yesButton = document.getElementById("upload");
	const noButton = document.getElementById("uploadCancle");

	yesButton.replaceWith(yesButton.cloneNode(true));
	noButton.replaceWith(noButton.cloneNode(true));
	//const isSelectedUser = document.getElementById("userList").value;
	document.getElementById("taskAssignYes").addEventListener("click", function() {
		// Collect selected users
		//if(isSelectedUser )
		debugger;
		const selectedUsers = Array.from(document.getElementById('assignListTo').selectedOptions)
			.map(option => option.value);
		
		const UserId = document.getElementById('assignListTo').value;
		

		assignTasks(selectedTasks, selectedUsers);
		popup.style.display = "none";
	});

	document.getElementById("taskAssignNo").addEventListener("click", function() {
		message.textContent = ``;
		message.style.backgroundColor = 'white';
		taskFile.value = '';
		popup.style.display = "none";
	});
}


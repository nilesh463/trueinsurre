
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
			//alert(data.message);
			if (data.status === 200) {
				fetchTasks(currentPage);
				closeEmployeePopup();
				form.reset();
				showAlert("Task added sucessfully.");
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

		var selectDisposition = document.getElementById('selectDisposition').value;
		var selectStatus = document.getElementById('selectStatus').value;

		var userId = document.getElementById('userList').value;
		var url = `/task/all-active?page=${page}&size=${size}`;
		const baseUrl = `?page=${page}&size=${size}`
		if (selectDisposition != '' && selectStatus != '') {
			url = url = `/task/by-status-disposition/` + selectDisposition + `/` + selectStatus;
		} else if (selectDisposition != '') {
			url = url = `/task/by-disposition/` + selectDisposition;
		} else if (selectStatus != '') {
			url = url = `/task/by-status/` + selectStatus;
		} else {

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
		}

		const response = await fetch(url + baseUrl);
		const data = await response.json();

		// Populate table with task data
		const userTableBody = document.getElementById("userTableBody");
		userTableBody.innerHTML = ""; // Clear existing rows

		const statusOptions = [
			"Select status",
			"S1-Payment Collection Pending",
			"S2-Finance Confirmation Pending",
			"S3-Document Pending",
			"S4-Policy request Pending",
			"S4.1-EWI Pending",
			"S5-Pending from vendor",
			"S5.1-Pending for core insurance",
			"S5.2-Agent different case",
			"S6-Policy issued",
			"Not Interested",
			"Renewed Outside"
		];

		const dispositionOptionsList = [
			"Select disposition",
			"RNR",
			"Not Interested",
			"Follow up",
			"Renewed Outside",
			"Ready to pay today",
			"Issued",
			"Paid",
			"Payment Issue",
			"Others",
			"Price is High (C)",
			"Financial issue (C)",
			"Need EMI Option (C)",
			"Not Happy with Insurance Vendor (NC)",
			"Not Happy with Porter Service (NC)",
			"Wrong Number (NC)",
			"Sold his vehicle (NC)",
			"Relative is an Agent (NC)",
			"Payment Mode issue",
			"EWI Interest is high",
			"Not Answering the call",
			"Not Eligible",
			"KYC issue"
		];

		const messageStatusOptions = [
			"Select",
			"message sent",
			"Not on whatsapp"
		];

		data.tasks.forEach((task, index) => {
			const row = document.createElement("tr");

			// Generate the <select> dropdown with options
			const selectOptions = statusOptions.map(option => {
				const isSelected = task.status === option ? 'selected' : ''; // Check if the option matches task.status
				return `<option value="${option}" ${isSelected}>${option}</option>`;
			}).join("");

			const dispositionOptions = dispositionOptionsList.map(option => {
				const isSelected = task.disposition === option ? 'selected' : ''; // Check if the option matches task.disposition
				return `<option value="${option}" ${isSelected}>${option}</option>`;
			}).join("");

			const selectMessageStatusOptions = messageStatusOptions.map(option => {
				const isSelected = task.messageStatus === option ? 'selected' : ''; // Check if the option matches task.messageStatus
				return `<option value="${option}" ${isSelected}>${option}</option>`;
			}).join("");

			row.setAttribute("data-row", JSON.stringify(task)); // Store row data for filtering
			//<span ><a href="/task/${task.id}" title="History" style="color: green"><i class="fa fa-info-circle" aria-hidden="true"></i></a></span>
			row.innerHTML = `
        <td class="srWidth">
            <input type="checkbox" class="rowCheckbox direction-left" data-task-id="${task.id}" />
            <span class="serialNumber">${page * size + index + 1}</span>
            
        </td>
        <td data-column="vehicleNumber" onclick="confirmation('${task.vehicleNumber}','${task.id}','Vehicle Number')">${task.vehicleNumber}</td>
        <td data-column="partnerNumber" onclick="confirmation('${task.partnerNumber}','${task.id}','Partner Number')">${task.partnerNumber}</td>
        <td data-column="agentName" onclick="confirmation('${task.agentName}','${task.id}','Agent Name')">${task.agentName || "-"}</td>
        <td data-column="driverName" onclick="confirmation('${task.driverName}','${task.id}','Driver Name')">${task.driverName || "-"}</td>
        <td data-column="city" onclick="confirmation('${task.city}','${task.id}','City')">${task.city}</td>
        <td data-column="lastYearPolicyIssuedBy" onclick="confirmation('${task.lastYearPolicyIssuedBy}','${task.id}','Last Year Policy IssuedBy')">${task.lastYearPolicyIssuedBy || "-"}</td>
        <td data-column="partnerRate" onclick="confirmation('${task.partnerRate}','${task.id}','Partner Rate')">${task.partnerRate || "-"}</td>
        <td data-column="newExpiryDate" onclick="dateUpdate('${task.newExpiryDate}', '${task.id}', 'New Expiry Date')">${task.newExpiryDate || "-"}</td>
		 <td data-column="message" onclick="confirmation('${task.message}','${task.id}','Message')">
		   <i class="fa fa-external-link" aria-hidden="true"></i>
		</td>
		 <td data-column="messageLink">
			<button type="button" class="btns" 
				onclick="redirectToWhatsApp('${task.partnerNumber}', '${task.message}')">
				Send Message
			</button>
		</td>
		 <td data-column="status">
				<select class="form-control statusDropdown" data-task-id="${task.id}" onchange="statusUpdate(this.value, '${task.id}', 'Status')">
					${selectOptions}
				</select>
			</td>
		 <td data-column="policyIssuedDate" onclick="dateUpdate('${task.policyIssuedDate}', '${task.id}', 'Policy Issued Date')">${task.policyIssuedDate || "-"}</td>
		 <td data-column="messageStatus">
				<select class="form-control messageStatusDropdown" data-task-id="${task.id}" onchange="statusUpdate(this.value, '${task.id}', 'MessageStatus')">
					${selectMessageStatusOptions}
				</select>
			</td>
		 <td data-column="disposition">
				<select class="form-control dispositionDropdown" data-task-id="${task.id}" onchange="statusUpdate(this.value, '${task.id}', 'Disposition')">
					${dispositionOptions}
				</select>
			</td>
		 <td data-column="nextFollowUpDate" onclick="dateUpdate('${task.nextFollowUpDate}', '${task.id}', 'Next FollowUp Date')">${task.nextFollowUpDate || "-"}</td>
		 <td data-column="comments" onclick="confirmation('${task.comments}', '${task.id}','Comment')">
		    <i class="fa fa-external-link" aria-hidden="true"></i>
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

function statusUpdate(option, taskId, validateKey) {
	const payload = {
		id: taskId,
		message: option,
		validateKey: validateKey
	};

	fetch('/task/status', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(payload)
	})
		.then(response => response.json())
		.then(data => {
			fetchTasks(currentPage);
			showAlert(data.message);
		})
		.catch(error => {
			console.error('Error:', error);
		});
}


function updateEntriesPerPage(size) {
	currentSize = parseInt(size, 10);
	currentPage = 0;
	fetchTasks();
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

	// Clear the "Select All" checkbox when the filter changes
	document.getElementById("selectAll").checked = false;

	if (searchValue.trim() === "") {
		// If search is empty, re-fetch and reload the default table data
		fetchTasks();
		return;
	}

	// Split the input value into individual filters based on spaces
	const filters = searchValue.trim().toLowerCase().split(/\s+/);

	rows.forEach((row) => {
		const rowData = JSON.parse(row.getAttribute("data-row") || "{}"); // Parse data-row JSON
		const rowContent = JSON.stringify(rowData).toLowerCase(); // Flatten row data for search

		// Check if the row matches all filters
		const matchesAllFilters = filters.every((filter) => rowContent.includes(filter));

		// Show or hide the row based on the filters
		row.style.display = matchesAllFilters ? "" : "none";
	});
}



// Table check box
function toggleSelectAll(selectAllCheckbox) {
	const userTableBody = document.getElementById("userTableBody");
	const visibleRows = Array.from(userTableBody.rows).filter(row => row.style.display !== "none");

	visibleRows.forEach(row => {
		const checkbox = row.querySelector(".rowCheckbox");
		if (checkbox) {
			checkbox.checked = selectAllCheckbox.checked;
		}
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
			const dateA = new Date(cellA);
			const dateB = new Date(cellB);
			return sortOrder * (dateA - dateB);
		} else {
			// Alphabetic sorting
			return sortOrder * cellA.localeCompare(cellB);
		}
	});

	// Rebuild the table body with sorted rows
	userTableBody.innerHTML = "";
	rows.forEach((row, index) => {
		row.querySelector(".serialNumber").innerText = index + 1;
		userTableBody.appendChild(row);
	});

	// Toggle sort order for next click
	sortOrder *= -1;
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
			showAlert(data.message);
			if (data.status === 200) {
				fetchTasks(currentPage); // Refresh the user list
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
			showAlert("Only Excel files (.xls, .xlsx, .csv, .excel) are allowed.");
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
			return response.json();
		})
		.then((data) => {
			fetchTasks(currentPage);
			console.log(data.csvCount);
			console.log(data.duplicateCount);
			console.log(data.csvValidate);

			if (data.duplicateCount > 0) {
				openPopup(data.duplicateData);
			} else {
				showAlert("Task uploaded sucessfully.");
			}
			//alert(data);
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
		assignTo.innerHTML = `<option class="dropdown-item form-control" value="">Select Employee</option>,
		<option class="dropdown-item form-control" value="">Admin</option>`;

		employees.forEach(employee => {
			const option = document.createElement('option');
			option.className = 'dropdown-item form-control';
			option.value = employee.id;
			option.textContent = employee.emp;
			assignTo.appendChild(option);
		});

		const assignListTo = document.getElementById('assignListTo');
		assignListTo.innerHTML = `<option class="dropdown-item form-control" value="">Select Employee</option>,
		<option class="dropdown-item form-control" value="">Admin</option>`;

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
		taskList: selectedTasks.map(Number),
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
			
			//fetchTasks(currentPage);
			showAlert(data.message);
			window.location.reload();
		})
		.catch(error => {
			console.error('Error:', error);
		});
}


function taskAssignPopup() {

	const selectedTasks = Array.from(document.querySelectorAll('.rowCheckbox:checked'))
		.map(checkbox => checkbox.dataset.taskId);

	if (selectedTasks.length === 0) {
		showAlert("Please select at least one task before Assign");
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

		const selectedUsers = Array.from(document.getElementById('assignListTo').selectedOptions)
			.map(option => option.value);

		const UserId = document.getElementById('assignListTo').value;

		debugger;
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


function getPaymentLink(mobileNumber, message) {
    debugger;
    // UPI and payment details
    mobileNumber = "91"+mobileNumber;
    const upiId = "1998nileshicici@icici";
    const amount = "1";
    const currency = "INR"; // Currency, default is INR
    message = message || "Hi, please make a payment.";

    if (!mobileNumber) {
        alert("Mobile number is missing.");
        return;
    }

    // Construct the UPI payment link
    const gpayLink = `upi://pay?pa=${upiId}&am=${amount}&cu=${currency}`;
    const fullMessage = `${message}\n\nClick below to pay via Google Pay:\n${gpayLink}`;

    // Construct the WhatsApp URL
    const whatsappUrl = `https://wa.me/${mobileNumber}?text=${encodeURIComponent(fullMessage)}`;

    // Open the WhatsApp URL in a new tab
    window.open(whatsappUrl, '_blank');
}



//PaymentsLink 
function redirectToWhatsApps(mobileNumber, message) {
	debugger;
	mobileNumber = "918707825790";
	var upiId = "1998nileshicici@icici"
	var amount = "1";
	message = "Hi please Pay";
    if (message != null && message != '') {
        message = message.replace(/'/g, "\\'");
    }

    if (!mobileNumber) {
        showAlert("Mobile number is missing.");
        return;
    }

    // Construct GPay UPI payment link if UPI ID and amount are provided
    let gpayLink = '';
    if (upiId && amount) {
        gpayLink = `upi://pay?pa=${encodeURIComponent(upiId)}&am=${encodeURIComponent(amount)}&cu=INR`;
        message += `\n\nPay via GPay: ${gpayLink}`;
    }

    const whatsappUrl = `https://web.whatsapp.com/send?phone=${mobileNumber}&text=${encodeURIComponent(message)}`;

    window.open(whatsappUrl, '_blank');
}


function redirectToWhatsApp(mobileNumber, message) {

	if (message != null && message != '') {
		message = message.replace(/'/g, "\\'");
	}

	if (!mobileNumber) {
		showAlert("Mobile number is missing.");
		return;
	}

	const whatsappUrl = `https://web.whatsapp.com/send?phone=` + mobileNumber + `&text=+` + message;

	window.open(whatsappUrl, '_blank');
}


function confirmation(text, id, validateKey) {
	const popup = document.getElementById("commentPopup");

	if (!popup) {
		console.error("Popup element not found!");
		return;
	}

	popup.style.display = "flex";

	const userComment = document.getElementById("userComment");
	if (userComment) {
		userComment.value = '';
		userComment.value = text || '';
	} else {
		console.error("User comment textarea not found!");
		return;
	}

	const confrmLable = document.getElementById("confrmLable");
	if (confrmLable) {
		confrmLable.innerHTML = `Enter ${validateKey} :`;
	} else {
		console.error("Confirmation label not found!");
		return;
	}

	const yesButton = document.getElementById("commentDelYes");
	const noButton = document.getElementById("commentDelNo");

	if (yesButton && noButton) {
		yesButton.replaceWith(yesButton.cloneNode(true));
		noButton.replaceWith(noButton.cloneNode(true));

		document.getElementById("commentDelYes").addEventListener("click", function() {
			const textData = document.getElementById("userComment").value;
			updateCommentAndMessage(textData, id, validateKey);
			popup.style.display = "none";
		});

		document.getElementById("commentDelNo").addEventListener("click", function() {
			if (userComment) userComment.value = '';
			popup.style.display = "none";
		});
	} else {
		console.error("Yes or No button not found!");
	}
}


function updateCommentAndMessage(messageText, taskId, validateKey) {
	const payload = {
		id: taskId,
		message: messageText,
		validateKey: validateKey
	};

	fetch('/task/update', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(payload)
	})
		.then(response => response.json())
		.then(data => {
			fetchTasks(currentPage);
			//alert(data.message);
			showAlert(data.message);
		})
		.catch(error => {
			console.error('Error:', error);
		});
}

function dateUpdate(currentDate, taskId, label) {
	// Remove any previously added date input to avoid duplicates
	const existingInput = document.getElementById("dynamicDateInput");
	if (existingInput) {
		existingInput.remove();
	}

	// Create a dynamic Flatpickr input field
	const dateInput = document.createElement("input");
	dateInput.type = "text"; // Flatpickr works with text input
	dateInput.id = "dynamicDateInput";
	dateInput.style.position = "absolute";
	dateInput.style.zIndex = "9999"; // Ensure it's on top
	dateInput.style.left = `${event.clientX}px`; // Position near the click
	dateInput.style.top = `${event.clientY}px`; // Position near the click

	document.body.appendChild(dateInput); // Append the input to the document

	// Initialize Flatpickr with custom format
	flatpickr(dateInput, {
		dateFormat: "m/d/Y", // Set the format to mm/dd/yyyy
		defaultDate: currentDate, // Pre-fill the current date if available
		onClose: function(selectedDates, dateStr) {
			if (dateStr) {
				console.log(`Selected Date: ${dateStr}`);
				console.log(`Task ID: ${taskId}`);
				console.log(`Label: ${label}`);

				updateCommentAndMessage(dateStr, taskId, label);

				// Update the clicked cell with the new date
				const cell = event.target;
				cell.textContent = dateStr;
			}

			// Remove the input element after selection
			dateInput.remove();
		},
	});

	// Automatically open the calendar
	dateInput.focus();
}



//old Date Select
function dateUpdates(currentDate, taskId, label) {
	// Remove any previously added date input to avoid duplicates
	const existingInput = document.getElementById("dynamicDateInput");
	if (existingInput) {
		existingInput.remove();
	}

	// Create a dynamic date input field
	const dateInput = document.createElement("input");
	dateInput.type = "date";
	dateInput.id = "dynamicDateInput";
	dateInput.style.position = "absolute";
	dateInput.style.zIndex = "9999"; // Ensure it's on top
	dateInput.style.left = `${event.clientX}px`; // Position near the click
	dateInput.style.top = `${event.clientY}px`; // Position near the click

	// Convert mm/dd/yyyy to yyyy-mm-dd for date input compatibility
	if (currentDate) {
		const [month, day, year] = currentDate.split("/");
		dateInput.value = `${year}-${month.padStart(2, "0")}-${day.padStart(2, "0")}`;
	}

	document.body.appendChild(dateInput); // Append the input to the document

	dateInput.focus();

	dateInput.addEventListener("change", function() {
		if (dateInput.value) {
			// Convert yyyy-mm-dd back to mm/dd/yyyy
			const [year, month, day] = dateInput.value.split("-");
			const selectedDate = `${month}/${day}/${year}`;
			console.log(`Selected Date: ${selectedDate}`);
			console.log(`Task ID: ${taskId}`);
			console.log(`Label: ${label}`);

			updateCommentAndMessage(selectedDate, taskId, label);

			const cell = event.target;
			cell.textContent = selectedDate;
		}

		// Remove the input element after selection
		dateInput.remove();
	});

	// Remove the input element if the user clicks elsewhere
	dateInput.addEventListener("blur", function() {
		dateInput.remove();
	});
}

function openPopup(duplicateData) {
	const popupOverlay = document.getElementById('popupOverlay');
	const popup = document.getElementById('dupPopup');
	const tableBody = document.getElementById('popupTableBody');

	// Clear existing rows
	tableBody.innerHTML = '';

	// Populate table with duplicate data
	duplicateData.forEach((data, index) => {
		const row = document.createElement('tr');
		row.id = `dup${index + 1}`;

		row.innerHTML = `
                <td>${data.vehicleNumber || ''}</td>
                <td>${data.partnerNumber || ''}</td>
                <td>${data.agentName || ''}</td>
                <td>${data.driverName || ''}</td>
                <td>${data.city || ''}</td>
                <td>${data.partnerRate || ''}</td>
            `;

		tableBody.appendChild(row);
	});

	// Show popup and overlay
	popupOverlay.style.display = 'block';
	popup.style.display = 'block';
}

function closePopup() {
	document.getElementById('popupOverlay').style.display = 'none';
	document.getElementById('dupPopup').style.display = 'none';
}




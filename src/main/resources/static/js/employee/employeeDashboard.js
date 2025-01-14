
let currentPage = 0;
const pageSize = 50;
let currentSize = 50;
let sortOrder = 1; // 1 for ascending, -1 for descending
//Employee List url
const API_URL = window.location.origin + "/task/all";


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
		var taskType = document.getElementById('btnGroupDrop1');
		if (taskType != null) {
			taskType = taskType.value;
		}

		var selectDisposition = encodeURIComponent(document.getElementById('selectDisposition').value);
		var selectStatus = encodeURIComponent(document.getElementById('selectStatus').value);
		var userId = encodeURIComponent(document.getElementById('userId').value);

		var url = `/task/emp-active/${userId}?page=${page}&size=${size}`;

		if (selectDisposition && selectStatus) {
			url = `/task/by-status-disposition/${userId}/${selectDisposition}/${selectStatus}?page=${page}&size=${size}`;
		} else if (selectDisposition) {
			url = `/task/by-disposition/${userId}/${selectDisposition}?page=${page}&size=${size}`;
		} else if (selectStatus) {
			url = `/task/by-status/${userId}/${selectStatus}?page=${page}&size=${size}`;
		} else {
			if (taskType == 'active') {
				url = `/task/emp-active/${userId}?page=${page}&size=${size}`;
			} else if (taskType == 'completed') {
				url = `/task/emp-completed/${userId}?page=${page}&size=${size}`;
			} else if (taskType == 'all') {
				url = `/task/emp-all/${userId}?page=${page}&size=${size}`;
			}
		}
		const response = await fetch(url);
		const data = await response.json();

		// Populate table with task data
		const userTableBody = document.getElementById("userTableBody");
		userTableBody.innerHTML = ""; // Clear existing rows

		const statusOptions = [
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
			"message sent",
			"Not on whatsapp"
		];
		if (data.tasks.length == 0) {
			userTableBody.style.textAlign = "center";
			userTableBody.innerText = "No Task assign for you."
		}

		data.tasks.forEach((task, index) => {
			const row = document.createElement("tr");

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
			row.innerHTML = `
        <td class="srWidth">
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
        <td data-column="newExpiryDate">${task.newExpiryDate || "-"}</td>
		<td data-column="message" onclick="confirmMessage('${task.message}, ${task.id}')">
		    ${task.message || "-"}
		</td>
		 <td data-column="messageLink">
			<button type="button" class="btns" 
				onclick="redirectToWhatsApp('${task.partnerNumber}', '${task.message.replace(/'/g, "\\'")}')">
				Open in WhatsApp
			</button>
		</td>

		 <td data-column="status">
				<select class="form-control statusDropdown" data-task-id="${task.id}" onchange="statusUpdate(this.value, '${task.id}', 'Status')">
					${selectOptions}
				</select>
			</td>
		 <td data-column="policyIssuedDate">${task.policyIssuedDate || "-"}</td>
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
		 <td data-column="nextFollowUpDate">${task.nextFollowUpDate || "-"}</td>
		 
		 <td data-column="comments" onclick="confirmMessage('${task.comments}, ${task.id}')">
		    ${task.comments || "-"}
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
 <td data-column="status">
		<select class="form-control statusDropdown" data-task-id="${task.id}">
			${selectOptions}
		</select>
	</td>
 <td data-column="policyIssuedDate">${task.policyIssuedDate || "-"}</td>
 <td data-column="messageStatus">
		<select class="form-control messageStatusDropdown" data-task-id="${task.id}">
			${selectMessageStatusOptions}
		</select>
	</td>
 <td data-column="disposition">
		<select class="form-control dispositionDropdown" data-task-id="${task.id}">
			${dispositionOptions}
		</select>
	</td>
 <td data-column="nextFollowUpDate">${task.nextFollowUpDate || "-"}</td>
 <td data-column="comments">${task.comments || "-"}</td>*/

function statusUpdate(option, taskId, validateKey) {
	const payload = {
		id: taskId,
		message: option, // Convert to numbers if IDs are numeric
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
			alert(data.message);
		})
		.catch(error => {
			console.error('Error:', error);
		});
}

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

function redirectToWhatsApp(mobileNumber, message) {
	if (!mobileNumber) {
		alert('Mobile number is missing.');
		return;
	}

	const whatsappUrl = `https://web.whatsapp.com/send?phone=` + mobileNumber + `&text=+` + message;

	window.open(whatsappUrl, '_blank');
}

//Message Update
function confirmMessage(messageText, id) {

	const popup = document.getElementById("messagePopup");
	//const message = document.getElementById("messageErrorText");
	popup.style.display = "flex";

	const userMessage = document.getElementById("userMessage");
	userMessage .innerHTML = messageText;
	/*message.innerHTML = `Update Message.`;*/

	const yesButton = document.getElementById("messageDelYes");
	const noButton = document.getElementById("messageDelNo");


	yesButton.replaceWith(yesButton.cloneNode(true));
	noButton.replaceWith(noButton.cloneNode(true));

	document.getElementById("messageDelYes").addEventListener("click", function() {
		updateMessage(messageText, id);
		popup.style.display = "none";
	});

	document.getElementById("messageDelNo").addEventListener("click", function() {
		popup.style.display = "none";
	});
}

function updateMessage(message, id) {
	fetch("/task/update-message/" + id, {
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

// comment Update
function confirmComment(messageText, id) {

	const popup = document.getElementById("commentPopup");
	//const message = document.getElementById("commentErrorText");
	popup.style.display = "flex";


	const userComment = document.getElementById("userComment");
	userComment .innerHTML = messageText;
	//message.textContent = ;
	//message.innerHTML = `Are you sure you want to remove?`;

	const yesButton = document.getElementById("commentDelYes");
	const noButton = document.getElementById("commentDelNo");


	yesButton.replaceWith(yesButton.cloneNode(true));
	noButton.replaceWith(noButton.cloneNode(true));

	document.getElementById("commentDelYes").addEventListener("click", function() {
		updateMessage(message, id);
		popup.style.display = "none";
	});

	document.getElementById("commentDelNo").addEventListener("click", function() {
		popup.style.display = "none";
	});
}

function updateComment(messageText, id) {
	fetch("/task/update-comment/" + id, {
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

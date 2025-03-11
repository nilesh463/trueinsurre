
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
	document.getElementById("id").value = document.getElementById("userId").value;
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

async function getByfilterData(data) {
	var page = currentPage;
	var size = currentSize;
	try {
		/*var taskType = document.getElementById('btnGroupDrop1');
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
		const data = await response.json();*/

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

			row.setAttribute("data-row", JSON.stringify(task)); 
			// <span ><a href="/task/emp/${task.id}" title="History" style="color: green"><i class="fa fa-info-circle" aria-hidden="true"></i></a></span>
			row.innerHTML = `
        <td class="srWidth">
            <input type="checkbox" class="rowCheckbox direction-left" data-task-id="${task.id}" />
            <span class="serialNumber">${page * size + index + 1}</span>
            
       
        </td>
        <td data-column="vehicleNumber" >${task.vehicleNumber}</td>
        <td data-column="partnerNumber" >${task.partnerNumber}</td>
        <td data-column="agentName">${task.agentName || "-"}</td>
        <td data-column="driverName" >${task.driverName || "-"}</td>
        <td data-column="city" >${task.city}</td>
        <td data-column="lastYearPolicyIssuedBy" >${task.lastYearPolicyIssuedBy || "-"}</td>
        <td data-column="partnerRate" >${task.partnerRate || "-"}</td>
        <td data-column="newExpiryDate" onclick="dateUpdate('${task.newExpiryDate}', '${task.id}', 'New Expiry Date')">${task.newExpiryDate || "-"}</td>
		<td data-column="message" onclick="confirmation(decodeURIComponent('${encodeURIComponent(task.message)}'), '${task.id}', 'Message')">
		   <i class="fa fa-external-link" aria-hidden="true"></i>
		</td>
		 <td data-column="messageLink">
			<button type="button" class="btns" 
				onclick="redirectToWhatsApp('${task.partnerNumber}', '${task.message.replace(/'/g, "\\'")}')">
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
		 <td data-column="comments" onclick="confirmation(decodeURIComponent('${encodeURIComponent(task.comments)}'), '${task.id}','Comment')">
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
			showAlert(data.message);
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

    const createPageButton = (page) => {
        const pageButton = document.createElement("button");
        pageButton.textContent = page + 1;
        pageButton.className = page === current ? "active" : "";
        pageButton.disabled = page === current;
        pageButton.addEventListener("click", () => fetchTasks(page));
        paginationContainer.appendChild(pageButton);
    };

    if (totalPages <= 7) {
        // Show all pages if total pages are small
        for (let i = 0; i < totalPages; i++) createPageButton(i);
    } else {
        createPageButton(0); // First page

        if (current > 3) paginationContainer.appendChild(document.createTextNode("..."));

        for (let i = Math.max(1, current - 2); i <= Math.min(totalPages - 2, current + 2); i++) {
            createPageButton(i);
        }

        if (current < totalPages - 4) paginationContainer.appendChild(document.createTextNode("..."));

        createPageButton(totalPages - 1); // Last page
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
		showAlert("Mobile number is missing.");
		return;
	}
	const whatsappUrl = `https://web.whatsapp.com/send?phone=` + mobileNumber + `&text=+` + message;
	window.open(whatsappUrl, '_blank');
}

//Message Update
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

        document.getElementById("commentDelYes").addEventListener("click", function () {
            const textData = document.getElementById("userComment").value;
            updateCommentAndMessage(textData, id, validateKey);
            popup.style.display = "none";
        });

        document.getElementById("commentDelNo").addEventListener("click", function () {
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
			showAlert(data.message);
		})
		.catch(error => {
			console.error('Error:', error);
		});
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
			showAlert(data.message);
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
    onClose: function (selectedDates, dateStr) {
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


function openFilter() {
	document.getElementById("filter").style.display = "block";
}

function closeFilterPopup() {
	document.getElementById("filter").style.display = "none";
}

function datesFormat(datefrom) {
	// Remove any previously added date input to avoid duplicates
	var currentDate = '';
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
	dateInput.style.left = `${event.clientX}px`;
	dateInput.style.top = `${event.clientY}px`;

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
			const cell = event.target;
			cell.textContent = selectedDate;
			if (datefrom === "nextfollowUpdate") {
				document.getElementById('filternextfollowUpdate').value = selectedDate;
			} else if (datefrom === "newExpiryDate") {
				document.getElementById('filternewExpiryDate').value = selectedDate;
			} else if (datefrom === "policyIssuedDate") {
				document.getElementById('filterpolicyIssuedDate').value = selectedDate;
			}
		}

		// Remove the input element after selection
		dateInput.remove();
	});

	// Remove the input element if the user clicks elsewhere
	dateInput.addEventListener("blur", function() {
		dateInput.remove();
	});
}

async function fetchTasks(page = currentPage, size = currentSize) {
	
	const url = `/task/filtered?page=${page}&size=${size}`;
	var userId = document.getElementById('userId').value;
	var vehicleNumber = document.getElementById('filterVehicleNumber').value;
	var partnerNumber = document.getElementById('filterPartnerNumber').value;
	var agentName = document.getElementById('filteragentName').value;
	var driverName = document.getElementById('filterDriverName').value;
	var city = document.getElementById('filterCity').value;
	var lastYearPolicyIssuedBy = document.getElementById('filterLastYearIssuedby').value;
	var partnerRate = document.getElementById('filterPartnerRate').value;
	var newExpiryDate = document.getElementById('filternewExpiryDate').value;
	var policyIssuedDate = document.getElementById('filterpolicyIssuedDate').value;
	var messageStatus = document.getElementById('filterMessageStatus').value;
	var disposition = document.getElementById('filterDisposition').value;
	var nextFollowUpDate = document.getElementById('filternextfollowUpdate').value;
	var status = document.getElementById('filterStatus').value;

	const filterDto = {
		userId: userId,
		vehicleNumber: vehicleNumber,
		partnerNumber: partnerNumber,
		agentName: agentName,
		driverName: driverName,
		city: city,
		lastYearPolicyIssuedBy: lastYearPolicyIssuedBy,
		partnerRate: partnerRate,
		newExpiryDate: newExpiryDate,
		policyIssuedDate: policyIssuedDate,
		messageStatus: messageStatus,
		disposition: disposition,
		nextFollowUpDate: nextFollowUpDate,
		status: status
	};
	toggleLoader();
	closeFilterPopup();

	try {
		const response = await fetch(url, {
			method: "Post",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(filterDto)
		});

		if (!response.ok) {
			toggleLoader();
			throw new Error(`HTTP error! Status: ${response.status}`);
		}

		const data = await response.json();
		getByfilterData(data);
		toggleLoader();
		console.log(data);
		return data;
	} catch (error) {
		console.error("Error fetching filtered tasks:", error);
	}
}



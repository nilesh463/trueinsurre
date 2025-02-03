
let currentPage = 0;
const pageSize = 10;
//Employee List url
const API_URL = window.location.origin+"/emp/list"; 
// Initial fetch
fetchUsers(currentPage);

//add new Employee Start
function addEmployee() {
	document.getElementById("empTitle").textContent = "Add Employee"
    document.getElementById("employeeModal").style.display = "block";
}

function closeEmployeePopup() {
  document.getElementById("employeeModal").style.display = "none";
}

function submitForm() {
  const form = document.getElementById("employeeForm");
  const formData = new FormData(form);
  const userDto = Object.fromEntries(formData.entries());
  
  // Check if all form fields are empty
    const isFormEmpty = Object.values(userDto).every(value => value.trim() === "");
	
	if (userDto.email=='') {
        showAlert("Please fill out the Email field.");
        return;
    }
    
	if (userDto.id == '') {
		if (userDto.password == '') {
			showAlert("Please fill out the Password field.");
			return;
		}
	}
    if (isFormEmpty) {
        showAlert("Please fill out the fields.");
        return;
    }

  fetch("/emp/add", {
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
		 fetchUsers(currentPage);
        closeEmployeePopup();
        form.reset();
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}
//add new Employee End//



// Function to fetch and display data
function fetchUsers(page) {
    fetch(`${API_URL}?page=${page}&size=${pageSize}`)
        .then(response => response.json())
        .then(data => {
            displayUsers(data.content);
            setupPagination(data.pageable.pageNumber, data.totalPages);
        })
        .catch(error => console.error("Error fetching users:", error));
}

// Function to display user data in the table
function displayUsers(users) {
    const tableBody = document.getElementById("userTableBody");
    tableBody.innerHTML = ""; // Clear the table

    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${user.firstName}</td>
            <td>${user.email}</td>
            <td>${user.phone || "N/A"}</td>
            <td>${user.empNo || "N/A"}</td>
            <td>${user.country}</td>
            <td>${user.aadharNumber || "N/A"}</td>
            <td>
	            <button class="tableBtn" onclick="editEmployee(${user.id})" tile="Edit"><i class="fa fa-pencil-square" aria-hidden="true"></i></button>
	             <button class="tableBtn" onclick="confirmDelete(${user.id}, '${user.firstName}', '${user.empNo}', '${user.email}')" tile="Delete"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
	             
	        </td>
        `;
        tableBody.appendChild(row);
    });
}

// Function to create pagination buttons
function setupPagination(current, totalPages) {
    const paginationContainer = document.getElementById("paginationButtons");
    paginationContainer.innerHTML = ""; // Clear existing buttons

    // Previous button
    const prevButton = document.createElement("button");
    prevButton.textContent = "Previous";
    prevButton.className = current === 0 ? "disabled" : "";
    prevButton.disabled = current === 0;
    prevButton.addEventListener("click", () => fetchUsers(current - 1));
    paginationContainer.appendChild(prevButton);

    // Page numbers
    for (let i = 0; i < totalPages; i++) {
        const pageButton = document.createElement("button");
        pageButton.textContent = i + 1;
        pageButton.className = i === current ? "disabled" : "";
        pageButton.disabled = i === current;
        pageButton.addEventListener("click", () => fetchUsers(i));
        paginationContainer.appendChild(pageButton);
    }

    // Next button
    const nextButton = document.createElement("button");
    nextButton.textContent = "Next";
    nextButton.className = current === totalPages - 1 ? "disabled" : "";
    nextButton.disabled = current === totalPages - 1;
    nextButton.addEventListener("click", () => fetchUsers(current + 1));
    paginationContainer.appendChild(nextButton);
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
			
			addEmployee();
            // Populate the form fields with the response data empTitle
            document.getElementById("empTitle").textContent = "Update employee deatils"
            document.getElementById("firstName").value = data.firstName || "";
            document.getElementById("lastName").value = data.lastName || "";
            document.getElementById("phone").value = data.phone || "";
            document.getElementById("email").value = data.email || "";
            document.getElementById("passwordLable").textContent = "Update Password";
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

//Delete Employee
function confirmDelete(id, name,empId, email) {
    
    const popup = document.getElementById("empDelPopup");
    const message = document.getElementById("empDelMessage");
    popup.style.display = "flex";

    
    //message.textContent = `Are you sure you want to delete '${name}'?`;
	message.innerHTML = `
    <h4>Employee Details:</h4><br>
    Employee Name: ${name}<br>
    Employee ID: ${empId}<br><br>
    Are you sure you want to delete this Employee?
`;
// Employee Email: ${email}<br>
    
    const yesButton = document.getElementById("empDelYes");
    const noButton = document.getElementById("empDelNo");

    
    yesButton.replaceWith(yesButton.cloneNode(true));
    noButton.replaceWith(noButton.cloneNode(true));

    document.getElementById("empDelYes").addEventListener("click", function () {
        deleteEmployee(id);
        popup.style.display = "none"; 
    });

    document.getElementById("empDelNo").addEventListener("click", function () {
        popup.style.display = "none"; 
    });
}

function deleteEmployee(id) {
    fetch("/emp/delete/" + id, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
    })
        .then((response) => response.json())
        .then((data) => {
            showAlert(data.message);
            if (data.status === 200) {
                fetchUsers(currentPage); // Refresh the user list
            }
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}


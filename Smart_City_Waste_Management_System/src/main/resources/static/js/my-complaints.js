
document.addEventListener("DOMContentLoaded", function () {

    const API_BASE = "/api";

    const tableBody = document.getElementById("complaintsTableBody");
    const messageBox = document.getElementById("messageBox");
    const emptyState = document.getElementById("emptyState");

    const totalComplaints = document.getElementById("totalComplaints");
    const pendingComplaints = document.getElementById("pendingComplaints");
    const progressComplaints = document.getElementById("progressComplaints");
    const resolvedComplaints = document.getElementById("resolvedComplaints");

    const searchInput = document.getElementById("searchInput");
    const statusFilter = document.getElementById("statusFilter");
    const refreshBtn = document.getElementById("refreshBtn");
    const logoutBtn = document.getElementById("logoutBtn");

    let complaints = [];

    // Display Message
    function showMessage(message, type) {
        messageBox.textContent = message;
        messageBox.className = "message-box " + type;
    }

    function clearMessage() {
        messageBox.textContent = "";
        messageBox.className = "message-box";
    }

    // Check Login Session
    async function checkLoginSession() {

        try {

            const response = await fetch(
                API_BASE + "/users/session",
                {
                    method: "GET",
                    credentials: "same-origin"
                }
            );

            if (response.status === 401) {
                window.location.href = "login.html";
                return false;
            }

            if (!response.ok) {
                showMessage("Unable to verify your login session.", "error");
                return false;
            }

            const user = await response.json();

            if (user.roleName !== "USER") {
                window.location.href = "login.html";
                return false;
            }

            return true;

        } catch (error) {

            showMessage("Unable to connect to the server.", "error");
            return false;
        }
    }

    // Format Date
    function formatDate(dateValue) {

        if (!dateValue) {
            return "Not available";
        }

        const date = new Date(dateValue);

        if (isNaN(date.getTime())) {
            return "Not available";
        }

        return date.toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric"
        });
    }

    // Get Category Name
    function getCategoryName(complaint) {

        if (complaint.categoryName) {
            return complaint.categoryName;
        }

        if (complaint.category && complaint.category.categoryName) {
            return complaint.category.categoryName;
        }

        return "Not available";
    }

    // Get Status Class
    function getStatusClass(status) {

        switch (status) {

            case "PENDING":
                return "status-pending";

            case "ASSIGNED":
                return "status-assigned";

            case "IN_PROGRESS":
                return "status-in-progress";

            case "RESOLVED":
                return "status-resolved";

            case "REJECTED":
                return "status-rejected";

            default:
                return "";
        }
    }

    // Format Status Text
    function formatStatus(status) {

        if (!status) {
            return "Unknown";
        }

        return status
            .replaceAll("_", " ")
            .toLowerCase()
            .replace(/\b\w/g, function (letter) {
                return letter.toUpperCase();
            });
    }

    // Update Summary Cards
    function updateSummary() {

        totalComplaints.textContent = complaints.length;

        pendingComplaints.textContent = complaints.filter(
            complaint => complaint.status === "PENDING"
        ).length;

        progressComplaints.textContent = complaints.filter(
            complaint => complaint.status === "IN_PROGRESS"
        ).length;

        resolvedComplaints.textContent = complaints.filter(
            complaint => complaint.status === "RESOLVED"
        ).length;
    }

    // Show Complaint Details
    function showComplaintDetails(complaint) {

        const details =
            "Complaint ID: " + complaint.complaintId + "\n\n" +
            "Category: " + getCategoryName(complaint) + "\n\n" +
            "Description: " + (complaint.description || "Not available") + "\n\n" +
            "Reported Date: " + formatDate(complaint.createdAt) + "\n\n" +
            "Status: " + formatStatus(complaint.status) + "\n\n" +
            "Location: " + (complaint.locationAddress || "Not available");

        alert(details);
    }

    // Create Table Cell
    function createCell(value) {

        const cell = document.createElement("td");
        cell.textContent = value;
        return cell;
    }

    // Display Complaints
    function displayComplaints(list) {

        tableBody.innerHTML = "";
        emptyState.style.display = "none";

        if (list.length === 0) {

            const row = document.createElement("tr");
            const cell = document.createElement("td");

            cell.colSpan = 6;
            cell.className = "no-results";
            cell.textContent = "No matching complaints found.";

            row.appendChild(cell);
            tableBody.appendChild(row);

            if (complaints.length === 0) {
                emptyState.style.display = "block";
            }

            return;
        }

        list.forEach(function (complaint) {

            const row = document.createElement("tr");

            // Complaint ID
            row.appendChild(
                createCell("#" + complaint.complaintId)
            );

            // Category
            row.appendChild(
                createCell(getCategoryName(complaint))
            );

            // Description
            const description = complaint.description || "Not available";

            const shortDescription = description.length > 45
                ? description.substring(0, 45) + "..."
                : description;

            row.appendChild(createCell(shortDescription));

            // Reported Date
            row.appendChild(
                createCell(formatDate(complaint.createdAt))
            );

            // Status
            const statusCell = document.createElement("td");
            const statusBadge = document.createElement("span");

            statusBadge.className =
                "status " + getStatusClass(complaint.status);

            statusBadge.textContent = formatStatus(complaint.status);

            statusCell.appendChild(statusBadge);
            row.appendChild(statusCell);

            // Action Button
            const actionCell = document.createElement("td");
            const viewButton = document.createElement("button");

            viewButton.type = "button";
            viewButton.className = "view-btn";
            viewButton.textContent = "View";

            viewButton.addEventListener("click", function () {
                showComplaintDetails(complaint);
            });

            actionCell.appendChild(viewButton);
            row.appendChild(actionCell);

            tableBody.appendChild(row);
        });
    }

    // Search and Filter
    function applyFilters() {

        const searchText = searchInput.value.trim().toLowerCase();
        const selectedStatus = statusFilter.value;

        const filteredComplaints = complaints.filter(function (complaint) {

            const complaintId = String(complaint.complaintId || "");
            const description = (complaint.description || "").toLowerCase();

            const matchesSearch =
                complaintId.includes(searchText) ||
                description.includes(searchText);

            const matchesStatus =
                selectedStatus === "ALL" ||
                complaint.status === selectedStatus;

            return matchesSearch && matchesStatus;
        });

        displayComplaints(filteredComplaints);
    }

    // Load My Complaints
    async function loadComplaints() {

        clearMessage();

        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="loading-cell">
                    Loading your complaints...
                </td>
            </tr>
        `;

        emptyState.style.display = "none";

        try {

            const response = await fetch(
                API_BASE + "/complaints/my-complaints",
                {
                    method: "GET",
                    credentials: "same-origin"
                }
            );

            if (response.status === 401) {
                window.location.href = "login.html";
                return;
            }

            if (!response.ok) {
                throw new Error("Unable to load complaints.");
            }

            const data = await response.json();

            if (!Array.isArray(data)) {
                throw new Error("Unexpected complaint data received.");
            }

            complaints = data;

            updateSummary();
            applyFilters();

        } catch (error) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="no-results">
                        Unable to load complaints. Please try again.
                    </td>
                </tr>
            `;

            showMessage(error.message, "error");
        }
    }

    // Refresh Button
    refreshBtn.addEventListener("click", function () {
        loadComplaints();
    });

    // Search
    searchInput.addEventListener("input", function () {
        applyFilters();
    });

    // Status Filter
    statusFilter.addEventListener("change", function () {
        applyFilters();
    });

    // Logout
    logoutBtn.addEventListener("click", async function (event) {

        event.preventDefault();

        try {

            await fetch(
                API_BASE + "/users/logout",
                {
                    method: "POST",
                    credentials: "same-origin"
                }
            );

        } catch (error) {
            console.error("Logout error:", error);
        }

        window.location.href = "login.html";
    });

    // Initialize Page
    async function initializePage() {

        const isLoggedIn = await checkLoginSession();

        if (isLoggedIn) {
            await loadComplaints();
        }
    }

    initializePage();

});

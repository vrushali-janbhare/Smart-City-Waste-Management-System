
document.addEventListener("DOMContentLoaded", function () {
    loadDashboard();

    const logoutButton = document.getElementById("logoutBtn");

    if (logoutButton) {
        logoutButton.addEventListener("click", function (event) {
            event.preventDefault();
            logoutUser();
        });
    }
});

// 1. Load dashboard data
async function loadDashboard() {
    try {
        const response = await fetch("/api/users/session");

        if (response.status === 401) {
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to verify user session.");
        }

        const user = await response.json();

        if (user.roleName !== "USER") {
            window.location.href = "login.html";
            return;
        }

        displayUserDetails(user);
        await loadComplaints();

    } catch (error) {
        console.error("Dashboard error:", error);
        alert("Unable to load dashboard. Please try again.");
    }
}

// 2. Display logged-in user details
function displayUserDetails(user) {
    const fullName = user.fullName || "Citizen";

    const profileName = document.querySelector(".profile-details h4");
    const profileAvatar = document.querySelector(".profile-avatar");
    const welcomeHeading = document.querySelector(".welcome-section h1");

    if (profileName) {
        profileName.textContent = fullName;
    }

    if (profileAvatar) {
        profileAvatar.textContent = fullName.charAt(0).toUpperCase();
    }

    if (welcomeHeading) {
        welcomeHeading.textContent =
            `Welcome back, ${fullName}! 👋`;
    }
}

// 3. Load complaints of logged-in user
async function loadComplaints() {
    try {
        const response = await fetch("/api/complaints/my-complaints");

        if (response.status === 401) {
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to load complaints.");
        }

        const complaints = await response.json();

        updateDashboardStats(complaints);
        displayRecentComplaints(complaints);
        displayRecentActivity(complaints);

    } catch (error) {
        console.error("Complaint loading error:", error);
        alert("Unable to load your complaints.");
    }
}

// 4. Update dashboard statistics
function updateDashboardStats(complaints) {
    const total = complaints.length;

    const pending = complaints.filter(
        complaint => complaint.status === "PENDING"
    ).length;

    const inProgress = complaints.filter(
        complaint =>
            complaint.status === "ASSIGNED" ||
            complaint.status === "IN_PROGRESS"
    ).length;

    const resolved = complaints.filter(
        complaint => complaint.status === "RESOLVED"
    ).length;

    const statValues = document.querySelectorAll(
        ".stat-card .stat-details h2"
    );

    if (statValues.length >= 4) {
        statValues[0].textContent = total;
        statValues[1].textContent = pending;
        statValues[2].textContent = inProgress;
        statValues[3].textContent = resolved;
    }
}

// 5. Display recent complaints
function displayRecentComplaints(complaints) {
    const tableBody = document.querySelector(
        ".table-container tbody"
    );

    if (!tableBody) {
        return;
    }

    tableBody.innerHTML = "";

    if (complaints.length === 0) {
        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 6;
        cell.textContent =
            "You have not reported any waste complaints yet.";

        row.appendChild(cell);
        tableBody.appendChild(row);
        return;
    }

    const recentComplaints = [...complaints].sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
    );

    recentComplaints.slice(0, 3).forEach(complaint => {
        const row = document.createElement("tr");

        // Complaint ID
        const idCell = document.createElement("td");
        idCell.className = "complaint-id";
        idCell.textContent = complaint.complaintId
            ? `#${complaint.complaintId}`
            : "N/A";

        // Waste category
        const categoryCell = document.createElement("td");
        categoryCell.textContent =
            complaint.categoryName ||
            (complaint.category && complaint.category.categoryName) ||
            "Not available";

        // Location
        const locationCell = document.createElement("td");
        locationCell.textContent =
            complaint.locationAddress || "Not available";

        // Reported date
        const dateCell = document.createElement("td");
        dateCell.textContent = formatDate(complaint.createdAt);

        // Complaint status
        const statusCell = document.createElement("td");
        const statusSpan = document.createElement("span");

        statusSpan.className =
            `status ${getStatusClass(complaint.status)}`;

        statusSpan.textContent =
            formatStatus(complaint.status);

        statusCell.appendChild(statusSpan);

        // View complaint
        const actionCell = document.createElement("td");
        const viewLink = document.createElement("a");

        viewLink.href =
            `track-complaint.html?complaintId=${encodeURIComponent(complaint.complaintId)}`;

        viewLink.className = "table-action";
        viewLink.textContent = "View";

        actionCell.appendChild(viewLink);

        row.appendChild(idCell);
        row.appendChild(categoryCell);
        row.appendChild(locationCell);
        row.appendChild(dateCell);
        row.appendChild(statusCell);
        row.appendChild(actionCell);

        tableBody.appendChild(row);
    });
}

// 6. Display recent activity
function displayRecentActivity(complaints) {
    const activityList = document.querySelector(".activity-list");

    if (!activityList) {
        return;
    }

    activityList.innerHTML = "";

    if (complaints.length === 0) {
        const message = document.createElement("p");
        message.textContent = "No recent activity available.";
        activityList.appendChild(message);
        return;
    }

    const recentComplaints = [...complaints].sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
    );

    recentComplaints.slice(0, 3).forEach(complaint => {
        const activityItem = document.createElement("div");
        activityItem.className = "activity-item";

        const indicator = document.createElement("div");
        indicator.className =
            `activity-indicator ${getActivityColor(complaint.status)}`;

        const activityDetails = document.createElement("div");

        const heading = document.createElement("h4");
        heading.textContent =
            `Complaint #${complaint.complaintId}`;

        const description = document.createElement("p");
        description.textContent =
            `Your complaint status is ${formatStatus(complaint.status)}.`;

        const date = document.createElement("span");
        date.textContent = formatDate(complaint.createdAt);

        activityDetails.appendChild(heading);
        activityDetails.appendChild(description);
        activityDetails.appendChild(date);

        activityItem.appendChild(indicator);
        activityItem.appendChild(activityDetails);

        activityList.appendChild(activityItem);
    });
}

// 7. Format complaint status
function formatStatus(status) {
    const statusNames = {
        PENDING: "Pending",
        ASSIGNED: "Assigned",
        IN_PROGRESS: "In Progress",
        RESOLVED: "Resolved",
        REJECTED: "Rejected"
    };

    return statusNames[status] || "Unknown";
}

// 8. Get CSS class for status
function getStatusClass(status) {
    const statusClasses = {
        PENDING: "pending",
        ASSIGNED: "progress",
        IN_PROGRESS: "progress",
        RESOLVED: "resolved",
        REJECTED: "rejected"
    };

    return statusClasses[status] || "pending";
}

// 9. Get activity indicator color
function getActivityColor(status) {
    const colors = {
        PENDING: "blue",
        ASSIGNED: "orange",
        IN_PROGRESS: "orange",
        RESOLVED: "green",
        REJECTED: "orange"
    };

    return colors[status] || "blue";
}

// 10. Format date
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

// 11. Logout user
async function logoutUser() {
    try {
        const response = await fetch("/api/users/logout", {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error("Logout failed.");
        }

        window.location.href = "login.html";

    } catch (error) {
        console.error("Logout error:", error);
        alert("Unable to logout. Please try again.");
    }
}

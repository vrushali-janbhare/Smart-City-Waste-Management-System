
document.addEventListener("DOMContentLoaded", function () {

    const API_BASE = "/api";

    const trackForm = document.getElementById("trackComplaintForm");
    const complaintIdInput = document.getElementById("complaintId");
    const trackBtn = document.getElementById("trackBtn");
    const messageBox = document.getElementById("messageBox");
    const complaintResult = document.getElementById("complaintResult");
    const logoutBtn = document.getElementById("logoutBtn");

    let areaList = [];

    // Check Login Session
    async function checkLoginSession() {

        try {

            const response = await fetch(API_BASE + "/users/session", {
                method: "GET",
                credentials: "same-origin"
            });

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

    // Show Message
    function showMessage(message, type) {

        messageBox.textContent = message;
        messageBox.className = "message-box " + type;
    }

    // Clear Message
    function clearMessage() {

        messageBox.textContent = "";
        messageBox.className = "message-box";
    }

    // Load Areas
    async function loadAreas() {

        try {

            const response = await fetch(API_BASE + "/areas", {
                method: "GET",
                credentials: "same-origin"
            });

            if (!response.ok) {
                throw new Error("Unable to load areas.");
            }

            const data = await response.json();

            areaList = Array.isArray(data) ? data : [];

        } catch (error) {

            console.error("Area loading error:", error);
            areaList = [];
        }
    }

    // Get Area Name
    function getAreaName(complaint) {

        // Check nested area object
        if (complaint.area) {

            if (complaint.area.areaName) {
                return complaint.area.areaName;
            }

            if (complaint.area.name) {
                return complaint.area.name;
            }

            // Match area ID with loaded area list
            const nestedAreaId = complaint.area.areaId;

            if (nestedAreaId != null) {

                const matchedArea = areaList.find(function (area) {
                    return String(area.areaId) === String(nestedAreaId);
                });

                if (matchedArea) {
                    return matchedArea.areaName;
                }
            }
        }

        // Check direct area name
        if (complaint.areaName) {
            return complaint.areaName;
        }

        // Check direct area ID
        if (complaint.areaId != null) {

            const matchedArea = areaList.find(function (area) {
                return String(area.areaId) === String(complaint.areaId);
            });

            if (matchedArea) {
                return matchedArea.areaName;
            }
        }

        return "Not available";
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

    // Format Status
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

    // Get Status CSS Class
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

    // Get Status Description
    function getStatusDescription(status) {

        switch (status) {

            case "PENDING":
                return "Your complaint has been submitted successfully and is waiting for further action.";

            case "ASSIGNED":
                return "A worker has been assigned to handle your complaint.";

            case "IN_PROGRESS":
                return "Work on your complaint is currently in progress.";

            case "RESOLVED":
                return "Your complaint has been resolved successfully.";

            case "REJECTED":
                return "Your complaint has been rejected. Please contact the administration for more information.";

            default:
                return "The current status of your complaint is not available.";
        }
    }

    // Display Complaint Details
    function displayComplaint(complaint) {

        document.getElementById("resultComplaintId").textContent =
            complaint.complaintId || "-";

        document.getElementById("categoryName").textContent =
            getCategoryName(complaint);

        document.getElementById("reportedDate").textContent =
            formatDate(complaint.createdAt);

        document.getElementById("description").textContent =
            complaint.description || "Not available";

        document.getElementById("locationAddress").textContent =
            complaint.locationAddress || "Not available";

        document.getElementById("areaName").textContent =
            getAreaName(complaint);

        // Resolved Date
        document.getElementById("resolvedDate").textContent =
            complaint.resolvedAt
                ? formatDate(complaint.resolvedAt)
                : "Not resolved yet";

        // Status
        const statusElement = document.getElementById("complaintStatus");

        statusElement.textContent = formatStatus(complaint.status);

        statusElement.className =
            "status " + getStatusClass(complaint.status);

        // Status Description
        document.getElementById("statusDescription").textContent =
            getStatusDescription(complaint.status);

        complaintResult.style.display = "block";
    }

    // Track Complaint
    async function trackComplaint(event) {

        event.preventDefault();

        clearMessage();
        complaintResult.style.display = "none";

        const complaintId = complaintIdInput.value.trim();

        if (!complaintId || Number(complaintId) <= 0) {

            showMessage("Please enter a valid complaint ID.", "error");
            return;
        }

        trackBtn.disabled = true;
        trackBtn.textContent = "Searching...";

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
                throw new Error("Unable to retrieve your complaints.");
            }

            const complaints = await response.json();

            if (!Array.isArray(complaints)) {
                throw new Error("Unexpected complaint data received.");
            }

            // Find complaint belonging to logged-in user
            const complaint = complaints.find(function (item) {

                return String(item.complaintId) === complaintId;
            });

            if (!complaint) {

                showMessage(
                    "No complaint found with this ID in your account.",
                    "error"
                );

                return;
            }

            displayComplaint(complaint);

            showMessage(
                "Complaint details loaded successfully.",
                "success"
            );

        } catch (error) {

            showMessage(
                error.message || "Something went wrong. Please try again.",
                "error"
            );

        } finally {

            trackBtn.disabled = false;
            trackBtn.textContent = "Track Complaint";
        }
    }

    // Logout
    logoutBtn.addEventListener("click", async function (event) {

        event.preventDefault();

        try {

            await fetch(API_BASE + "/users/logout", {
                method: "POST",
                credentials: "same-origin"
            });

        } catch (error) {

            console.error("Logout error:", error);
        }

        window.location.href = "login.html";
    });

    // Form Submit
    trackForm.addEventListener("submit", trackComplaint);

    // Initialize Page
    async function initializePage() {

        const isLoggedIn = await checkLoginSession();

        if (isLoggedIn) {
            await loadAreas();
        }
    }

    initializePage();

});

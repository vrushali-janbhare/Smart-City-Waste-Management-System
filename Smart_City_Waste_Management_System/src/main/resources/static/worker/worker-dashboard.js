
document.addEventListener("DOMContentLoaded", async function () {

    "use strict";

    // ==========================================
    // API ENDPOINTS
    // ==========================================

    const API = {
        session: "/api/users/session",
        profile: "/api/workers/me",
        updateProfile: "/api/workers/me/profile",
        dashboard: "/api/workers/me/dashboard",
        complaints: "/api/workers/me/complaints",
        schedules: "/api/workers/me/schedules",
        logout: "/api/users/logout"
    };

    const LOGIN_URL = "/login.html";

    // ==========================================
    // ELEMENTS
    // ==========================================

    const sidebar = document.getElementById("sidebar");
    const menuToggle = document.getElementById("menuToggle");
    const pageTitle = document.getElementById("pageTitle");
    const globalMessage = document.getElementById("globalMessage");

    const navLinks = document.querySelectorAll(".nav-link");
    const sections = document.querySelectorAll(".content-section");

    const logoutBtn = document.getElementById("logoutBtn");

    const viewAllComplaintsBtn =
        document.getElementById("viewAllComplaintsBtn");

    const complaintStatusFilter =
        document.getElementById("complaintStatusFilter");

    // Edit Profile elements
    const editProfileBtn =
        document.getElementById("editProfileBtn");

    const profileView =
        document.getElementById("profileView");

    const profileEditView =
        document.getElementById("profileEditView");

    const editProfileForm =
        document.getElementById("editProfileForm");

    const cancelEditProfileBtn =
        document.getElementById("cancelEditProfileBtn");

    const saveProfileBtn =
        document.getElementById("saveProfileBtn");

    const editProfileMessage =
        document.getElementById("editProfileMessage");

    const editFullName =
        document.getElementById("editFullName");

    const editPhoneNumber =
        document.getElementById("editPhoneNumber");

    const editAddress =
        document.getElementById("editAddress");

    const editEmail =
        document.getElementById("editEmail");

    // ==========================================
    // VARIABLES
    // ==========================================

    let myComplaints = [];
    let currentWorkerProfile = null;
    let messageTimeout = null;

    // ==========================================
    // COMMON FUNCTIONS
    // ==========================================

    function setText(id, value) {

        const element = document.getElementById(id);

        if (element) {
            element.textContent =
                value === null ||
                value === undefined ||
                value === ""
                    ? "—"
                    : String(value);
        }
    }

    function showMessage(text, type = "error") {

        if (messageTimeout) {
            clearTimeout(messageTimeout);
        }

        globalMessage.textContent = text;
        globalMessage.className = "global-message " + type;
        globalMessage.hidden = false;

        messageTimeout = setTimeout(function () {
            globalMessage.hidden = true;
        }, 5000);
    }

    function formatDate(dateValue) {

        if (!dateValue) {
            return "—";
        }

        const date = new Date(dateValue);

        if (isNaN(date.getTime())) {
            return String(dateValue);
        }

        return date.toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric"
        });
    }

    function formatTime(timeValue) {

        if (!timeValue) {
            return "—";
        }

        const parts = String(timeValue).split(":");

        const hours = Number(parts[0]);
        const minutes = Number(parts[1] || 0);

        if (isNaN(hours) || isNaN(minutes)) {
            return String(timeValue);
        }

        const period = hours >= 12 ? "PM" : "AM";
        const displayHours = hours % 12 || 12;

        return displayHours + ":" +
            String(minutes).padStart(2, "0") +
            " " + period;
    }

    function createCell(row, value) {

        const cell = document.createElement("td");

        cell.textContent =
            value === null ||
            value === undefined ||
            value === ""
                ? "—"
                : String(value);

        row.appendChild(cell);

        return cell;
    }

    function showTableMessage(tbody, message, colspan) {

        tbody.replaceChildren();

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = colspan;
        cell.className = "empty-state";
        cell.textContent = message;

        row.appendChild(cell);
        tbody.appendChild(row);
    }

    function createStatusBadge(status) {

        const badge = document.createElement("span");

        const statusText = String(status || "PENDING");

        badge.className =
            "status-badge status-" +
            statusText.toLowerCase().replaceAll("_", "-");

        badge.textContent =
            statusText.replaceAll("_", " ");

        return badge;
    }

    // ==========================================
    // API REQUEST HELPER
    // ==========================================

    async function apiRequest(url, options = {}) {

        const response = await fetch(url, {
            ...options,
            credentials: "same-origin",
            headers: {
                "Accept": "application/json",
                ...(options.headers || {})
            }
        });

        if (response.status === 401 ||
            response.status === 403) {

            window.location.replace(LOGIN_URL);

            throw new Error(
                "Your session is not authorized."
            );
        }

        const contentType =
            response.headers.get("content-type") || "";

        let result = null;

        if (contentType.includes("application/json")) {
            result = await response.json();
        } else {
            result = await response.text();
        }

        if (!response.ok) {

            const errorMessage =
                result && typeof result === "object"
                    ? result.message
                    : result;

            throw new Error(
                errorMessage || "Request failed."
            );
        }

        return result;
    }

    // ==========================================
    // CHECK WORKER SESSION
    // ==========================================

    async function checkWorkerSession() {

        try {

            const session =
                await apiRequest(API.session);

            const profile =
                await apiRequest(API.profile);

            if (!session || !profile) {

                window.location.replace(LOGIN_URL);
                return false;
            }

            displayProfile(profile);

            return true;

        } catch (error) {

            console.error("Session Error:", error);

            window.location.replace(LOGIN_URL);

            return false;
        }
    }

    // ==========================================
    // SIDEBAR NAVIGATION
    // ==========================================

    const sectionConfig = {

        dashboard: {
            id: "dashboardSection",
            title: "Dashboard Overview"
        },

        complaints: {
            id: "complaintsSection",
            title: "My Complaints"
        },

        schedules: {
            id: "schedulesSection",
            title: "My Schedule"
        },

        profile: {
            id: "profileSection",
            title: "My Profile"
        }
    };

    function openSection(sectionName) {

        const config = sectionConfig[sectionName];

        if (!config) {
            return;
        }

        sections.forEach(function (section) {
            section.classList.remove("active");
        });

        navLinks.forEach(function (link) {

            link.classList.toggle(
                "active",
                link.dataset.section === sectionName
            );
        });

        document.getElementById(config.id)
            .classList.add("active");

        pageTitle.textContent = config.title;

        sidebar.classList.remove("open");

        menuToggle.setAttribute(
            "aria-expanded",
            "false"
        );

        window.scrollTo(0, 0);
    }

    navLinks.forEach(function (link) {

        link.addEventListener("click", function () {

            openSection(link.dataset.section);

        });
    });

    menuToggle.addEventListener("click", function () {

        const isOpen =
            sidebar.classList.toggle("open");

        menuToggle.setAttribute(
            "aria-expanded",
            String(isOpen)
        );
    });

    viewAllComplaintsBtn.addEventListener(
        "click",
        function () {

            openSection("complaints");
        }
    );

    // ==========================================
    // WORKER PROFILE
    // ==========================================

    function displayProfile(profile) {

        // Save current profile for editing
        currentWorkerProfile = profile;

        const name = profile.fullName || "Worker";

        setText("headerWorkerName", name);
        setText("welcomeWorkerName", name);
        setText("profileWorkerName", name);
        setText("profileFullName", name);

        setText("profileEmail", profile.email);
        setText("profilePhone", profile.phoneNumber);
        setText("profileAddress", profile.address);

        setText("profileWorkerId", profile.workerId);

        setText(
            "profileEmployeeCode",
            profile.employeeCode
        );

        setText(
            "profileJoiningDate",
            formatDate(profile.joiningDate)
        );

        const initial =
            name.charAt(0).toUpperCase();

        const headerAvatar =
            document.querySelector(".worker-avatar");

        if (headerAvatar) {
            headerAvatar.textContent = initial;
        }

        setText("profileAvatar", initial);
    }

    // ==========================================
    // EDIT PROFILE - MESSAGE
    // ==========================================

    function showEditProfileMessage(message, type) {

        editProfileMessage.textContent = message;

        editProfileMessage.className = type;

        editProfileMessage.hidden = false;
    }

    // ==========================================
    // EDIT PROFILE - OPEN FORM
    // ==========================================

    editProfileBtn.addEventListener(
        "click",
        function () {

            if (!currentWorkerProfile) {

                showMessage(
                    "Worker profile is not loaded yet."
                );

                return;
            }

            // Fill form with existing profile data
            editFullName.value =
                currentWorkerProfile.fullName || "";

            editPhoneNumber.value =
                currentWorkerProfile.phoneNumber || "";

            editAddress.value =
                currentWorkerProfile.address || "";

            editEmail.value =
                currentWorkerProfile.email || "";

            editProfileMessage.hidden = true;

            // Hide profile and show edit form
            profileView.hidden = true;
            profileEditView.hidden = false;

            editFullName.focus();
        }
    );

    // ==========================================
    // EDIT PROFILE - CANCEL
    // ==========================================

    cancelEditProfileBtn.addEventListener(
        "click",
        function () {

            editProfileForm.reset();

            editProfileMessage.hidden = true;

            profileEditView.hidden = true;
            profileView.hidden = false;
        }
    );

    // ==========================================
    // EDIT PROFILE - SAVE CHANGES
    // ==========================================

    editProfileForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const fullName =
                editFullName.value.trim();

            const phoneNumber =
                editPhoneNumber.value.trim();

            const address =
                editAddress.value.trim();

            // Validate required fields
            if (!fullName || !phoneNumber || !address) {

                showEditProfileMessage(
                    "All fields are required.",
                    "error"
                );

                return;
            }

            // Validate full name
            if (fullName.length > 100) {

                showEditProfileMessage(
                    "Full name cannot exceed 100 characters.",
                    "error"
                );

                return;
            }

            // Validate phone number
            if (!/^[0-9]{10}$/.test(phoneNumber)) {

                showEditProfileMessage(
                    "Enter a valid 10-digit phone number.",
                    "error"
                );

                return;
            }

            // Validate address
            if (address.length > 255) {

                showEditProfileMessage(
                    "Address cannot exceed 255 characters.",
                    "error"
                );

                return;
            }

            saveProfileBtn.disabled = true;
            saveProfileBtn.textContent = "Saving...";

            try {

                // Send profile details to Spring Boot
                const updatedProfile = await apiRequest(
                    API.updateProfile,
                    {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({
                            fullName: fullName,
                            phoneNumber: phoneNumber,
                            address: address
                        })
                    }
                );

                // Update profile on page
                displayProfile(updatedProfile);

                // Close edit form
                profileEditView.hidden = true;
                profileView.hidden = false;

                editProfileMessage.hidden = true;

                // Show success message
                showMessage(
                    "Profile updated successfully!",
                    "success"
                );

            } catch (error) {

                console.error(
                    "Edit Profile Error:",
                    error
                );

                showEditProfileMessage(
                    error.message ||
                    "Unable to update profile.",
                    "error"
                );

            } finally {

                saveProfileBtn.disabled = false;

                saveProfileBtn.textContent =
                    "Save Changes";
            }
        }
    );

    // ==========================================
    // DASHBOARD STATISTICS
    // ==========================================

    async function loadDashboardStats() {

        try {

            const stats =
                await apiRequest(API.dashboard);

            setText(
                "totalComplaints",
                stats.totalComplaints ?? 0
            );

            setText(
                "assignedComplaints",
                stats.assignedComplaints ?? 0
            );

            setText(
                "inProgressComplaints",
                stats.inProgressComplaints ?? 0
            );

            setText(
                "resolvedComplaints",
                stats.resolvedComplaints ?? 0
            );

        } catch (error) {

            console.error(
                "Dashboard Error:",
                error
            );

            showMessage(
                "Unable to load dashboard statistics."
            );
        }
    }

    // ==========================================
    // RECENT COMPLAINTS
    // ==========================================

    function renderRecentComplaints() {

        const tbody =
            document.getElementById(
                "recentComplaintsBody"
            );

        tbody.replaceChildren();

        const recent =
            myComplaints.slice(0, 5);

        if (recent.length === 0) {

            showTableMessage(
                tbody,
                "No complaints assigned yet.",
                4
            );

            return;
        }

        recent.forEach(function (complaint) {

            const row =
                document.createElement("tr");

            createCell(
                row,
                "#" + complaint.complaintId
            );

            createCell(
                row,
                complaint.description
            );

            createCell(
                row,
                complaint.locationAddress
            );

            const statusCell =
                document.createElement("td");

            statusCell.appendChild(
                createStatusBadge(complaint.status)
            );

            row.appendChild(statusCell);

            tbody.appendChild(row);
        });
    }

    // ==========================================
    // MY COMPLAINTS TABLE
    // ==========================================

    function renderComplaints() {

        const tbody =
            document.getElementById(
                "complaintsTableBody"
            );

        tbody.replaceChildren();

        const selectedStatus =
            complaintStatusFilter.value;

        const filteredComplaints =
            selectedStatus === "ALL"
                ? myComplaints
                : myComplaints.filter(
                    function (complaint) {

                        return complaint.status ===
                            selectedStatus;
                    }
                );

        if (filteredComplaints.length === 0) {

            showTableMessage(
                tbody,
                "No complaints found.",
                7
            );

            return;
        }

        filteredComplaints.forEach(
            function (complaint) {

                const row =
                    document.createElement("tr");

                createCell(
                    row,
                    "#" + complaint.complaintId
                );

                createCell(
                    row,
                    complaint.description
                );

                createCell(
                    row,
                    complaint.categoryName || "—"
                );

                createCell(
                    row,
                    complaint.locationAddress
                );

                createCell(
                    row,
                    formatDate(complaint.createdAt)
                );

                const statusCell =
                    document.createElement("td");

                statusCell.appendChild(
                    createStatusBadge(complaint.status)
                );

                row.appendChild(statusCell);

                const actionCell =
                    document.createElement("td");

                let nextStatus = null;
                let buttonText = "";

                if (complaint.status === "ASSIGNED") {

                    nextStatus = "IN_PROGRESS";
                    buttonText = "Start Work";

                } else if (
                    complaint.status === "IN_PROGRESS"
                ) {

                    nextStatus = "RESOLVED";
                    buttonText = "Mark Resolved";
                }

                if (nextStatus) {

                    const button =
                        document.createElement("button");

                    button.type = "button";
                    button.className = "action-btn";
                    button.textContent = buttonText;

                    button.addEventListener(
                        "click",
                        function () {

                            updateComplaintStatus(
                                complaint.complaintId,
                                nextStatus,
                                button
                            );
                        }
                    );

                    actionCell.appendChild(button);

                } else {

                    actionCell.textContent = "—";
                }

                row.appendChild(actionCell);

                tbody.appendChild(row);
            }
        );
    }

    complaintStatusFilter.addEventListener(
        "change",
        renderComplaints
    );

    // ==========================================
    // LOAD ASSIGNED COMPLAINTS
    // ==========================================

    async function loadMyComplaints() {

        try {

            const complaints =
                await apiRequest(API.complaints);

            myComplaints =
                Array.isArray(complaints)
                    ? complaints
                    : [];

            renderComplaints();
            renderRecentComplaints();

        } catch (error) {

            console.error(
                "Complaints Error:",
                error
            );

            showTableMessage(
                document.getElementById(
                    "complaintsTableBody"
                ),
                "Unable to load complaints.",
                7
            );

            showTableMessage(
                document.getElementById(
                    "recentComplaintsBody"
                ),
                "Unable to load complaints.",
                4
            );
        }
    }

    // ==========================================
    // UPDATE COMPLAINT STATUS
    // ==========================================

    async function updateComplaintStatus(
        complaintId,
        newStatus,
        button
    ) {

        const actionDescription =
            newStatus === "IN_PROGRESS"
                ? "start work on"
                : "mark as resolved";

        const confirmed = window.confirm(
            "Are you sure you want to " +
            actionDescription +
            " complaint #" +
            complaintId +
            "?"
        );

        if (!confirmed) {
            return;
        }

        button.disabled = true;
        button.textContent = "Updating...";

        try {

            await apiRequest(
                API.complaints +
                "/" +
                complaintId +
                "/status",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        status: newStatus
                    })
                }
            );

            showMessage(
                "Complaint #" +
                complaintId +
                " updated successfully.",
                "success"
            );

            await Promise.all([
                loadMyComplaints(),
                loadDashboardStats()
            ]);

        } catch (error) {

            console.error(
                "Status Update Error:",
                error
            );

            showMessage(error.message);

            button.disabled = false;

            button.textContent =
                newStatus === "IN_PROGRESS"
                    ? "Start Work"
                    : "Mark Resolved";
        }
    }

    // ==========================================
    // MY COLLECTION SCHEDULES
    // ==========================================

    async function loadMySchedules() {

        const tbody =
            document.getElementById(
                "schedulesTableBody"
            );

        try {

            const schedules =
                await apiRequest(API.schedules);

            tbody.replaceChildren();

            if (!Array.isArray(schedules) ||
                schedules.length === 0) {

                showTableMessage(
                    tbody,
                    "No collection schedules assigned yet.",
                    7
                );

                return;
            }

            schedules.forEach(
                function (schedule) {

                    const row =
                        document.createElement("tr");

                    createCell(
                        row,
                        schedule.scheduleName
                    );

                    createCell(
                        row,
                        schedule.areaName
                    );

                    createCell(
                        row,
                        schedule.vehicleNumber
                    );

                    createCell(
                        row,
                        formatDate(schedule.startDate)
                    );

                    createCell(
                        row,
                        formatDate(schedule.endDate)
                    );

                    const timeText =
                        formatTime(schedule.startTime) +
                        " - " +
                        formatTime(schedule.endTime);

                    createCell(row, timeText);

                    const statusCell =
                        document.createElement("td");

                    statusCell.appendChild(
                        createStatusBadge(
                            schedule.scheduleStatus ||
                            "PENDING"
                        )
                    );

                    row.appendChild(statusCell);

                    tbody.appendChild(row);
                }
            );

        } catch (error) {

            console.error(
                "Schedules Error:",
                error
            );

            showTableMessage(
                tbody,
                "Unable to load collection schedules.",
                7
            );

            showMessage(
                "Unable to load collection schedules."
            );
        }
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    logoutBtn.addEventListener(
        "click",
        async function () {

            const confirmed = window.confirm(
                "Are you sure you want to logout?"
            );

            if (!confirmed) {
                return;
            }

            logoutBtn.disabled = true;
            logoutBtn.textContent = "Logging out...";

            try {

                await apiRequest(API.logout, {
                    method: "POST"
                });

                window.location.replace(LOGIN_URL);

            } catch (error) {

                console.error(
                    "Logout Error:",
                    error
                );

                showMessage(
                    "Unable to logout. Please try again."
                );

                logoutBtn.disabled = false;
                logoutBtn.textContent = "Logout";
            }
        }
    );

    // ==========================================
    // INITIALIZE WORKER DASHBOARD
    // ==========================================

    async function initializeDashboard() {

        const authorized =
            await checkWorkerSession();

        if (!authorized) {
            return;
        }

        openSection("dashboard");

        await Promise.all([
            loadDashboardStats(),
            loadMyComplaints(),
            loadMySchedules()
        ]);
    }

    initializeDashboard();

});

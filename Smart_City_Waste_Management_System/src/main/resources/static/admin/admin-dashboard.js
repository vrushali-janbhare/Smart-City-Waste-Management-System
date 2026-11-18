
document.addEventListener("DOMContentLoaded", function () {

    // ==========================================
    // E-SAFAI ADMIN DASHBOARD
    // ==========================================

    const sidebar = document.getElementById("adminSidebar");
    const menuToggle = document.getElementById("menuToggle");
    const pageTitle = document.getElementById("pageTitle");

    const navLinks = document.querySelectorAll(
        ".sidebar-nav .nav-link[data-section]"
    );

    const sections = document.querySelectorAll(
        ".dashboard-section"
    );

    // ==========================================
    // 1. SIDEBAR NAVIGATION
    // ==========================================

    function closeSidebar() {
        if (sidebar) {
            sidebar.classList.remove("sidebar-open");
        }

        if (menuToggle) {
            menuToggle.setAttribute("aria-expanded", "false");
        }
    }

    function showSection(sectionId) {
        const selected = document.getElementById(sectionId);

        if (!selected ||
            !selected.classList.contains("dashboard-section")) {
            return;
        }

        sections.forEach(section => {
            section.classList.remove("active-section");
        });

        selected.classList.add("active-section");

        navLinks.forEach(link => {
            const active = link.dataset.section === sectionId;
            link.classList.toggle("active", active);

            if (active && pageTitle) {
                const title = link.querySelector("span");
                if (title) {
                    pageTitle.textContent = title.textContent.trim();
                }
            }
        });

        closeSidebar();
        window.scrollTo(0, 0);

        if (sectionId === "complaints") {
            loadManageComplaints();
        }

        if (sectionId === "citizens") {
            loadCitizens();
        }
    }

    navLinks.forEach(link => {
        link.addEventListener("click", function (event) {
            const sectionId = link.dataset.section;

            if (!document.getElementById(sectionId)) {
                return;
            }

            event.preventDefault();
            showSection(sectionId);

            if (window.location.hash !== "#" + sectionId) {
                history.pushState(null, "", "#" + sectionId);
            }
        });
    });

    if (menuToggle && sidebar) {
        menuToggle.addEventListener("click", function () {
            const open = sidebar.classList.toggle("sidebar-open");
            menuToggle.setAttribute("aria-expanded", String(open));
        });
    }

    window.addEventListener("hashchange", function () {
        showSection(window.location.hash.substring(1) || "dashboard");
    });

    window.addEventListener("popstate", function () {
        showSection(window.location.hash.substring(1) || "dashboard");
    });

    // ==========================================
    // 2. COMMON HELPERS
    // ==========================================

    async function apiRequest(url, options = {}) {
        const response = await fetch(url, {
            credentials: "same-origin",
            ...options
        });

        if (!response.ok) {
            let message = "Request failed (" + response.status + ")";

            try {
                const data = await response.json();

                if (typeof data === "string") {
                    message = data;
                } else {
                    message = data.message ||
                        data.error ||
                        message;
                }
            } catch (error) {
                // Keep default error message.
            }

            throw new Error(message);
        }

        if (response.status === 204) {
            return null;
        }

        const contentType =
            response.headers.get("content-type") || "";

        if (contentType.includes("application/json")) {
            return response.json();
        }

        return response.text();
    }

    function setText(id, value) {
        const element = document.getElementById(id);

        if (element) {
            element.textContent = value ?? "-";
        }
    }

    // Use this helper for input fields, including hidden inputs.
    function setInputValue(id, value) {
        const element = document.getElementById(id);

        if (element) {
            element.value = value ?? "";
        }
    }

    function formatDate(value) {
        if (!value) {
            return "-";
        }

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return String(value);
        }

        return date.toLocaleString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function createBadge(status) {
        const badge = document.createElement("span");
        badge.className = "badge";

        const colors = {
            PENDING: "bg-warning text-dark",
            ASSIGNED: "bg-info text-dark",
            IN_PROGRESS: "bg-primary",
            RESOLVED: "bg-success",
            REJECTED: "bg-danger"
        };

        const normalized = status || "UNKNOWN";

        badge.textContent = normalized.replace(/_/g, " ");

        (colors[normalized] || "bg-secondary")
            .split(" ")
            .forEach(cls => badge.classList.add(cls));

        return badge;
    }

    function createButton(label, className, callback) {
        const button = document.createElement("button");

        button.type = "button";
        button.className = className;
        button.textContent = label;
        button.addEventListener("click", callback);

        return button;
    }

    function showTableMessage(body, colspan, message, isError = false) {
        if (!body) {
            return;
        }

        body.replaceChildren();

        const row = body.insertRow();
        const cell = row.insertCell();

        cell.colSpan = colspan;
        cell.className = isError
            ? "text-center text-danger py-4"
            : "text-center text-muted py-4";

        cell.textContent = message;
    }

    function showModal(id) {
        const element = document.getElementById(id);

        if (!element || typeof bootstrap === "undefined") {
            console.error("Bootstrap modal unavailable:", id);
            return;
        }

        bootstrap.Modal.getOrCreateInstance(element).show();
    }

    function hideModal(id) {
        const element = document.getElementById(id);

        if (element && typeof bootstrap !== "undefined") {
            bootstrap.Modal.getOrCreateInstance(element).hide();
        }
    }

    function showComplaintMessage(message, success = true) {
        const element = document.getElementById(
            "complaintActionMessage"
        );

        if (!element) {
            return;
        }

        element.className = success
            ? "alert alert-success"
            : "alert alert-danger";

        element.textContent = message;
    }

    // ==========================================
    // 3. DASHBOARD STATISTICS
    // ==========================================

    async function loadDashboardStatistics() {
        try {
            const data = await apiRequest(
                "/api/admin/dashboard/stats"
            );

            [
                "totalCitizens",
                "totalWorkers",
                "totalComplaints",
                "pendingComplaints",
                "resolvedComplaints",
                "totalBulkPickups"
            ].forEach(id => {
                if (data[id] !== undefined) {
                    setText(id, data[id]);
                }
            });

        } catch (error) {
            console.error("Dashboard statistics error:", error);
        }
    }

    // ==========================================
    // 4. COMPLAINT STATISTICS CHART
    // ==========================================

    let complaintChartInstance = null;

    async function loadComplaintChart() {
        const canvas = document.getElementById("complaintChart");
        const message = document.getElementById("chartMessage");

        if (!canvas) {
            return;
        }

        if (typeof Chart === "undefined") {
            if (message) {
                message.textContent = "Chart.js is not loaded.";
            }
            return;
        }

        try {
            const data = await apiRequest(
                "/api/admin/dashboard/complaint-chart"
            );

            const labels = [
                "Pending",
                "Assigned",
                "In Progress",
                "Resolved",
                "Rejected"
            ];

            const values = [
                data.PENDING || 0,
                data.ASSIGNED || 0,
                data.IN_PROGRESS || 0,
                data.RESOLVED || 0,
                data.REJECTED || 0
            ];

            if (complaintChartInstance) {
                complaintChartInstance.destroy();
            }

            complaintChartInstance = new Chart(canvas, {
                type: "doughnut",
                data: {
                    labels: labels,
                    datasets: [{
                        label: "Complaints",
                        data: values,
                        backgroundColor: [
                            "#f59e0b",
                            "#3b82f6",
                            "#8b5cf6",
                            "#10b981",
                            "#ef4444"
                        ],
                        borderWidth: 2,
                        borderColor: "#ffffff"
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: "bottom"
                        }
                    }
                }
            });

            if (message) {
                message.style.display = "none";
            }

        } catch (error) {
            console.error("Complaint chart error:", error);

            if (message) {
                message.textContent =
                    "Unable to load complaint statistics.";
                message.style.display = "block";
            }
        }
    }

    // ==========================================
    // 5. RECENT COMPLAINTS
    // ==========================================

    async function loadRecentComplaints() {
        const body = document.getElementById(
            "recentComplaintsTable"
        );

        if (!body) {
            return;
        }

        try {
            const complaints = await apiRequest(
                "/api/admin/dashboard/recent-complaints"
            );

            body.replaceChildren();

            if (!complaints.length) {
                showTableMessage(body, 6, "No complaints found.");
                return;
            }

            complaints.forEach(complaint => {
                const row = body.insertRow();

                row.insertCell().textContent =
                    complaint.complaintId ?? "-";

                row.insertCell().textContent =
                    complaint.citizenName || "Unknown";

                row.insertCell().textContent =
                    complaint.categoryName || "-";

                row.insertCell().textContent =
                    complaint.locationAddress || "-";

                row.insertCell().appendChild(
                    createBadge(complaint.status)
                );

                const actionCell = row.insertCell();

                actionCell.appendChild(
                    createButton(
                        "View",
                        "btn btn-sm btn-outline-success",
                        function () {
                            showSection("complaints");

                            history.pushState(
                                null,
                                "",
                                "#complaints"
                            );

                            openComplaintDetails(
                                complaint.complaintId
                            );
                        }
                    )
                );
            });

        } catch (error) {
            console.error("Recent complaints error:", error);

            showTableMessage(
                body,
                6,
                "Unable to load recent complaints.",
                true
            );
        }
    }

    // ==========================================
    // 6. MANAGE CITIZENS
    // ==========================================

    let allCitizens = [];

    async function loadCitizens() {
        const body = document.getElementById(
            "citizensTableBody"
        );

        if (!body) {
            return;
        }

        try {
            allCitizens = await apiRequest(
                "/api/admin/citizens"
            );

            setText("citizenCount", allCitizens.length);

            displayCitizens(allCitizens);

        } catch (error) {
            console.error("Citizens loading error:", error);

            showTableMessage(
                body,
                6,
                "Unable to load citizens.",
                true
            );
        }
    }

    function displayCitizens(citizens) {
        const body = document.getElementById(
            "citizensTableBody"
        );

        if (!body) {
            return;
        }

        body.replaceChildren();

        if (!citizens.length) {
            showTableMessage(body, 6, "No citizens found.");
            return;
        }

        citizens.forEach(citizen => {
            const row = body.insertRow();

            row.insertCell().textContent =
                citizen.userId ?? "-";

            row.insertCell().textContent =
                citizen.fullName || "-";

            row.insertCell().textContent =
                citizen.email || "-";

            row.insertCell().textContent =
                citizen.phoneNumber || "-";

            row.insertCell().textContent =
                citizen.address || "-";

            row.insertCell().appendChild(
                createButton(
                    "View",
                    "btn btn-sm btn-outline-primary",
                    function () {
                        setText(
                            "modalCitizenId",
                            citizen.userId
                        );

                        setText(
                            "modalCitizenName",
                            citizen.fullName
                        );

                        setText(
                            "modalCitizenEmail",
                            citizen.email
                        );

                        setText(
                            "modalCitizenPhone",
                            citizen.phoneNumber
                        );

                        setText(
                            "modalCitizenAddress",
                            citizen.address
                        );

                        showModal("citizenDetailsModal");
                    }
                )
            );
        });
    }

    const citizenSearch = document.getElementById(
        "citizenSearch"
    );

    if (citizenSearch) {
        citizenSearch.addEventListener("input", function () {
            const query = citizenSearch.value
                .toLowerCase()
                .trim();

            const filtered = allCitizens.filter(citizen => {
                return [
                    citizen.fullName,
                    citizen.email,
                    citizen.phoneNumber
                ].some(value =>
                    String(value || "")
                        .toLowerCase()
                        .includes(query)
                );
            });

            displayCitizens(filtered);
        });
    }

    // ==========================================
    // 7. MANAGE COMPLAINTS
    // ==========================================

    let allComplaints = [];
    let availableWorkers = [];

    const complaintSearch = document.getElementById(
        "complaintSearch"
    );

    const complaintStatusFilter = document.getElementById(
        "complaintStatusFilter"
    );

    const complaintsBody = document.getElementById(
        "complaintsTableBody"
    );

    async function loadManageComplaints() {
        if (!complaintsBody) {
            return;
        }

        try {
            const complaints = await apiRequest(
                "/api/admin/complaints"
            );

            allComplaints = Array.isArray(complaints)
                ? complaints
                : [];

            updateComplaintStatistics();
            displayComplaints();

        } catch (error) {
            console.error("Manage Complaints error:", error);

            showTableMessage(
                complaintsBody,
                6,
                "Unable to load complaints: " + error.message,
                true
            );

            showComplaintMessage(error.message, false);
        }
    }

    function updateComplaintStatistics() {
        setText(
            "manageTotalComplaints",
            allComplaints.length
        );

        setText(
            "managePendingComplaints",
            allComplaints.filter(
                complaint => complaint.status === "PENDING"
            ).length
        );

        setText(
            "manageAssignedComplaints",
            allComplaints.filter(
                complaint => complaint.status === "ASSIGNED"
            ).length
        );

        setText(
            "manageResolvedComplaints",
            allComplaints.filter(
                complaint => complaint.status === "RESOLVED"
            ).length
        );
    }

    function displayComplaints() {
        if (!complaintsBody) {
            return;
        }

        const searchText = (complaintSearch?.value || "")
            .toLowerCase()
            .trim();

        const selectedStatus =
            complaintStatusFilter?.value || "ALL";

        const filtered = allComplaints.filter(complaint => {
            const matchesStatus =
                selectedStatus === "ALL" ||
                complaint.status === selectedStatus;

            const searchable = [
                complaint.complaintId,
                complaint.categoryName,
                complaint.areaName,
                complaint.locationAddress,
                complaint.description
            ].join(" ").toLowerCase();

            return matchesStatus &&
                searchable.includes(searchText);
        });

        complaintsBody.replaceChildren();

        setText(
            "complaintResultsCount",
            "Showing " + filtered.length +
            " of " + allComplaints.length + " complaints"
        );

        if (!filtered.length) {
            showTableMessage(
                complaintsBody,
                6,
                "No matching complaints found."
            );
            return;
        }

        filtered.forEach(complaint => {
            const row = complaintsBody.insertRow();

            row.insertCell().textContent =
                complaint.complaintId ?? "-";

            row.insertCell().textContent =
                complaint.categoryName || "-";

            row.insertCell().textContent =
                complaint.locationAddress ||
                complaint.areaName ||
                "-";

            row.insertCell().appendChild(
                createBadge(complaint.status)
            );

            row.insertCell().textContent =
                formatDate(complaint.createdAt);

            const actionCell = row.insertCell();
            const actions = document.createElement("div");

            actions.className = "complaint-actions";

            actions.appendChild(
                createButton(
                    "View",
                    "btn btn-sm btn-outline-primary",
                    function () {
                        openComplaintDetails(
                            complaint.complaintId
                        );
                    }
                )
            );

            if (
                complaint.status === "PENDING" ||
                complaint.status === "ASSIGNED"
            ) {
                actions.appendChild(
                    createButton(
                        complaint.status === "ASSIGNED"
                            ? "Reassign"
                            : "Assign",
                        "btn btn-sm btn-outline-success",
                        function () {
                            openAssignWorker(
                                complaint.complaintId
                            );
                        }
                    )
                );
            }

            if (complaint.status === "PENDING") {
                actions.appendChild(
                    createButton(
                        "Reject",
                        "btn btn-sm btn-outline-danger",
                        function () {
                            openRejectComplaint(
                                complaint.complaintId
                            );
                        }
                    )
                );
            }

            actionCell.appendChild(actions);
        });
    }

    complaintSearch?.addEventListener(
        "input",
        displayComplaints
    );

    complaintStatusFilter?.addEventListener(
        "change",
        displayComplaints
    );

    document.getElementById(
        "refreshComplaintsBtn"
    )?.addEventListener("click", loadManageComplaints);

    // ==========================================
    // 8. VIEW COMPLAINT DETAILS
    // ==========================================

    async function openComplaintDetails(complaintId) {
        try {
            const complaint = await apiRequest(
                "/api/admin/complaints/" + complaintId
            );

            setText(
                "modalComplaintId",
                complaint.complaintId
            );

            setText(
                "modalComplaintCategory",
                complaint.categoryName
            );

            setText(
                "modalComplaintArea",
                complaint.areaName
            );

            setText(
                "modalComplaintLocation",
                complaint.locationAddress
            );

            setText(
                "modalComplaintDescription",
                complaint.description
            );

            setText(
                "modalComplaintStatus",
                (complaint.status || "-").replace(/_/g, " ")
            );

            setText(
                "modalComplaintCreatedAt",
                formatDate(complaint.createdAt)
            );

            setText(
                "modalComplaintResolvedAt",
                formatDate(complaint.resolvedAt)
            );

            const coordinates =
                complaint.latitude != null &&
                complaint.longitude != null
                    ? complaint.latitude + ", " +
                      complaint.longitude
                    : "-";

            setText(
                "modalComplaintCoordinates",
                coordinates
            );

            showModal("complaintDetailsModal");

        } catch (error) {
            showComplaintMessage(
                "Unable to load complaint: " + error.message,
                false
            );
        }
    }

    // ==========================================
    // 9. ASSIGN / REASSIGN WORKER
    // ==========================================

    async function openAssignWorker(complaintId) {

        // FIX: Hidden input must use value, not textContent.
        setInputValue("assignComplaintId", complaintId);

        setText("assignComplaintIdText", complaintId);
        setText("assignWorkerMessage", "");

        const select = document.getElementById(
            "assignWorkerSelect"
        );

        if (!select) {
            return;
        }

        select.replaceChildren();

        const placeholder = document.createElement("option");
        placeholder.value = "";
        placeholder.textContent = "Loading workers...";
        select.appendChild(placeholder);

        try {
            availableWorkers = await apiRequest(
                "/api/admin/complaints/workers"
            );

            select.replaceChildren();

            const firstOption = document.createElement("option");
            firstOption.value = "";
            firstOption.textContent = "Select a worker";
            select.appendChild(firstOption);

            availableWorkers.forEach(worker => {
                const option = document.createElement("option");

                option.value = worker.workerId;

                const workerName =
                    worker.fullName ||
                    worker.user?.fullName ||
                    "Worker";

                option.textContent =
                    workerName +
                    " (" +
                    (worker.employeeCode || "ID " + worker.workerId) +
                    ")";

                select.appendChild(option);
            });

            if (!availableWorkers.length) {
                setText(
                    "assignWorkerMessage",
                    "No registered workers found."
                );
            }

            showModal("assignWorkerModal");

        } catch (error) {
            showComplaintMessage(
                "Unable to load workers: " + error.message,
                false
            );
        }
    }

    document.getElementById(
        "assignWorkerForm"
    )?.addEventListener("submit", async function (event) {
        event.preventDefault();

        const complaintId = document.getElementById(
            "assignComplaintId"
        ).value;

        const workerId = document.getElementById(
            "assignWorkerSelect"
        ).value;

        const button = document.getElementById(
            "confirmAssignWorkerBtn"
        );

        // FIX: Validate the complaint ID before sending PUT.
        if (!complaintId || !/^\d+$/.test(complaintId)) {
            setText(
                "assignWorkerMessage",
                "Invalid complaint ID. Please reopen Assign Worker."
            );
            return;
        }

        if (!workerId) {
            setText(
                "assignWorkerMessage",
                "Please select a worker."
            );
            return;
        }

        if (!button) {
            return;
        }

        button.disabled = true;

        try {
            await apiRequest(
                "/api/admin/complaints/" +
                encodeURIComponent(complaintId) +
                "/assign",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        workerId: Number(workerId)
                    })
                }
            );

            hideModal("assignWorkerModal");

            await refreshComplaintData();

            showComplaintMessage(
                "Worker assigned successfully."
            );

        } catch (error) {
            setText(
                "assignWorkerMessage",
                error.message
            );
        } finally {
            button.disabled = false;
        }
    });

    // ==========================================
    // 10. REJECT COMPLAINT
    // ==========================================

    function openRejectComplaint(complaintId) {

        // FIX: Hidden input must use value, not textContent.
        setInputValue("rejectComplaintId", complaintId);

        setText("rejectComplaintIdText", complaintId);
        setText("rejectComplaintMessage", "");

        const reason = document.getElementById(
            "rejectReason"
        );

        if (reason) {
            reason.value = "";
        }

        showModal("rejectComplaintModal");
    }

    document.getElementById(
        "rejectComplaintForm"
    )?.addEventListener("submit", async function (event) {
        event.preventDefault();

        const complaintId = document.getElementById(
            "rejectComplaintId"
        ).value;

        const reason = document.getElementById(
            "rejectReason"
        ).value.trim();

        const button = document.getElementById(
            "confirmRejectComplaintBtn"
        );

        // FIX: Validate complaint ID before sending PUT.
        if (!complaintId || !/^\d+$/.test(complaintId)) {
            setText(
                "rejectComplaintMessage",
                "Invalid complaint ID. Please reopen Reject Complaint."
            );
            return;
        }

        if (!reason || reason.length > 255) {
            setText(
                "rejectComplaintMessage",
                "Enter a reason of up to 255 characters."
            );
            return;
        }

        if (!window.confirm(
            "Are you sure you want to reject complaint #" +
            complaintId + "?"
        )) {
            return;
        }

        if (!button) {
            return;
        }

        button.disabled = true;

        try {
            await apiRequest(
                "/api/admin/complaints/" +
                encodeURIComponent(complaintId) +
                "/reject",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        reason: reason
                    })
                }
            );

            hideModal("rejectComplaintModal");

            await refreshComplaintData();

            showComplaintMessage(
                "Complaint rejected successfully."
            );

        } catch (error) {
            setText(
                "rejectComplaintMessage",
                error.message
            );
        } finally {
            button.disabled = false;
        }
    });

    // ==========================================
    // 11. REFRESH AFTER COMPLAINT ACTION
    // ==========================================

    async function refreshComplaintData() {
        await loadManageComplaints();

        await Promise.all([
            loadDashboardStatistics(),
            loadComplaintChart(),
            loadRecentComplaints()
        ]);
    }

    // ==========================================
    // 12. INITIAL PAGE LOAD
    // ==========================================

    const initialSection =
        window.location.hash.substring(1) || "dashboard";

    if (
        document.getElementById(initialSection)
            ?.classList.contains("dashboard-section")
    ) {
        showSection(initialSection);
    } else {
        showSection("dashboard");
    }

   
    loadDashboardStatistics();
    loadComplaintChart();
    loadRecentComplaints();
    loadCitizens();

    // ==========================================
    // ADMIN LOGOUT
    // ==========================================

    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", async function () {

            logoutBtn.disabled = true;

            try {
                const response = await fetch("/api/users/logout", {
                    method: "POST",
                    credentials: "same-origin"
                });

                if (!response.ok) {
                    throw new Error("Logout failed");
                }

                window.location.replace("/login.html");

            } catch (error) {
                console.error("Logout error:", error);
                alert("Unable to logout. Please try again.");
                logoutBtn.disabled = false;
            }
        });
    }

});

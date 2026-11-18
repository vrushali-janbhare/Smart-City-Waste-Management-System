
document.addEventListener("DOMContentLoaded", function () {

    // ==========================================
    // E-SAFAI - ADMIN MANAGE WORKERS
    // ==========================================

    const API_URL = "/api/workers";

    let allWorkers = [];

    const workersTableBody =
        document.getElementById("workersTableBody");

    const workerSearch =
        document.getElementById("workerSearch");


    // ==========================================
    // 1. COMMON HELPER FUNCTIONS
    // ==========================================

    async function workerApiRequest(url, options = {}) {

        const response = await fetch(url, {
            ...options,
            credentials: "same-origin"
        });

        const contentType =
            response.headers.get("content-type") || "";

        let data;

        if (contentType.includes("application/json")) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {

            let message = "Request failed (" +
                response.status + ")";

            if (typeof data === "string" && data.trim()) {
                message = data;
            } else if (data && typeof data === "object") {
                message = data.message ||
                    data.error ||
                    message;
            }

            throw new Error(message);
        }

        return data;
    }


    function setWorkerText(id, value) {

        const element = document.getElementById(id);

        if (element) {
            element.textContent = value ?? "-";
        }
    }


    function setWorkerInput(id, value) {

        const element = document.getElementById(id);

        if (element) {
            element.value = value ?? "";
        }
    }


    function showWorkerMessage(message, success = true) {

        const element =
            document.getElementById("workerActionMessage");

        if (!element) {
            return;
        }

        element.className = success
            ? "alert alert-success"
            : "alert alert-danger";

        element.textContent = message;
    }


    function showWorkerModal(id) {

        const element = document.getElementById(id);

        if (!element) {
            return;
        }

        if (typeof bootstrap === "undefined") {
            console.error("Bootstrap is not loaded.");
            return;
        }

        bootstrap.Modal
            .getOrCreateInstance(element)
            .show();
    }


    function hideWorkerModal(id) {

        const element = document.getElementById(id);

        if (!element ||
            typeof bootstrap === "undefined") {
            return;
        }

        bootstrap.Modal
            .getOrCreateInstance(element)
            .hide();
    }


    function showWorkerTableMessage(message, isError = false) {

        if (!workersTableBody) {
            return;
        }

        workersTableBody.replaceChildren();

        const row = workersTableBody.insertRow();

        const cell = row.insertCell();

        cell.colSpan = 7;

        cell.className = isError
            ? "text-center text-danger py-4"
            : "text-center text-muted py-4";

        cell.textContent = message;
    }


    function createWorkerButton(label, className, callback) {

        const button = document.createElement("button");

        button.type = "button";
        button.className = className;
        button.textContent = label;

        button.addEventListener("click", callback);

        return button;
    }


    function formatWorkerDate(value) {

        if (!value) {
            return "-";
        }

        const date = new Date(value + "T00:00:00");

        if (Number.isNaN(date.getTime())) {
            return value;
        }

        return date.toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric"
        });
    }


    // ==========================================
    // 2. LOAD ALL WORKERS
    // ==========================================

    async function loadWorkers() {

        if (!workersTableBody) {
            return;
        }

        showWorkerTableMessage("Loading workers...");

        try {

            const response = await workerApiRequest(API_URL);

            allWorkers = Array.isArray(response)
                ? response
                : [];

            updateWorkerStatistics();

            displayWorkers();

        } catch (error) {

            console.error("Workers loading error:", error);

            showWorkerTableMessage(
                "Unable to load workers: " + error.message,
                true
            );
        }
    }


    // ==========================================
    // 3. UPDATE WORKER STATISTICS
    // ==========================================

    function updateWorkerStatistics() {

        setWorkerText(
            "manageWorkersCount",
            allWorkers.length
        );

        const totalAssigned = allWorkers.reduce(
            (total, worker) => {
                return total +
                    Number(worker.assignedComplaintCount || 0);
            },
            0
        );

        setWorkerText(
            "manageWorkersAssignedCount",
            totalAssigned
        );
    }


    // ==========================================
    // 4. DISPLAY WORKERS TABLE
    // ==========================================

    function displayWorkers() {

        if (!workersTableBody) {
            return;
        }

        const searchText = (workerSearch?.value || "")
            .toLowerCase()
            .trim();

        const filteredWorkers = allWorkers.filter(worker => {

            const searchable = [
                worker.workerId,
                worker.fullName,
                worker.email,
                worker.phoneNumber,
                worker.employeeCode
            ].join(" ").toLowerCase();

            return searchable.includes(searchText);
        });

        workersTableBody.replaceChildren();

        setWorkerText(
            "workerResultsCount",
            "Showing " + filteredWorkers.length +
            " of " + allWorkers.length + " workers"
        );

        if (filteredWorkers.length === 0) {

            showWorkerTableMessage(
                "No matching workers found."
            );

            return;
        }

        filteredWorkers.forEach(worker => {

            const row = workersTableBody.insertRow();

            row.insertCell().textContent =
                worker.workerId ?? "-";

            row.insertCell().textContent =
                worker.fullName || "-";

            row.insertCell().textContent =
                worker.employeeCode || "-";

            row.insertCell().textContent =
                worker.email || "-";

            row.insertCell().textContent =
                worker.phoneNumber || "-";

            const countCell = row.insertCell();

            const countBadge = document.createElement("span");

            countBadge.className =
                "badge bg-success";

            countBadge.textContent =
                worker.assignedComplaintCount ?? 0;

            countCell.appendChild(countBadge);

            const actionCell = row.insertCell();

            const actions = document.createElement("div");

            actions.className =
                "d-flex flex-wrap gap-2";

            // View button
            actions.appendChild(
                createWorkerButton(
                    "View",
                    "btn btn-sm btn-outline-primary",
                    function () {
                        viewWorker(worker.workerId);
                    }
                )
            );

            // Edit button
            actions.appendChild(
                createWorkerButton(
                    "Edit",
                    "btn btn-sm btn-outline-success",
                    function () {
                        editWorker(worker.workerId);
                    }
                )
            );

            actionCell.appendChild(actions);
        });
    }


    // ==========================================
    // 5. SEARCH WORKERS
    // ==========================================

    workerSearch?.addEventListener(
        "input",
        displayWorkers
    );


    // ==========================================
    // 6. VIEW WORKER DETAILS
    // ==========================================

    async function viewWorker(workerId) {

        try {

            const worker = await workerApiRequest(
                API_URL + "/" + workerId
            );

            setWorkerText(
                "modalWorkerId",
                worker.workerId
            );

            setWorkerText(
                "modalWorkerUserId",
                worker.userId
            );

            setWorkerText(
                "modalWorkerName",
                worker.fullName
            );

            setWorkerText(
                "modalWorkerCode",
                worker.employeeCode
            );

            setWorkerText(
                "modalWorkerEmail",
                worker.email
            );

            setWorkerText(
                "modalWorkerPhone",
                worker.phoneNumber
            );

            setWorkerText(
                "modalWorkerAddress",
                worker.address
            );

            setWorkerText(
                "modalWorkerJoiningDate",
                formatWorkerDate(worker.joiningDate)
            );

            setWorkerText(
                "modalWorkerAssignedCount",
                worker.assignedComplaintCount ?? 0
            );

            showWorkerModal("workerDetailsModal");

        } catch (error) {

            showWorkerMessage(
                "Unable to load worker: " + error.message,
                false
            );
        }
    }


    // ==========================================
    // 7. REGISTER NEW WORKER
    // ==========================================

    const registerWorkerForm =
        document.getElementById("registerWorkerForm");

    registerWorkerForm?.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const button =
                document.getElementById(
                    "saveRegisterWorkerBtn"
                );

            const message =
                document.getElementById(
                    "registerWorkerMessage"
                );

            const request = {

                fullName:
                    document.getElementById(
                        "registerWorkerName"
                    ).value.trim(),

                email:
                    document.getElementById(
                        "registerWorkerEmail"
                    ).value.trim(),

                phoneNumber:
                    document.getElementById(
                        "registerWorkerPhone"
                    ).value.trim(),

                address:
                    document.getElementById(
                        "registerWorkerAddress"
                    ).value.trim(),

                password:
                    document.getElementById(
                        "registerWorkerPassword"
                    ).value,

                employeeCode:
                    document.getElementById(
                        "registerWorkerCode"
                    ).value.trim()
            };

            if (message) {
                message.textContent = "";
            }

            if (button) {
                button.disabled = true;
            }

            try {

                await workerApiRequest(
                    API_URL + "/register",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify(request)
                    }
                );

                hideWorkerModal("registerWorkerModal");

                registerWorkerForm.reset();

                await loadWorkers();

                showWorkerMessage(
                    "Worker registered successfully."
                );

                // Refresh overview statistics when possible.
                refreshWorkerDashboardStats();

            } catch (error) {

                if (message) {
                    message.textContent = error.message;
                }

            } finally {

                if (button) {
                    button.disabled = false;
                }
            }
        }
    );


    // ==========================================
    // 8. OPEN EDIT WORKER MODAL
    // ==========================================

    async function editWorker(workerId) {

        try {

            const worker = await workerApiRequest(
                API_URL + "/" + workerId
            );

            setWorkerInput(
                "editWorkerId",
                worker.workerId
            );

            setWorkerInput(
                "editWorkerName",
                worker.fullName
            );

            setWorkerInput(
                "editWorkerCode",
                worker.employeeCode
            );

            setWorkerInput(
                "editWorkerEmail",
                worker.email
            );

            setWorkerInput(
                "editWorkerPhone",
                worker.phoneNumber
            );

            setWorkerInput(
                "editWorkerAddress",
                worker.address
            );

            setWorkerText(
                "editWorkerMessage",
                ""
            );

            showWorkerModal("editWorkerModal");

        } catch (error) {

            showWorkerMessage(
                "Unable to load worker: " + error.message,
                false
            );
        }
    }


    // ==========================================
    // 9. UPDATE WORKER DETAILS
    // ==========================================

    const editWorkerForm =
        document.getElementById("editWorkerForm");

    editWorkerForm?.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const workerId =
                document.getElementById(
                    "editWorkerId"
                ).value;

            const button =
                document.getElementById(
                    "saveEditWorkerBtn"
                );

            const message =
                document.getElementById(
                    "editWorkerMessage"
                );

            if (!/^\d+$/.test(workerId)) {

                if (message) {
                    message.textContent = "Invalid worker ID.";
                }

                return;
            }

            const request = {

                fullName:
                    document.getElementById(
                        "editWorkerName"
                    ).value.trim(),

                email:
                    document.getElementById(
                        "editWorkerEmail"
                    ).value.trim(),

                phoneNumber:
                    document.getElementById(
                        "editWorkerPhone"
                    ).value.trim(),

                address:
                    document.getElementById(
                        "editWorkerAddress"
                    ).value.trim(),

                employeeCode:
                    document.getElementById(
                        "editWorkerCode"
                    ).value.trim()
            };

            if (message) {
                message.textContent = "";
            }

            if (button) {
                button.disabled = true;
            }

            try {

                await workerApiRequest(
                    API_URL + "/" + workerId,
                    {
                        method: "PUT",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify(request)
                    }
                );

                hideWorkerModal("editWorkerModal");

                await loadWorkers();

                showWorkerMessage(
                    "Worker updated successfully."
                );

            } catch (error) {

                if (message) {
                    message.textContent = error.message;
                }

            } finally {

                if (button) {
                    button.disabled = false;
                }
            }
        }
    );


    // ==========================================
    // 10. REFRESH WORKERS
    // ==========================================

    document.getElementById(
        "refreshWorkersBtn"
    )?.addEventListener("click", loadWorkers);


    // ==========================================
    // 11. REFRESH DASHBOARD WORKER COUNT
    // ==========================================

    async function refreshWorkerDashboardStats() {

        try {

            const data = await workerApiRequest(
                "/api/admin/dashboard/stats"
            );

            setWorkerText(
                "totalWorkers",
                data.totalWorkers ?? "-"
            );

        } catch (error) {

            console.error(
                "Unable to refresh worker statistics:",
                error
            );
        }
    }


    // ==========================================
    // 12. LOAD WORKERS WHEN SECTION OPENS
    // ==========================================

    const workersNavigationLink =
        document.querySelector(
            '.sidebar-nav .nav-link[data-section="workers"]'
        );

    workersNavigationLink?.addEventListener(
        "click",
        loadWorkers
    );

    window.addEventListener("hashchange", function () {

        if (window.location.hash === "#workers") {
            loadWorkers();
        }
    });


    // ==========================================
    // 13. INITIAL PAGE LOAD
    // ==========================================

    if (window.location.hash === "#workers") {
        loadWorkers();
    }

});

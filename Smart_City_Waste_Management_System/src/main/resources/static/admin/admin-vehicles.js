
document.addEventListener("DOMContentLoaded", function () {

    "use strict";

    // ==========================================
    // 1. API URL AND VARIABLES
    // ==========================================

    const API_URL = "/api/vehicles";

    let allVehicles = [];
    let isLoading = false;
    let isAddingVehicle = false;
    let isEditingVehicle = false;

    // ==========================================
    // 2. HTML ELEMENTS
    // ==========================================

    const tableBody = document.getElementById("vehiclesTableBody");
    const searchInput = document.getElementById("vehicleSearch");
    const statusFilter = document.getElementById("vehicleStatusFilter");
    const refreshBtn = document.getElementById("refreshVehiclesBtn");

    const totalVehicles = document.getElementById("totalManageVehicles");
    const availableVehicles = document.getElementById("totalAvailableVehicles");
    const inUseVehicles = document.getElementById("totalInUseVehicles");
    const maintenanceVehicles = document.getElementById("totalMaintenanceVehicles");

    const resultsCount = document.getElementById("vehicleResultsCount");
    const actionMessage = document.getElementById("vehicleActionMessage");

    // Add Vehicle
    const addForm = document.getElementById("addVehicleForm");
    const addModalElement = document.getElementById("addVehicleModal");
    const addNumber = document.getElementById("addVehicleNumber");
    const addType = document.getElementById("addVehicleType");
    const addCapacity = document.getElementById("addVehicleCapacity");
    const addStatus = document.getElementById("addVehicleStatus");
    const addMessage = document.getElementById("addVehicleMessage");
    const saveAddBtn = document.getElementById("saveAddVehicleBtn");

    // View Vehicle
    const viewModalElement = document.getElementById("vehicleDetailsModal");

    // Edit Vehicle
    const editForm = document.getElementById("editVehicleForm");
    const editModalElement = document.getElementById("editVehicleModal");
    const editId = document.getElementById("editVehicleId");
    const editNumber = document.getElementById("editVehicleNumber");
    const editType = document.getElementById("editVehicleType");
    const editCapacity = document.getElementById("editVehicleCapacity");
    const editStatus = document.getElementById("editVehicleStatus");
    const editMessage = document.getElementById("editVehicleMessage");
    const saveEditBtn = document.getElementById("saveEditVehicleBtn");

    // ==========================================
    // 3. HELPER FUNCTIONS
    // ==========================================

    function safeText(value) {
        if (value === null || value === undefined || value === "") {
            return "—";
        }
        return String(value);
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function setText(id, value) {
        const element = document.getElementById(id);

        if (element) {
            element.textContent = safeText(value);
        }
    }

    function showMessage(message, type = "success") {
        if (!actionMessage) return;

        actionMessage.className = "alert alert-" + type;
        actionMessage.textContent = message;
        actionMessage.classList.remove("d-none");
    }

    function hideMessage() {
        if (!actionMessage) return;

        actionMessage.textContent = "";
        actionMessage.className = "alert d-none";
    }

    function showFormError(element, message) {
        if (!element) return;

        element.textContent = message;
        element.classList.remove("d-none");
    }

    function clearFormError(element) {
        if (!element) return;

        element.textContent = "";
        element.classList.add("d-none");
    }

    function setButtonLoading(button, loading, loadingText = "Saving...") {
        if (!button) return;

        if (loading) {
            if (button.disabled) return;

            button.dataset.originalText = button.innerHTML;
            button.disabled = true;

            button.innerHTML =
                '<span class="spinner-border spinner-border-sm me-2"></span>' +
                loadingText;

        } else {
            button.disabled = false;

            if (button.dataset.originalText) {
                button.innerHTML = button.dataset.originalText;
                delete button.dataset.originalText;
            }
        }
    }

    function getModal(element) {
        if (!element || typeof bootstrap === "undefined") {
            return null;
        }

        return bootstrap.Modal.getOrCreateInstance(element);
    }

    async function readResponse(response) {
        const text = await response.text();

        if (!text) return null;

        try {
            return JSON.parse(text);
        } catch (error) {
            return text;
        }
    }

    function extractError(data, status) {

        if (data && typeof data === "object" && data.message) {
            return data.message;
        }

        if (status === 400) {
            return "Invalid vehicle details or duplicate vehicle number.";
        }

        if (status === 403) {
            return "Admin access required. Please log in as ADMIN.";
        }

        if (status === 404) {
            return "Vehicle record not found.";
        }

        if (status === 409) {
            return "Vehicle number already exists. Please use another number.";
        }

        if (status === 500) {
            return "Server error occurred. Please check the vehicle records before trying again.";
        }

        return "Request failed. HTTP status: " + status;
    }

    async function apiRequest(url, options = {}) {

        const response = await fetch(url, {
            ...options,
            credentials: "same-origin",
            headers: {
                "Accept": "application/json",
                ...(options.body ? {
                    "Content-Type": "application/json"
                } : {}),
                ...(options.headers || {})
            }
        });

        const data = await readResponse(response);

        if (!response.ok) {
            throw new Error(extractError(data, response.status));
        }

        return data;
    }

    function formatCapacity(value) {
        const number = Number(value);

        if (!Number.isFinite(number)) {
            return safeText(value);
        }

        return number.toLocaleString("en-IN", {
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
        }) + " kg";
    }

    function statusBadge(status) {
        const value = String(status || "").toUpperCase();

        let className = "bg-secondary";
        let label = value || "UNKNOWN";

        if (value === "AVAILABLE") {
            className = "bg-success";
            label = "Available";
        } else if (value === "IN_USE") {
            className = "bg-primary";
            label = "In Use";
        } else if (value === "MAINTENANCE") {
            className = "bg-warning text-dark";
            label = "Maintenance";
        }

        return '<span class="badge ' + className + '">' +
            escapeHtml(label) + '</span>';
    }

    // ==========================================
    // 4. UPDATE STATISTICS
    // ==========================================

    function updateStatistics() {

        const available = allVehicles.filter(vehicle =>
            String(vehicle.vehicleStatus || "").toUpperCase() === "AVAILABLE"
        ).length;

        const inUse = allVehicles.filter(vehicle =>
            String(vehicle.vehicleStatus || "").toUpperCase() === "IN_USE"
        ).length;

        const maintenance = allVehicles.filter(vehicle =>
            String(vehicle.vehicleStatus || "").toUpperCase() === "MAINTENANCE"
        ).length;

        if (totalVehicles) {
            totalVehicles.textContent = allVehicles.length;
        }

        if (availableVehicles) {
            availableVehicles.textContent = available;
        }

        if (inUseVehicles) {
            inUseVehicles.textContent = inUse;
        }

        if (maintenanceVehicles) {
            maintenanceVehicles.textContent = maintenance;
        }
    }

    // ==========================================
    // 5. DISPLAY VEHICLES IN TABLE
    // ==========================================

    function renderVehicles() {
        if (!tableBody) return;

        const search = searchInput
            ? searchInput.value.trim().toLowerCase()
            : "";

        const selectedStatus = statusFilter
            ? statusFilter.value.toUpperCase()
            : "ALL";

        const filteredVehicles = allVehicles.filter(vehicle => {

            const id = String(vehicle.vehicleId ?? "");
            const number = String(vehicle.vehicleNumber || "").toLowerCase();
            const type = String(vehicle.vehicleType || "").toLowerCase();
            const status = String(vehicle.vehicleStatus || "").toUpperCase();

            const matchesSearch =
                id.includes(search) ||
                number.includes(search) ||
                type.includes(search) ||
                status.toLowerCase().includes(search);

            const matchesStatus =
                selectedStatus === "ALL" ||
                selectedStatus === status;

            return matchesSearch && matchesStatus;
        });

        if (resultsCount) {
            resultsCount.textContent =
                "Showing " + filteredVehicles.length +
                " of " + allVehicles.length + " vehicles";
        }

        if (filteredVehicles.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6"
                        class="text-center text-muted py-4">
                        No vehicles found.
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = filteredVehicles.map(vehicle => `
            <tr>
                <td>${escapeHtml(safeText(vehicle.vehicleId))}</td>

                <td class="fw-semibold">
                    ${escapeHtml(safeText(vehicle.vehicleNumber))}
                </td>

                <td>
                    ${escapeHtml(safeText(vehicle.vehicleType))}
                </td>

                <td>
                    ${escapeHtml(formatCapacity(vehicle.capacityKg))}
                </td>

                <td>
                    ${statusBadge(vehicle.vehicleStatus)}
                </td>

                <td>
                    <div class="d-flex flex-wrap gap-2">

                        <button type="button"
                            class="btn btn-sm btn-outline-primary"
                            data-vehicle-action="view"
                            data-vehicle-id="${escapeHtml(vehicle.vehicleId)}">
                            <i class="fa-solid fa-eye me-1"></i>
                            View
                        </button>

                        <button type="button"
                            class="btn btn-sm btn-outline-success"
                            data-vehicle-action="edit"
                            data-vehicle-id="${escapeHtml(vehicle.vehicleId)}">
                            <i class="fa-solid fa-pen-to-square me-1"></i>
                            Edit
                        </button>

                    </div>
                </td>
            </tr>
        `).join("");
    }

    // ==========================================
    // 6. LOAD VEHICLES FROM BACKEND
    // ==========================================

    async function loadVehicles() {
        if (isLoading) return false;

        isLoading = true;

        if (refreshBtn) {
            refreshBtn.disabled = true;
        }

        if (tableBody) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6"
                        class="text-center text-muted py-4">
                        Loading vehicles...
                    </td>
                </tr>
            `;
        }

        try {
            const data = await apiRequest(API_URL);

            if (!Array.isArray(data)) {
                throw new Error("Invalid vehicle data received.");
            }

            allVehicles = data;

            updateStatistics();
            renderVehicles();

            return true;

        } catch (error) {
            console.error("Error loading vehicles:", error);

            if (tableBody) {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="6"
                            class="text-center text-danger py-4">
                            Unable to load vehicle records.
                        </td>
                    </tr>
                `;
            }

            showMessage(error.message, "danger");
            return false;

        } finally {
            isLoading = false;

            if (refreshBtn) {
                refreshBtn.disabled = false;
            }
        }
    }

    // ==========================================
    // 7. VIEW VEHICLE DETAILS
    // ==========================================

    function viewVehicle(vehicleId) {

        const vehicle = allVehicles.find(item =>
            String(item.vehicleId) === String(vehicleId)
        );

        if (!vehicle) {
            showMessage("Vehicle not found.", "danger");
            return;
        }

        setText("modalVehicleId", vehicle.vehicleId);
        setText("modalVehicleNumber", vehicle.vehicleNumber);
        setText("modalVehicleType", vehicle.vehicleType);
        setText("modalVehicleCapacity", formatCapacity(vehicle.capacityKg));
        setText("modalVehicleStatus",
            String(vehicle.vehicleStatus || "").replace(/_/g, " ")
        );

        const modal = getModal(viewModalElement);

        if (modal) {
            modal.show();
        }
    }

    // ==========================================
    // 8. OPEN EDIT VEHICLE MODAL
    // ==========================================

    function openEditVehicle(vehicleId) {

        const vehicle = allVehicles.find(item =>
            String(item.vehicleId) === String(vehicleId)
        );

        if (!vehicle) {
            showMessage("Vehicle not found.", "danger");
            return;
        }

        if (editId) editId.value = vehicle.vehicleId;
        if (editNumber) editNumber.value = vehicle.vehicleNumber || "";
        if (editType) editType.value = vehicle.vehicleType || "";
        if (editCapacity) editCapacity.value = vehicle.capacityKg ?? "";
        if (editStatus) editStatus.value = vehicle.vehicleStatus || "AVAILABLE";

        clearFormError(editMessage);

        const modal = getModal(editModalElement);

        if (modal) {
            modal.show();
        }
    }

    // ==========================================
    // 9. VALIDATE VEHICLE FORM
    // ==========================================

    function validateVehicleData(data) {

        if (!data.vehicleNumber) {
            throw new Error("Vehicle number is required.");
        }

        if (data.vehicleNumber.length > 30) {
            throw new Error("Vehicle number cannot exceed 30 characters.");
        }

        if (!data.vehicleType) {
            throw new Error("Vehicle type is required.");
        }

        if (data.vehicleType.length > 50) {
            throw new Error("Vehicle type cannot exceed 50 characters.");
        }

        if (!Number.isFinite(data.capacityKg) || data.capacityKg <= 0) {
            throw new Error("Capacity must be greater than zero.");
        }

        if (data.capacityKg > 99999999.99) {
            throw new Error("Capacity exceeds the allowed database limit.");
        }

        if (Math.round(data.capacityKg * 100) / 100 !== data.capacityKg) {
            throw new Error("Capacity cannot have more than two decimal places.");
        }

        const validStatuses = [
            "AVAILABLE",
            "IN_USE",
            "MAINTENANCE"
        ];

        if (!validStatuses.includes(data.vehicleStatus)) {
            throw new Error("Please select a valid vehicle status.");
        }
    }

    // ==========================================
    // 10. ADD VEHICLE
    // ==========================================

    async function handleAddVehicle(event) {
        event.preventDefault();

        // Prevent multiple requests.
        if (isAddingVehicle) return;

        if (!addForm || !addForm.reportValidity()) return;

        clearFormError(addMessage);

        const data = {
            vehicleNumber: addNumber.value.trim().toUpperCase(),
            vehicleType: addType.value.trim(),
            capacityKg: Number(addCapacity.value),
            vehicleStatus: addStatus.value.toUpperCase()
        };

        try {
            validateVehicleData(data);

            isAddingVehicle = true;
            setButtonLoading(saveAddBtn, true, "Saving...");

            await apiRequest(API_URL, {
                method: "POST",
                body: JSON.stringify(data)
            });

            const modal = getModal(addModalElement);

            if (modal) {
                modal.hide();
            }

            addForm.reset();

            const loaded = await loadVehicles();

            if (loaded) {
                showMessage("Vehicle added successfully.", "success");
            }

        } catch (error) {
            console.error("Error adding vehicle:", error);
            showFormError(addMessage, error.message);

        } finally {
            isAddingVehicle = false;
            setButtonLoading(saveAddBtn, false);
        }
    }

    // ==========================================
    // 11. EDIT VEHICLE
    // ==========================================

    async function handleEditVehicle(event) {
        event.preventDefault();

        // Prevent multiple requests.
        if (isEditingVehicle) return;

        if (!editForm || !editForm.reportValidity()) return;

        clearFormError(editMessage);

        const vehicleId = editId.value;

        if (!vehicleId) {
            showFormError(editMessage, "Vehicle ID is missing.");
            return;
        }

        const data = {
            vehicleNumber: editNumber.value.trim().toUpperCase(),
            vehicleType: editType.value.trim(),
            capacityKg: Number(editCapacity.value),
            vehicleStatus: editStatus.value.toUpperCase()
        };

        try {
            validateVehicleData(data);

            isEditingVehicle = true;
            setButtonLoading(saveEditBtn, true, "Saving...");

            await apiRequest(
                API_URL + "/" + encodeURIComponent(vehicleId),
                {
                    method: "PUT",
                    body: JSON.stringify(data)
                }
            );

            const modal = getModal(editModalElement);

            if (modal) {
                modal.hide();
            }

            const loaded = await loadVehicles();

            if (loaded) {
                showMessage("Vehicle updated successfully.", "success");
            }

        } catch (error) {
            console.error("Error updating vehicle:", error);
            showFormError(editMessage, error.message);

        } finally {
            isEditingVehicle = false;
            setButtonLoading(saveEditBtn, false);
        }
    }

    // ==========================================
    // 12. EVENT LISTENERS
    // ==========================================

    if (searchInput) {
        searchInput.addEventListener("input", renderVehicles);
    }

    if (statusFilter) {
        statusFilter.addEventListener("change", renderVehicles);
    }

    if (refreshBtn) {
        refreshBtn.addEventListener("click", async function () {
            hideMessage();
            await loadVehicles();
        });
    }

    if (tableBody) {
        tableBody.addEventListener("click", function (event) {

            const button = event.target.closest(
                "button[data-vehicle-action]"
            );

            if (!button) return;

            const action = button.dataset.vehicleAction;
            const vehicleId = button.dataset.vehicleId;

            if (action === "view") {
                viewVehicle(vehicleId);
            }

            if (action === "edit") {
                openEditVehicle(vehicleId);
            }
        });
    }

    if (addForm) {
        addForm.addEventListener("submit", handleAddVehicle);

        addForm.addEventListener("input", function () {
            clearFormError(addMessage);
        });

        addForm.addEventListener("change", function () {
            clearFormError(addMessage);
        });
    }

    if (editForm) {
        editForm.addEventListener("submit", handleEditVehicle);

        editForm.addEventListener("input", function () {
            clearFormError(editMessage);
        });

        editForm.addEventListener("change", function () {
            clearFormError(editMessage);
        });
    }

    if (addModalElement) {
        addModalElement.addEventListener("show.bs.modal", function () {
            clearFormError(addMessage);
        });

        addModalElement.addEventListener("hidden.bs.modal", function () {
            if (addForm && !isAddingVehicle) {
                addForm.reset();
            }
            clearFormError(addMessage);
        });
    }

    if (editModalElement) {
        editModalElement.addEventListener("show.bs.modal", function () {
            clearFormError(editMessage);
        });
    }

    // Reload data when Manage Vehicles is opened.
    document.querySelectorAll(
        '[data-section="vehicles"]'
    ).forEach(link => {

        link.addEventListener("click", function () {
            hideMessage();
            loadVehicles();
        });
    });

    window.addEventListener("hashchange", function () {
        if (window.location.hash === "#vehicles") {
            loadVehicles();
        }
    });

    // Initial loading.
    loadVehicles();

});

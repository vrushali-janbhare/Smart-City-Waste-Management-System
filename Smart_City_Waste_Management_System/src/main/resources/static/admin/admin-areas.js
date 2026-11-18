
"use strict";

// ==========================================
// E-Safai Admin Dashboard - Manage Areas
// ==========================================

document.addEventListener("DOMContentLoaded", () => {

    const API_URL = "/api/areas";

    const areaSection = document.getElementById("areas");
    const areasTableBody = document.getElementById("areasTableBody");

    if (!areaSection || !areasTableBody) {
        return;
    }

    // ------------------------------------------
    // HTML elements
    // ------------------------------------------

    const areaSearch = document.getElementById("areaSearch");
    const refreshAreasBtn = document.getElementById("refreshAreasBtn");

    const totalManageAreas = document.getElementById("totalManageAreas");
    const totalManageWards = document.getElementById("totalManageWards");
    const totalManageCities = document.getElementById("totalManageCities");

    const areaResultsCount = document.getElementById("areaResultsCount");
    const areaActionMessage = document.getElementById("areaActionMessage");

    // Add Area
    const addAreaForm = document.getElementById("addAreaForm");
    const addAreaName = document.getElementById("addAreaName");
    const addAreaWard = document.getElementById("addAreaWard");
    const addAreaCity = document.getElementById("addAreaCity");
    const addAreaMessage = document.getElementById("addAreaMessage");
    const saveAddAreaBtn = document.getElementById("saveAddAreaBtn");

    // Edit Area
    const editAreaForm = document.getElementById("editAreaForm");
    const editAreaId = document.getElementById("editAreaId");
    const editAreaName = document.getElementById("editAreaName");
    const editAreaWard = document.getElementById("editAreaWard");
    const editAreaCity = document.getElementById("editAreaCity");
    const editAreaMessage = document.getElementById("editAreaMessage");
    const saveEditAreaBtn = document.getElementById("saveEditAreaBtn");

    // View Area
    const modalAreaId = document.getElementById("modalAreaId");
    const modalAreaName = document.getElementById("modalAreaName");
    const modalAreaWard = document.getElementById("modalAreaWard");
    const modalAreaCity = document.getElementById("modalAreaCity");

    // Modals
    const addAreaModalElement = document.getElementById("addAreaModal");
    const editAreaModalElement = document.getElementById("editAreaModal");
    const areaDetailsModalElement = document.getElementById("areaDetailsModal");

    // ------------------------------------------
    // State
    // ------------------------------------------

    let allAreas = [];
    let loading = false;

    // ------------------------------------------
    // Helpers
    // ------------------------------------------

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

    function showMessage(element, message, type = "success") {

        if (!element) return;

        element.textContent = message;
        element.className = `alert alert-${type}`;
        element.classList.remove("d-none");
    }

    function hideMessage(element) {

        if (!element) return;

        element.textContent = "";
        element.className = "alert d-none";
    }

    function showFormError(element, message) {
        if (element) {
            element.textContent = message;
        }
    }

    function clearFormError(element) {
        if (element) {
            element.textContent = "";
        }
    }

    function setButtonLoading(button, isLoading, loadingText) {

        if (!button) return;

        if (isLoading) {
            button.dataset.originalHtml = button.innerHTML;
            button.disabled = true;
            button.textContent = loadingText;
        } else {
            button.disabled = false;

            if (button.dataset.originalHtml) {
                button.innerHTML = button.dataset.originalHtml;
            }
        }
    }

    function getModal(element) {

        if (!element || !window.bootstrap) {
            return null;
        }

        return bootstrap.Modal.getOrCreateInstance(element);
    }

    async function readResponse(response) {

        const contentType =
            response.headers.get("content-type") || "";

        if (contentType.includes("application/json")) {
            return await response.json();
        }

        return await response.text();
    }

    function extractError(data, fallback) {

        if (data && typeof data === "object") {
            return data.message || data.error || fallback;
        }

        if (typeof data === "string" &&
            data.trim() &&
            !data.trim().startsWith("<")) {
            return data;
        }

        return fallback;
    }

    async function apiRequest(url, options = {}) {

        const response = await fetch(url, {
            credentials: "same-origin",
            ...options,
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

            const error = new Error(
                extractError(
                    data,
                    `Request failed (${response.status}).`
                )
            );

            error.status = response.status;
            throw error;
        }

        return data;
    }

    // ------------------------------------------
    // Statistics
    // ------------------------------------------

    function updateStatistics() {

        const wards = new Set();
        const cities = new Set();

        allAreas.forEach(area => {

            const ward = String(area.wardNumber ?? "")
                .trim()
                .toLowerCase();

            const city = String(area.city ?? "")
                .trim()
                .toLowerCase();

            if (ward) wards.add(ward);
            if (city) cities.add(city);
        });

        if (totalManageAreas) {
            totalManageAreas.textContent = allAreas.length;
        }

        if (totalManageWards) {
            totalManageWards.textContent = wards.size;
        }

        if (totalManageCities) {
            totalManageCities.textContent = cities.size;
        }
    }

    // ------------------------------------------
    // Render Area Table
    // ------------------------------------------

    function renderAreas() {

        const query = (areaSearch?.value || "")
            .trim()
            .toLowerCase();

        const filteredAreas = allAreas.filter(area => {

            const searchText = [
                area.areaId,
                area.areaName,
                area.wardNumber,
                area.city
            ].map(value => String(value ?? "").toLowerCase())
             .join(" ");

            return searchText.includes(query);
        });

        if (filteredAreas.length === 0) {

            areasTableBody.innerHTML = `
                <tr>
                    <td colspan="5"
                        class="text-center text-muted py-4">
                        ${allAreas.length === 0
                            ? "No areas registered yet."
                            : "No matching areas found."}
                    </td>
                </tr>
            `;

        } else {

            areasTableBody.innerHTML = filteredAreas.map(area => {

                const id = Number(area.areaId);

                return `
                    <tr>
                        <td>${escapeHtml(safeText(area.areaId))}</td>

                        <td>
                            <strong>
                                ${escapeHtml(safeText(area.areaName))}
                            </strong>
                        </td>

                        <td>
                            ${escapeHtml(safeText(area.wardNumber))}
                        </td>

                        <td>
                            ${escapeHtml(safeText(area.city))}
                        </td>

                        <td>
                            <div class="d-flex gap-2 flex-wrap">

                                <button type="button"
                                    class="btn btn-sm btn-outline-success"
                                    data-area-action="view"
                                    data-area-id="${id}">
                                    <i class="fa-solid fa-eye me-1"></i>
                                    View
                                </button>

                                <button type="button"
                                    class="btn btn-sm btn-success"
                                    data-area-action="edit"
                                    data-area-id="${id}">
                                    <i class="fa-solid fa-pen me-1"></i>
                                    Edit
                                </button>

                            </div>
                        </td>
                    </tr>
                `;
            }).join("");
        }

        if (areaResultsCount) {
            areaResultsCount.textContent =
                `Showing ${filteredAreas.length} of ${allAreas.length} areas`;
        }
    }

    // ------------------------------------------
    // Load Areas From Spring Boot
    // ------------------------------------------

    async function loadAreas() {

        if (loading) return;

        loading = true;

        if (refreshAreasBtn) {
            refreshAreasBtn.disabled = true;
        }

        areasTableBody.innerHTML = `
            <tr>
                <td colspan="5"
                    class="text-center text-muted py-4">
                    Loading areas...
                </td>
            </tr>
        `;

        try {

            const data = await apiRequest(API_URL);

            if (!Array.isArray(data)) {
                throw new Error("Invalid area data received.");
            }

            allAreas = data;

            updateStatistics();
            renderAreas();

        } catch (error) {

            areasTableBody.innerHTML = `
                <tr>
                    <td colspan="5"
                        class="text-center text-danger py-4">
                        Unable to load areas.
                    </td>
                </tr>
            `;

            if (areaResultsCount) {
                areaResultsCount.textContent = "";
            }

            showMessage(
                areaActionMessage,
                error.message || "Failed to load areas.",
                "danger"
            );

        } finally {

            loading = false;

            if (refreshAreasBtn) {
                refreshAreasBtn.disabled = false;
            }
        }
    }

    // ------------------------------------------
    // View Area
    // ------------------------------------------

    function viewArea(areaId) {

        const area = allAreas.find(
            item => Number(item.areaId) === Number(areaId)
        );

        if (!area) {
            showMessage(
                areaActionMessage,
                "Area not found. Please refresh.",
                "danger"
            );
            return;
        }

        modalAreaId.textContent = safeText(area.areaId);
        modalAreaName.textContent = safeText(area.areaName);
        modalAreaWard.textContent = safeText(area.wardNumber);
        modalAreaCity.textContent = safeText(area.city);

        getModal(areaDetailsModalElement)?.show();
    }

    // ------------------------------------------
    // Open Edit Area
    // ------------------------------------------

    function openEditArea(areaId) {

        const area = allAreas.find(
            item => Number(item.areaId) === Number(areaId)
        );

        if (!area) {
            showMessage(
                areaActionMessage,
                "Area not found. Please refresh.",
                "danger"
            );
            return;
        }

        editAreaId.value = area.areaId;
        editAreaName.value = area.areaName || "";
        editAreaWard.value = area.wardNumber || "";
        editAreaCity.value = area.city || "";

        clearFormError(editAreaMessage);

        getModal(editAreaModalElement)?.show();
    }

    // ------------------------------------------
    // Add Area
    // ------------------------------------------

    async function handleAddArea(event) {

        event.preventDefault();

        clearFormError(addAreaMessage);

        const payload = {
            areaName: addAreaName.value.trim(),
            wardNumber: addAreaWard.value.trim(),
            city: addAreaCity.value.trim()
        };

        if (!payload.areaName ||
            !payload.wardNumber ||
            !payload.city) {

            showFormError(
                addAreaMessage,
                "All fields are required."
            );
            return;
        }

        setButtonLoading(saveAddAreaBtn, true, "Saving...");

        try {

            await apiRequest(API_URL, {
                method: "POST",
                body: JSON.stringify(payload)
            });

            getModal(addAreaModalElement)?.hide();

            addAreaForm.reset();

            hideMessage(areaActionMessage);

            await loadAreas();

            showMessage(
                areaActionMessage,
                "Area added successfully.",
                "success"
            );

        } catch (error) {

            showFormError(
                addAreaMessage,
                error.message || "Failed to add area."
            );

        } finally {

            setButtonLoading(saveAddAreaBtn, false);
        }
    }

    // ------------------------------------------
    // Update Area
    // ------------------------------------------

    async function handleEditArea(event) {

        event.preventDefault();

        clearFormError(editAreaMessage);

        const areaId = editAreaId.value;

        const payload = {
            areaName: editAreaName.value.trim(),
            wardNumber: editAreaWard.value.trim(),
            city: editAreaCity.value.trim()
        };

        if (!payload.areaName ||
            !payload.wardNumber ||
            !payload.city) {

            showFormError(
                editAreaMessage,
                "All fields are required."
            );
            return;
        }

        setButtonLoading(saveEditAreaBtn, true, "Saving...");

        try {

            await apiRequest(`${API_URL}/${encodeURIComponent(areaId)}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });

            getModal(editAreaModalElement)?.hide();

            hideMessage(areaActionMessage);

            await loadAreas();

            showMessage(
                areaActionMessage,
                "Area updated successfully.",
                "success"
            );

        } catch (error) {

            showFormError(
                editAreaMessage,
                error.message || "Failed to update area."
            );

        } finally {

            setButtonLoading(saveEditAreaBtn, false);
        }
    }

    // ------------------------------------------
    // Table Button Clicks
    // ------------------------------------------

    areasTableBody.addEventListener("click", event => {

        const button = event.target.closest(
            "button[data-area-action]"
        );

        if (!button) return;

        const action = button.dataset.areaAction;
        const areaId = button.dataset.areaId;

        if (action === "view") {
            viewArea(areaId);
        }

        if (action === "edit") {
            openEditArea(areaId);
        }
    });

    // ------------------------------------------
    // Event Listeners
    // ------------------------------------------

    areaSearch?.addEventListener("input", renderAreas);

    refreshAreasBtn?.addEventListener("click", () => {
        hideMessage(areaActionMessage);
        loadAreas();
    });

    addAreaForm?.addEventListener("submit", handleAddArea);

    editAreaForm?.addEventListener("submit", handleEditArea);

    addAreaModalElement?.addEventListener("show.bs.modal", () => {
        addAreaForm?.reset();
        clearFormError(addAreaMessage);
    });

    editAreaModalElement?.addEventListener("hidden.bs.modal", () => {
        clearFormError(editAreaMessage);
    });

    // Reload when Manage Areas is selected.
    document.querySelectorAll(
        '.sidebar-nav a[data-section="areas"]'
    ).forEach(link => {

        link.addEventListener("click", () => {
            loadAreas();
        });
    });

    window.addEventListener("hashchange", () => {
        if (window.location.hash === "#areas") {
            loadAreas();
        }
    });

    // Initial load
    loadAreas();

});

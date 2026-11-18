
"use strict";

/* =====================================
   E-SAFAI - BULK PICKUP JAVASCRIPT
===================================== */

document.addEventListener("DOMContentLoaded", async function () {

    // FORM ELEMENTS
    const form = document.getElementById("bulkPickupForm");
    const categorySelect = document.getElementById("category");
    const areaSelect = document.getElementById("area");
    const requestedDateInput =
        document.getElementById("requestedDate");
    const quantityInput =
        document.getElementById("estimatedQuantityKg");
    const addressInput =
        document.getElementById("pickupAddress");
    const messageBox =
        document.getElementById("messageBox");
    const submitBtn =
        document.getElementById("submitBtn");
    const resetBtn =
        document.getElementById("resetBtn");
    const logoutBtn =
        document.getElementById("logoutBtn");

    // REQUEST HISTORY ELEMENTS
    const pickupRequestsTable =
        document.getElementById("pickupRequestsTable");
    const totalPickupRequests =
        document.getElementById("totalPickupRequests");
    const pendingPickupRequests =
        document.getElementById("pendingPickupRequests");
    const completedPickupRequests =
        document.getElementById("completedPickupRequests");
    const pickupHistoryMessage =
        document.getElementById("pickupHistoryMessage");
    const refreshRequestsBtn =
        document.getElementById("refreshRequestsBtn");

    let isSubmitting = false;
    let isLoggedIn = false;
    let dataLoaded = false;

    /* =====================================
       DATE HELPERS
    ===================================== */

    function getTodayDate() {
        const now = new Date();

        const year = now.getFullYear();
        const month = String(
            now.getMonth() + 1
        ).padStart(2, "0");
        const day = String(
            now.getDate()
        ).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    function updateMinimumDate() {
        requestedDateInput.min = getTodayDate();
    }

    /* =====================================
       MESSAGE HELPERS
    ===================================== */

    function showMessage(message, type) {
        messageBox.textContent = message;
        messageBox.className = "message-box " + type;
        messageBox.style.display = "block";
        messageBox.setAttribute("role", "status");
        messageBox.setAttribute("aria-live", "polite");
    }

    function clearMessage() {
        messageBox.textContent = "";
        messageBox.className = "message-box";
        messageBox.style.display = "none";
    }

    /* =====================================
       API HELPERS
    ===================================== */

    async function readResponse(response) {
        const text = await response.text();

        if (!text) {
            return {};
        }

        try {
            return JSON.parse(text);
        } catch (error) {
            throw new Error(
                "Unexpected response from the server."
            );
        }
    }

    async function fetchJSON(url, options = {}) {
        const response = await fetch(url, {
            credentials: "same-origin",
            ...options
        });

        const data = await readResponse(response);

        if (!response.ok) {
            if (response.status === 401 ||
                response.status === 403) {
                throw new Error(
                    "Your session may have expired. " +
                    "Please log in again."
                );
            }

            throw new Error(
                data.message ||
                "Unable to complete the request."
            );
        }

        return data;
    }

    /* =====================================
       CHECK LOGIN SESSION
    ===================================== */

    async function checkUserSession() {
        try {
            const user = await fetchJSON(
                "/api/users/session"
            );

            if (user.userId == null) {
                window.location.href = "login.html";
                return false;
            }

            // Role verification is also enforced
            // by the Bulk Pickup backend controller.
            if (user.roleName &&
                user.roleName !== "USER") {
                window.location.href = "login.html";
                return false;
            }

            isLoggedIn = true;
            return true;

        } catch (error) {
            window.location.href = "login.html";
            return false;
        }
    }

    /* =====================================
       LOAD AREAS
    ===================================== */

    async function loadAreas() {
        const areas = await fetchJSON("/api/areas");

        if (!Array.isArray(areas)) {
            throw new Error(
                "Invalid area data received."
            );
        }

        areaSelect.innerHTML =
            '<option value="">Select Your Area</option>';

        areas.forEach(function (area) {
            const id = area.areaId ?? area.area_id;
            const name = area.areaName ?? area.area_name;

            if (id == null || !name) {
                return;
            }

            const option = document.createElement("option");
            option.value = id;
            option.textContent = name;

            areaSelect.appendChild(option);
        });

        if (areaSelect.options.length === 1) {
            throw new Error(
                "No areas are available. " +
                "Please contact the administrator."
            );
        }
    }

    /* =====================================
       LOAD WASTE CATEGORIES
    ===================================== */

    async function loadCategories() {
        const categories = await fetchJSON(
            "/api/waste-categories"
        );

        if (!Array.isArray(categories)) {
            throw new Error(
                "Invalid waste category data received."
            );
        }

        categorySelect.innerHTML =
            '<option value="">Select Waste Category</option>';

        categories.forEach(function (category) {
            const id =
                category.categoryId ??
                category.category_id;

            const name =
                category.categoryName ??
                category.category_name;

            if (id == null || !name) {
                return;
            }

            const option = document.createElement("option");
            option.value = id;
            option.textContent = name;

            categorySelect.appendChild(option);
        });

        if (categorySelect.options.length === 1) {
            throw new Error(
                "No waste categories are available."
            );
        }
    }

    /* =====================================
       VALIDATE FORM
    ===================================== */

    function validateForm() {
        const categoryId = Number(categorySelect.value);
        const areaId = Number(areaSelect.value);
        const requestedDate = requestedDateInput.value;
        const quantityText = quantityInput.value.trim();
        const quantity = Number(quantityText);
        const pickupAddress = addressInput.value.trim();

        if (!categorySelect.value ||
            !Number.isSafeInteger(categoryId) ||
            categoryId <= 0) {

            showMessage(
                "Please select a valid waste category.",
                "error"
            );
            categorySelect.focus();
            return false;
        }

        if (!areaSelect.value ||
            !Number.isSafeInteger(areaId) ||
            areaId <= 0) {

            showMessage(
                "Please select a valid area.",
                "error"
            );
            areaSelect.focus();
            return false;
        }

        if (!requestedDate) {
            showMessage(
                "Please select a pickup date.",
                "error"
            );
            requestedDateInput.focus();
            return false;
        }

        if (requestedDate < getTodayDate()) {
            showMessage(
                "Pickup date cannot be in the past.",
                "error"
            );
            requestedDateInput.focus();
            return false;
        }

        if (!quantityText ||
            !Number.isFinite(quantity) ||
            quantity <= 0 ||
            quantity > 99999999.99 ||
            !/^\d+(\.\d{1,2})?$/.test(quantityText)) {

            showMessage(
                "Enter a valid quantity greater than zero, " +
                "with up to two decimal places.",
                "error"
            );
            quantityInput.focus();
            return false;
        }

        if (!pickupAddress) {
            showMessage(
                "Please enter the complete pickup address.",
                "error"
            );
            addressInput.focus();
            return false;
        }

        if (pickupAddress.length > 255) {
            showMessage(
                "Pickup address cannot exceed 255 characters.",
                "error"
            );
            addressInput.focus();
            return false;
        }

        return true;
    }

    /* =====================================
       LOAD MY BULK PICKUP REQUESTS
    ===================================== */

    async function loadMyPickupRequests() {
        if (!isLoggedIn || !pickupRequestsTable) {
            return;
        }

        pickupRequestsTable.innerHTML =
            '<tr><td colspan="4">' +
            'Loading your requests...' +
            '</td></tr>';

        if (pickupHistoryMessage) {
            pickupHistoryMessage.textContent = "";
        }

        if (refreshRequestsBtn) {
            refreshRequestsBtn.disabled = true;
        }

        try {
            const requests = await fetchJSON(
                "/api/bulk-pickup-requests/my-requests"
            );

            if (!Array.isArray(requests)) {
                throw new Error(
                    "Invalid request history received."
                );
            }

            // Calculate request statistics
            const total = requests.length;

            const pending = requests.filter(
                request =>
                    String(
                        request.requestStatus || ""
                    ).toUpperCase() === "PENDING"
            ).length;

            const completed = requests.filter(
                request =>
                    String(
                        request.requestStatus || ""
                    ).toUpperCase() === "COMPLETED"
            ).length;

            totalPickupRequests.textContent = total;
            pendingPickupRequests.textContent = pending;
            completedPickupRequests.textContent = completed;

            // Clear old table rows
            pickupRequestsTable.innerHTML = "";

            if (requests.length === 0) {
                pickupRequestsTable.innerHTML =
                    '<tr><td colspan="4">' +
                    'No bulk pickup requests submitted yet.' +
                    '</td></tr>';
                return;
            }

            // Display each request
            requests.forEach(function (request) {

                const row = document.createElement("tr");

                const status = String(
                    request.requestStatus || "PENDING"
                ).toUpperCase();

                let statusClass = "other";

                if (status === "PENDING") {
                    statusClass = "pending";
                } else if (status === "COMPLETED") {
                    statusClass = "completed";
                } else if (status === "APPROVED") {
                    statusClass = "approved";
                } else if (status === "SCHEDULED") {
                    statusClass = "scheduled";
                } else if (status === "IN_PROGRESS") {
                    statusClass = "in-progress";
                } else if (status === "REJECTED") {
                    statusClass = "rejected";
                }

                // Request ID
                const idCell = document.createElement("td");
                idCell.textContent =
                    "#" + request.requestId;

                // Preferred pickup date
                const dateCell = document.createElement("td");
                dateCell.textContent =
                    request.requestedDate || "-";

                // Estimated quantity
                const quantityCell =
                    document.createElement("td");

                quantityCell.textContent =
                    request.estimatedQuantityKg != null
                        ? request.estimatedQuantityKg + " kg"
                        : "-";

                // Request status
                const statusCell =
                    document.createElement("td");

                const statusBadge =
                    document.createElement("span");

                statusBadge.className =
                    "pickup-status " + statusClass;

                statusBadge.textContent =
                    status.replace(/_/g, " ");

                statusCell.appendChild(statusBadge);

                row.appendChild(idCell);
                row.appendChild(dateCell);
                row.appendChild(quantityCell);
                row.appendChild(statusCell);

                pickupRequestsTable.appendChild(row);
            });

        } catch (error) {
            console.error(
                "Bulk pickup history error:",
                error
            );

            pickupRequestsTable.innerHTML =
                '<tr><td colspan="4">' +
                'Unable to load your requests.' +
                '</td></tr>';

            if (pickupHistoryMessage) {
                pickupHistoryMessage.textContent =
                    error.message;
            }

        } finally {
            if (refreshRequestsBtn) {
                refreshRequestsBtn.disabled = false;
            }
        }
    }

    /* =====================================
       SUBMIT BULK PICKUP REQUEST
    ===================================== */

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        if (isSubmitting || !isLoggedIn || !dataLoaded) {
            return;
        }

        clearMessage();
        updateMinimumDate();

        if (!validateForm()) {
            return;
        }

        const requestData = {
            areaId: Number(areaSelect.value),
            categoryId: Number(categorySelect.value),
            requestedDate: requestedDateInput.value,
            pickupAddress: addressInput.value.trim(),
            estimatedQuantityKg: Number(quantityInput.value)
        };

        isSubmitting = true;
        submitBtn.disabled = true;
        resetBtn.disabled = true;
        submitBtn.textContent = "Submitting...";

        try {
            const savedRequest = await fetchJSON(
                "/api/bulk-pickup-requests",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(requestData)
                }
            );

            // Reset form first
            form.reset();
            updateMinimumDate();

            // Show confirmation after resetting
            showMessage(
                "Bulk pickup request submitted successfully! " +
                "Your Request ID is " +
                savedRequest.requestId +
                ". Current status: " +
                (savedRequest.requestStatus || "PENDING") +
                ".",
                "success"
            );

            // Scroll to confirmation
            messageBox.scrollIntoView({
                behavior: "smooth",
                block: "center"
            });

            // Refresh request history independently.
            // A history-loading error must not turn a
            // successful submission into an error.
            await loadMyPickupRequests();

        } catch (error) {
            console.error(
                "Bulk pickup submission error:",
                error
            );

            showMessage(
                error.message ||
                "Unable to submit your request.",
                "error"
            );

        } finally {
            isSubmitting = false;
            submitBtn.disabled = !dataLoaded;
            resetBtn.disabled = false;
            submitBtn.textContent = "Submit Pickup Request";
        }
    });

    /* =====================================
       RESET FORM
    ===================================== */

    if (resetBtn) {
        resetBtn.addEventListener("click", function () {
            clearMessage();
        });
    }

    form.addEventListener("reset", function () {
        updateMinimumDate();
    });

    /* =====================================
       REFRESH REQUEST HISTORY
    ===================================== */

    if (refreshRequestsBtn) {
        refreshRequestsBtn.addEventListener(
            "click",
            loadMyPickupRequests
        );
    }

    /* =====================================
       LOGOUT
    ===================================== */

    if (logoutBtn) {
        logoutBtn.addEventListener(
            "click",
            async function (event) {
                event.preventDefault();

                try {
                    await fetchJSON(
                        "/api/users/logout",
                        {
                            method: "POST"
                        }
                    );

                    window.location.href = "login.html";

                } catch (error) {
                    showMessage(
                        "Logout failed. Please try again.",
                        "error"
                    );
                }
            }
        );
    }

    /* =====================================
       INITIALIZE PAGE
    ===================================== */

    async function initializePage() {
        submitBtn.disabled = true;
        updateMinimumDate();

        const validSession =
            await checkUserSession();

        if (!validSession) {
            return;
        }

        // Load previous requests even if form
        // dropdown data fails to load.
        await loadMyPickupRequests();

        try {
            await Promise.all([
                loadAreas(),
                loadCategories()
            ]);

            dataLoaded = true;
            submitBtn.disabled = false;

        } catch (error) {
            console.error(
                "Bulk pickup initialization error:",
                error
            );

            showMessage(
                error.message ||
                "Unable to load the form data.",
                "error"
            );
        }
    }

    await initializePage();
});

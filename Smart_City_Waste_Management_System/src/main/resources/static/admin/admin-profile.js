
document.addEventListener("DOMContentLoaded", function () {

    "use strict";

    // ==========================================
    // E-SAFAI ADMIN MY PROFILE
    // ==========================================

    const SESSION_API = "/api/users/session";
    const PROFILE_API = "/api/users/admin/profile";

    let currentAdmin = null;
    let isEditing = false;
    let isSaving = false;

    // ==========================================
    // HTML ELEMENTS
    // ==========================================

    const profileSection = document.getElementById("profile");
    const profileForm = document.getElementById("adminProfileForm");
    const profileMessage = document.getElementById("adminProfileMessage");

    const profileDisplayName = document.getElementById("profileDisplayName");
    const profileDisplayRole = document.getElementById("profileDisplayRole");

    const userIdInput = document.getElementById("adminProfileUserId");
    const roleInput = document.getElementById("adminProfileRole");
    const nameInput = document.getElementById("adminProfileName");
    const emailInput = document.getElementById("adminProfileEmail");
    const phoneInput = document.getElementById("adminProfilePhone");
    const addressInput = document.getElementById("adminProfileAddress");

    const editBtn = document.getElementById("editAdminProfileBtn");
    const saveBtn = document.getElementById("saveAdminProfileBtn");
    const cancelBtn = document.getElementById("cancelAdminProfileBtn");
    const refreshBtn = document.getElementById("refreshAdminProfileBtn");

    // ==========================================
    // CHECK REQUIRED HTML ELEMENTS
    // ==========================================

    const requiredElements = [
        profileSection,
        profileForm,
        userIdInput,
        roleInput,
        nameInput,
        emailInput,
        phoneInput,
        addressInput,
        editBtn,
        saveBtn,
        cancelBtn
    ];

    if (requiredElements.some(element => !element)) {
        console.error("Admin Profile: Required HTML elements are missing.");
        return;
    }

    // ==========================================
    // SHOW MESSAGE
    // ==========================================

    function showMessage(message, type = "success") {

        if (!profileMessage) return;

        profileMessage.textContent = message;
        profileMessage.className = "alert alert-" + type;
        profileMessage.classList.remove("d-none");
    }

    function clearMessage() {

        if (!profileMessage) return;

        profileMessage.textContent = "";
        profileMessage.className = "alert d-none";
    }

    // ==========================================
    // API REQUEST
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

        const contentType = response.headers.get("content-type") || "";

        let data = null;

        if (contentType.includes("application/json")) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) {

            let message = "Request failed (" + response.status + ")";

            if (data && typeof data === "object") {
                message = data.message || data.error || message;
            }

            if (response.status === 401) {
                message = "Session expired. Please log in again.";
            }

            if (response.status === 403) {
                message = "Access denied. Admin login required.";
            }

            throw new Error(message);
        }

        return data;
    }

    // ==========================================
    // DISPLAY PROFILE DATA
    // ==========================================

    function displayProfile(admin) {

        currentAdmin = admin;

        userIdInput.value = admin.userId ?? "";
        roleInput.value = admin.roleName || "";
        nameInput.value = admin.fullName || "";
        emailInput.value = admin.email || "";
        phoneInput.value = admin.phoneNumber || "";
        addressInput.value = admin.address || "";

        if (profileDisplayName) {
            profileDisplayName.textContent =
                admin.fullName || "Administrator";
        }

        if (profileDisplayRole) {
            profileDisplayRole.textContent =
                admin.roleName || "ADMIN";
        }

        const headerName = document.getElementById("adminName");

        if (headerName) {
            headerName.textContent =
                admin.fullName || "Administrator";
        }
    }

    // ==========================================
    // ENABLE / DISABLE EDIT MODE
    // ==========================================

    function setEditMode(editing) {

        isEditing = editing;

        // Only these three fields are editable.
        nameInput.readOnly = !editing;
        phoneInput.readOnly = !editing;
        addressInput.readOnly = !editing;

        // Always keep these fields read-only.
        userIdInput.readOnly = true;
        roleInput.readOnly = true;
        emailInput.readOnly = true;

        editBtn.classList.toggle("d-none", editing);
        saveBtn.classList.toggle("d-none", !editing);
        cancelBtn.classList.toggle("d-none", !editing);

        if (refreshBtn) {
            refreshBtn.disabled = editing || isSaving;
        }

        if (editing) {
            nameInput.focus();
        }
    }

    // ==========================================
    // LOAD ADMIN PROFILE
    // ==========================================

    async function loadAdminProfile() {

        if (isSaving) return;

        clearMessage();

        editBtn.disabled = true;

        try {

            const admin = await apiRequest(SESSION_API, {
                method: "GET"
            });

            if (!admin || admin.roleName !== "ADMIN") {
                throw new Error(
                    "Only administrators can access this profile."
                );
            }

            displayProfile(admin);
            setEditMode(false);

            editBtn.disabled = false;

        } catch (error) {

            currentAdmin = null;

            showMessage(
                "Unable to load Admin profile: " + error.message,
                "danger"
            );

            console.error("Admin profile loading error:", error);
        }
    }

    // ==========================================
    // EDIT PROFILE BUTTON
    // ==========================================

    editBtn.addEventListener("click", function () {

        if (!currentAdmin || isSaving) {
            showMessage("Please load your profile first.", "warning");
            return;
        }

        clearMessage();
        setEditMode(true);
    });

    // ==========================================
    // CANCEL EDIT BUTTON
    // ==========================================

    cancelBtn.addEventListener("click", function () {

        if (isSaving) return;

        clearMessage();

        if (currentAdmin) {
            displayProfile(currentAdmin);
        }

        setEditMode(false);
    });

    // ==========================================
    // SAVE ADMIN PROFILE
    // ==========================================

    profileForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        if (!isEditing || isSaving || !currentAdmin) return;

        clearMessage();

        const fullName = nameInput.value.trim();
        const phoneNumber = phoneInput.value.trim();
        const address = addressInput.value.trim();

        if (!fullName || !phoneNumber || !address) {
            showMessage("All three fields are required.", "danger");
            return;
        }

        if (fullName.length > 100) {
            showMessage("Name cannot exceed 100 characters.", "danger");
            return;
        }

        if (!/^[0-9+() -]{7,15}$/.test(phoneNumber)) {
            showMessage("Enter a valid phone number.", "danger");
            return;
        }

        if (address.length > 255) {
            showMessage("Address cannot exceed 255 characters.", "danger");
            return;
        }

        if (!profileForm.reportValidity()) return;

        const requestData = {
            fullName: fullName,
            phoneNumber: phoneNumber,
            address: address
        };

        isSaving = true;
        saveBtn.disabled = true;
        cancelBtn.disabled = true;
        saveBtn.textContent = "Saving...";

        try {

            const updatedAdmin = await apiRequest(PROFILE_API, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestData)
            });

            displayProfile(updatedAdmin);
            setEditMode(false);

            showMessage(
                "Admin profile updated successfully!",
                "success"
            );

        } catch (error) {

            showMessage(error.message, "danger");

            console.error("Admin profile update error:", error);

        } finally {

            isSaving = false;
            saveBtn.disabled = false;
            cancelBtn.disabled = false;
            saveBtn.textContent = "Save Changes";

            if (refreshBtn) {
                refreshBtn.disabled = isEditing;
            }
        }
    });

    // ==========================================
    // REFRESH BUTTON
    // ==========================================

    if (refreshBtn) {
        refreshBtn.addEventListener("click", loadAdminProfile);
    }

    // ==========================================
    // SIDEBAR MY PROFILE LINK
    // ==========================================

    document.querySelectorAll(
        '[data-section="profile"]'
    ).forEach(function (link) {

        link.addEventListener("click", function () {

            if (!isEditing) {
                loadAdminProfile();
            }
        });
    });

    // ==========================================
    // INITIAL LOAD
    // ==========================================

    setEditMode(false);
    loadAdminProfile();

});

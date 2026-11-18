
"use strict";

/* =====================================
   E-SAFAI - MY PROFILE JAVASCRIPT
===================================== */

document.addEventListener("DOMContentLoaded", async function () {
    // PROFILE DISPLAY ELEMENTS
    const profileAvatar =
        document.getElementById("profileAvatar");
    const profileName =
        document.getElementById("profileName");
    const profileEmail =
        document.getElementById("profileEmail");
    const profileRole =
        document.getElementById("profileRole");

    const userId =
        document.getElementById("userId");
    const fullName =
        document.getElementById("fullName");
    const email =
        document.getElementById("email");
    const phoneNumber =
        document.getElementById("phoneNumber");
    const address =
        document.getElementById("address");

    // EDIT FORM
    const profileDetails =
        document.getElementById("profileDetails");
    const editProfileForm =
        document.getElementById("editProfileForm");

    const editFullName =
        document.getElementById("editFullName");
    const editEmail =
        document.getElementById("editEmail");
    const editPhoneNumber =
        document.getElementById("editPhoneNumber");
    const editAddress =
        document.getElementById("editAddress");

    // BUTTONS AND MESSAGE
    const editProfileBtn =
        document.getElementById("editProfileBtn");
    const cancelEditBtn =
        document.getElementById("cancelEditBtn");
    const saveProfileBtn =
        document.getElementById("saveProfileBtn");
    const refreshBtn =
        document.getElementById("refreshProfileBtn");
    const logoutBtn =
        document.getElementById("logoutBtn");
    const messageBox =
        document.getElementById("profileMessage");

    let currentUser = null;
    let isSaving = false;


    /* =====================================
       MESSAGE HELPERS
    ===================================== */

    function showMessage(message, type) {
        messageBox.textContent = message;
        messageBox.className = "message-box " + type;
    }

    function clearMessage() {
        messageBox.textContent = "";
        messageBox.className = "message-box";
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
                "Unexpected response from server."
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

            if (response.status === 401) {
                window.location.href = "login.html";
                throw new Error(
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
       DISPLAY PROFILE
    ===================================== */

    function displayProfile(user) {

        currentUser = user;

        profileName.textContent =
            user.fullName || "-";

        profileEmail.textContent =
            user.email || "-";

        profileRole.textContent =
            user.roleName === "USER"
                ? "Citizen"
                : (user.roleName || "-");

        profileAvatar.textContent =
            user.fullName
                ? user.fullName.trim()
                    .charAt(0).toUpperCase()
                : "U";

        userId.textContent =
            user.userId != null
                ? user.userId
                : "-";

        fullName.textContent =
            user.fullName || "-";

        email.textContent =
            user.email || "-";

        phoneNumber.textContent =
            user.phoneNumber || "-";

        address.textContent =
            user.address || "-";

        editProfileBtn.disabled = false;
    }


    /* =====================================
       LOAD PROFILE
    ===================================== */

    async function loadProfile() {

        refreshBtn.disabled = true;
        refreshBtn.textContent = "Loading...";

        clearMessage();

        try {

            const user = await fetchJSON(
                "/api/users/session"
            );

            if (user.userId == null ||
                user.roleName !== "USER") {

                window.location.href = "login.html";
                return;
            }

            displayProfile(user);

        } catch (error) {

            console.error(
                "Profile loading error:",
                error
            );

            showMessage(
                error.message ||
                "Unable to load your profile.",
                "error"
            );

        } finally {

            refreshBtn.disabled = false;
            refreshBtn.textContent = "Refresh Profile";
        }
    }


    /* =====================================
       OPEN EDIT FORM
    ===================================== */

    function openEditForm() {

        if (!currentUser || isSaving) {
            return;
        }

        clearMessage();

        editFullName.value =
            currentUser.fullName || "";

        editEmail.value =
            currentUser.email || "";

        editPhoneNumber.value =
            currentUser.phoneNumber || "";

        editAddress.value =
            currentUser.address || "";

        profileDetails.hidden = true;
        editProfileForm.hidden = false;

        editProfileBtn.hidden = true;
        refreshBtn.disabled = true;

        editFullName.focus();
    }


    /* =====================================
       CLOSE EDIT FORM
    ===================================== */

    function closeEditForm() {

        editProfileForm.hidden = true;
        profileDetails.hidden = false;

        editProfileBtn.hidden = false;
        refreshBtn.disabled = false;
    }


    /* =====================================
       VALIDATE EDIT FORM
    ===================================== */

    function validateEditForm() {

        const name = editFullName.value.trim();
        const emailValue = editEmail.value.trim();
        const phone = editPhoneNumber.value.trim();
        const addressValue = editAddress.value.trim();

        if (!name || !emailValue ||
            !phone || !addressValue) {

            showMessage(
                "All profile fields are required.",
                "error"
            );

            return false;
        }

        if (name.length > 100 ||
            emailValue.length > 255 ||
            phone.length > 20 ||
            addressValue.length > 255) {

            showMessage(
                "One or more fields are too long.",
                "error"
            );

            return false;
        }

        const phonePattern = /^[0-9+() -]{7,20}$/;

        if (!phonePattern.test(phone)) {

            showMessage(
                "Please enter a valid phone number.",
                "error"
            );

            editPhoneNumber.focus();
            return false;
        }

        return true;
    }


    /* =====================================
       SAVE PROFILE CHANGES
    ===================================== */

    editProfileForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            if (isSaving || !currentUser) {
                return;
            }

            clearMessage();

            if (!validateEditForm()) {
                return;
            }

            const updatedData = {
                fullName: editFullName.value.trim(),
                email: editEmail.value.trim(),
                phoneNumber:
                    editPhoneNumber.value.trim(),
                address: editAddress.value.trim()
            };

            isSaving = true;

            saveProfileBtn.disabled = true;
            cancelEditBtn.disabled = true;

            saveProfileBtn.textContent =
                "Saving Changes...";

            try {

                const updatedUser = await fetchJSON(
                    "/api/users/profile",
                    {
                        method: "PUT",
                        headers: {
                            "Content-Type":
                                "application/json"
                        },
                        body: JSON.stringify(updatedData)
                    }
                );

                displayProfile(updatedUser);

                closeEditForm();

                showMessage(
                    "Your profile has been updated successfully!",
                    "success"
                );

            } catch (error) {

                console.error(
                    "Profile update error:",
                    error
                );

                showMessage(
                    error.message ||
                    "Unable to update your profile.",
                    "error"
                );

            } finally {

                isSaving = false;

                saveProfileBtn.disabled = false;
                cancelEditBtn.disabled = false;

                saveProfileBtn.textContent =
                    "Save Changes";
            }
        }
    );


    /* =====================================
       BUTTON EVENTS
    ===================================== */

    editProfileBtn.addEventListener(
        "click",
        openEditForm
    );

    cancelEditBtn.addEventListener(
        "click",
        function () {
            if (!isSaving) {
                closeEditForm();
                clearMessage();
            }
        }
    );

    refreshBtn.addEventListener(
        "click",
        loadProfile
    );


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

                    window.location.href =
                        "login.html";

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

    await loadProfile();

});


document.addEventListener("DOMContentLoaded", function () {

    const API_BASE = "/api";
    const MAX_IMAGES = 5;
    const MAX_FILE_SIZE = 5 * 1024 * 1024;

    const form = document.getElementById("reportWasteForm");
    const categorySelect = document.getElementById("category");
    const areaSelect = document.getElementById("area");

    const imageInput = document.getElementById("complaintImages");
    const selectedImages = document.getElementById("selectedImages");

    const messageBox = document.getElementById("messageBox");
    const submitBtn = document.getElementById("submitBtn");
    const resetBtn = document.getElementById("resetBtn");
    const logoutBtn = document.getElementById("logoutBtn");

    let selectedFiles = [];

    // Display message
    function showMessage(message, type) {
        messageBox.textContent = message;
        messageBox.className = "message-box " + type;
    }

    // Clear message
    function clearMessage() {
        messageBox.textContent = "";
        messageBox.className = "message-box";
    }

    // Read server error response
    async function getErrorMessage(response) {
        const text = await response.text();

        if (!text) {
            return "Request failed. Please try again.";
        }

        try {
            const data = JSON.parse(text);
            return data.message || data.error || text;
        } catch (error) {
            return text;
        }
    }

    // Check login session
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
                showMessage("Unable to verify login session.", "error");
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

    // Load areas
    async function loadAreas() {
        try {
            const response = await fetch(API_BASE + "/areas");

            if (!response.ok) {
                throw new Error("Unable to load areas.");
            }

            const areas = await response.json();

            areaSelect.innerHTML =
                '<option value="">Select Your Area</option>';

            areas.forEach(function (area) {
                const option = document.createElement("option");

                option.value = area.areaId;
                option.textContent = area.areaName;

                areaSelect.appendChild(option);
            });

        } catch (error) {
            showMessage(error.message, "error");
        }
    }

    // Load waste categories
    async function loadWasteCategories() {
        try {
            const response = await fetch(API_BASE + "/waste-categories");

            if (!response.ok) {
                throw new Error("Unable to load waste categories.");
            }

            const categories = await response.json();

            categorySelect.innerHTML =
                '<option value="">Select Waste Category</option>';

            categories.forEach(function (category) {
                const option = document.createElement("option");

                option.value = category.categoryId;
                option.textContent = category.categoryName;

                categorySelect.appendChild(option);
            });

        } catch (error) {
            showMessage(error.message, "error");
        }
    }

    // Validate selected images
    function validateImages(files) {

        if (files.length > MAX_IMAGES) {
            return "You can select a maximum of 5 images.";
        }

        const allowedTypes = [
            "image/jpeg",
            "image/png",
            "image/webp"
        ];

        for (const file of files) {

            if (!allowedTypes.includes(file.type)) {
                return "Only JPG, PNG and WEBP images are allowed.";
            }

            if (file.size > MAX_FILE_SIZE) {
                return file.name + " exceeds the 5 MB size limit.";
            }

            if (file.size === 0) {
                return file.name + " is empty.";
            }
        }

        return null;
    }

    // Display selected image names
    function displaySelectedImages(files) {

        selectedImages.innerHTML = "";

        if (files.length === 0) {
            return;
        }

        const heading = document.createElement("p");
        heading.textContent = "Selected Images:";
        selectedImages.appendChild(heading);

        files.forEach(function (file) {

            const item = document.createElement("p");
            item.textContent = file.name;

            selectedImages.appendChild(item);
        });
    }

    // Handle image selection
    imageInput.addEventListener("change", function () {

        clearMessage();

        const files = Array.from(imageInput.files);

        const error = validateImages(files);

        if (error) {
            imageInput.value = "";
            selectedFiles = [];
            selectedImages.innerHTML = "";
            showMessage(error, "error");
            return;
        }

        selectedFiles = files;
        displaySelectedImages(selectedFiles);
    });

    // Validate location coordinates
    function validateCoordinates(latitude, longitude) {

        if ((latitude === "") !== (longitude === "")) {
            return "Please enter both latitude and longitude.";
        }

        if (latitude !== "" && longitude !== "") {

            const lat = Number(latitude);
            const lng = Number(longitude);

            if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
                return "Please enter valid coordinates.";
            }

            if (lat < -90 || lat > 90) {
                return "Latitude must be between -90 and 90.";
            }

            if (lng < -180 || lng > 180) {
                return "Longitude must be between -180 and 180.";
            }
        }

        return null;
    }

    // Upload one image
    async function uploadImage(complaintId, file) {

        const formData = new FormData();

        formData.append("file", file);

        const response = await fetch(
            API_BASE + "/complaint-images/upload/" + complaintId,
            {
                method: "POST",
                credentials: "same-origin",
                body: formData
            }
        );

        if (!response.ok) {
            throw new Error(await getErrorMessage(response));
        }

        return await response.json();
    }

    // Submit complaint
    form.addEventListener("submit", async function (event) {

        event.preventDefault();
        clearMessage();

        const description =
            document.getElementById("description").value.trim();

        const areaId = areaSelect.value;
        const categoryId = categorySelect.value;

        const locationAddress =
            document.getElementById("locationAddress").value.trim();

        const latitude =
            document.getElementById("latitude").value.trim();

        const longitude =
            document.getElementById("longitude").value.trim();

        // Basic validation
        if (!description || !areaId || !categoryId || !locationAddress) {
            showMessage("Please fill in all required fields.", "error");
            return;
        }

        const coordinateError =
            validateCoordinates(latitude, longitude);

        if (coordinateError) {
            showMessage(coordinateError, "error");
            return;
        }

        const imageError = validateImages(selectedFiles);

        if (imageError) {
            showMessage(imageError, "error");
            return;
        }

        // Complaint JSON
        const complaintData = {
            description: description,
            locationAddress: locationAddress,

            area: {
                areaId: Number(areaId)
            },

            category: {
                categoryId: Number(categoryId)
            },

            latitude: latitude === "" ? null : Number(latitude),
            longitude: longitude === "" ? null : Number(longitude)
        };

        submitBtn.disabled = true;
        submitBtn.textContent = "Submitting...";

        try {

            // Step 1: Save complaint
            const complaintResponse = await fetch(
                API_BASE + "/complaints",
                {
                    method: "POST",
                    credentials: "same-origin",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(complaintData)
                }
            );

            if (!complaintResponse.ok) {
                throw new Error(
                    await getErrorMessage(complaintResponse)
                );
            }

            const savedComplaint = await complaintResponse.json();

            const complaintId = savedComplaint.complaintId;

            if (!complaintId) {
                throw new Error(
                    "Complaint was saved, but its ID was not returned."
                );
            }

            // Step 2: Upload selected images
            
            // Step 2: Upload selected images
            let uploadedCount = 0;
            let failedCount = 0;

            for (const file of selectedFiles) {
                try {
                    await uploadImage(complaintId, file);
                    uploadedCount++;
                } catch (uploadError) {
                    failedCount++;
                    console.error(
                        "Failed to upload " + file.name,
                        uploadError
                    );
                }
            }

            // Step 3: Display final submission result
            if (failedCount === 0) {
                showMessage(
                    "Complaint submitted successfully! " +
                    "Your Complaint ID is " + complaintId + ". " +
                    uploadedCount + " image(s) uploaded.",
                    "success"
                );
            } else {
                showMessage(
                    "Complaint #" + complaintId +
                    " was saved successfully. " +
                    uploadedCount + " image(s) uploaded, but " +
                    failedCount + " image(s) failed. " +
                    "Please do not submit the same complaint again.",
                    "error"
                );
            }



            // Reset form after successful submission
            form.reset();

            selectedFiles = [];
            selectedImages.innerHTML = "";

        } catch (error) {

            showMessage(
                error.message ||
                "Something went wrong. Please try again.",
                "error"
            );

        } finally {

            submitBtn.disabled = false;
            submitBtn.textContent = "Submit Complaint";
        }
    });

    // Reset form
    resetBtn.addEventListener("click", function () {

        selectedFiles = [];
        selectedImages.innerHTML = "";
        clearMessage();
    });

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

    // Initialize page
    async function initializePage() {

        const isLoggedIn = await checkLoginSession();

        if (isLoggedIn) {
            await Promise.all([
                loadAreas(),
                loadWasteCategories()
            ]);
        }
    }

    initializePage();

});

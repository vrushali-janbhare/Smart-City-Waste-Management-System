
document.addEventListener("DOMContentLoaded", async function () {

    const form = document.getElementById("feedbackForm");
    const complaintSelect = document.getElementById("complaintId");
    const commentsInput = document.getElementById("comments");
    const submitBtn = document.getElementById("submitBtn");
    const ratingText = document.getElementById("ratingText");
    const messageBox = document.getElementById("messageBox");
    const historyContainer = document.getElementById("feedbackHistory");
    const logoutBtn = document.getElementById("logoutBtn");

    const stars = document.querySelectorAll(".star");

    let selectedRating = 0;
    let complaints = [];
    let feedbackList = [];
    let isSubmitting = false;
    let isLoaded = false;


    // 1. Display success or error messages
    function showMessage(message, type = "error") {

        messageBox.textContent = message;
        messageBox.className = "message " + type;
        messageBox.hidden = false;

        // Make the message visible to screen readers
        messageBox.setAttribute("role", "status");
        messageBox.setAttribute("aria-live", "polite");

        // Scroll to the message
        messageBox.scrollIntoView({
            behavior: "smooth",
            block: "center"
        });
    }


    // 2. Hide message
    function clearMessage() {

        messageBox.hidden = true;
        messageBox.textContent = "";
        messageBox.className = "message";
    }


    // 3. Read server response
    async function readResponse(response) {

        const text = await response.text();

        if (!text) {
            return null;
        }

        try {
            return JSON.parse(text);
        } catch (error) {

            throw new Error(
                "Unexpected server response. Please check the backend."
            );
        }
    }


    // 4. Verify citizen session
    async function checkSession() {

        const response = await fetch("/api/users/session", {
            credentials: "same-origin"
        });

        if (!response.ok) {

            window.location.href = "login.html";
            return false;
        }

        const user = await readResponse(response);

        if (!user || user.roleName !== "USER") {

            window.location.href = "login.html";
            return false;
        }

        return true;
    }


    // 5. Update selected stars
    function updateStars(rating) {

        stars.forEach(function (star) {

            const value = Number(star.dataset.rating);

            star.classList.toggle(
                "selected",
                value <= rating
            );

            star.setAttribute(
                "aria-pressed",
                String(value === rating)
            );
        });

        const labels = {
            0: "Select your rating",
            1: "1 Star - Very Poor",
            2: "2 Stars - Poor",
            3: "3 Stars - Average",
            4: "4 Stars - Good",
            5: "5 Stars - Excellent"
        };

        ratingText.textContent = labels[rating];
    }


    // 6. Handle star selection
    stars.forEach(function (star) {

        star.addEventListener("click", function () {

            selectedRating = Number(star.dataset.rating);

            updateStars(selectedRating);
            updateSubmitButton();
        });
    });


    // 7. Enable or disable submit button
    function updateSubmitButton() {

        submitBtn.disabled =
            !isLoaded ||
            isSubmitting ||
            !complaintSelect.value ||
            selectedRating < 1 ||
            selectedRating > 5;
    }


    // 8. Load citizen complaints
    async function loadComplaints() {

        const response = await fetch(
            "/api/complaints/my-complaints",
            {
                credentials: "same-origin"
            }
        );

        const data = await readResponse(response);

        if (!response.ok) {

            throw new Error(
                data && data.message
                    ? data.message
                    : "Failed to load complaints"
            );
        }

        if (!Array.isArray(data)) {

            throw new Error(
                "Complaint API did not return a list"
            );
        }

        complaints = data;
    }


    // 9. Load feedback history
    async function loadFeedbackHistory() {

        const response = await fetch(
            "/api/feedback/my-feedback",
            {
                credentials: "same-origin"
            }
        );

        const data = await readResponse(response);

        if (!response.ok) {

            throw new Error(
                data && data.message
                    ? data.message
                    : "Failed to load feedback history"
            );
        }

        if (!Array.isArray(data)) {

            throw new Error(
                "Feedback API did not return a list"
            );
        }

        feedbackList = data;
    }


    // 10. Update statistics
    function updateStats() {

        const resolvedComplaints = complaints.filter(
            complaint => complaint.status === "RESOLVED"
        );

        const submittedIds = new Set(
            feedbackList.map(
                feedback => String(feedback.complaintId)
            )
        );

        const awaitingFeedback = resolvedComplaints.filter(
            complaint =>
                !submittedIds.has(
                    String(complaint.complaintId)
                )
        );

        document.getElementById("resolvedCount").textContent =
            resolvedComplaints.length;

        document.getElementById("feedbackCount").textContent =
            feedbackList.length;

        document.getElementById("pendingCount").textContent =
            awaitingFeedback.length;
    }


    // 11. Display available resolved complaints
    function renderComplaintOptions() {

        const previousValue = complaintSelect.value;

        complaintSelect.innerHTML = "";

        const defaultOption = document.createElement("option");

        defaultOption.value = "";
        defaultOption.textContent = "Select a resolved complaint";

        complaintSelect.appendChild(defaultOption);

        const submittedIds = new Set(
            feedbackList.map(
                feedback => String(feedback.complaintId)
            )
        );

        const availableComplaints = complaints.filter(
            complaint =>
                complaint.status === "RESOLVED" &&
                !submittedIds.has(
                    String(complaint.complaintId)
                )
        );

        availableComplaints.forEach(function (complaint) {

            const option = document.createElement("option");

            option.value = complaint.complaintId;

            const description =
                complaint.description || "Waste complaint";

            const shortDescription =
                description.length > 55
                    ? description.substring(0, 55) + "..."
                    : description;

            option.textContent =
                "#" + complaint.complaintId +
                " - " + shortDescription;

            complaintSelect.appendChild(option);
        });

        if (availableComplaints.length === 0) {

            defaultOption.textContent =
                "No resolved complaints awaiting feedback";
        }

        const stillAvailable = availableComplaints.some(
            complaint =>
                String(complaint.complaintId) === previousValue
        );

        complaintSelect.value =
            stillAvailable ? previousValue : "";

        updateSubmitButton();
    }


    // 12. Display feedback history
    function renderHistory() {

        historyContainer.innerHTML = "";

        if (feedbackList.length === 0) {

            const empty = document.createElement("div");

            empty.className = "empty-state";
            empty.textContent =
                "No feedback submitted yet. Your feedback will appear here.";

            historyContainer.appendChild(empty);
            return;
        }

        feedbackList.forEach(function (feedback) {

            const card = document.createElement("article");
            card.className = "history-item";

            const top = document.createElement("div");
            top.className = "history-top";

            const complaintTag = document.createElement("span");
            complaintTag.className = "complaint-tag";

            complaintTag.textContent =
                "Complaint #" + feedback.complaintId;

            const rating = Math.max(
                0,
                Math.min(5, Number(feedback.rating) || 0)
            );

            const starDisplay = document.createElement("span");

            starDisplay.className = "history-stars";

            starDisplay.textContent =
                "★".repeat(rating) +
                "☆".repeat(5 - rating);

            top.appendChild(complaintTag);
            top.appendChild(starDisplay);

            const description = document.createElement("p");

            description.className = "history-description";

            description.textContent =
                feedback.complaintDescription ||
                "Waste complaint";

            const location = document.createElement("p");

            location.className = "history-location";

            location.textContent =
                "📍 " +
                (feedback.locationAddress || "Location unavailable");

            card.appendChild(top);
            card.appendChild(description);
            card.appendChild(location);

            if (feedback.comments) {

                const comments = document.createElement("p");

                comments.className = "history-comments";
                comments.textContent = feedback.comments;

                card.appendChild(comments);
            }

            historyContainer.appendChild(card);
        });
    }


    // 13. Refresh feedback information
    async function loadPageData() {

        isLoaded = false;
        updateSubmitButton();

        try {

            await Promise.all([
                loadComplaints(),
                loadFeedbackHistory()
            ]);

            isLoaded = true;

            renderComplaintOptions();
            renderHistory();
            updateStats();

            return true;

        } catch (error) {

            isLoaded = false;

            throw error;

        } finally {

            updateSubmitButton();
        }
    }


    // 14. Submit feedback
    form.addEventListener("submit", async function (event) {

        event.preventDefault();

        clearMessage();

        if (!complaintSelect.value) {

            showMessage(
                "Please select a resolved complaint.",
                "error"
            );
            return;
        }

        if (selectedRating < 1 || selectedRating > 5) {

            showMessage(
                "Please select a rating from 1 to 5 stars.",
                "error"
            );
            return;
        }

        if (isSubmitting) {
            return;
        }

        isSubmitting = true;
        updateSubmitButton();

        const selectedComplaintId = Number(
            complaintSelect.value
        );

        let submissionSucceeded = false;

        try {

            const payload = {
                complaintId: selectedComplaintId,
                rating: selectedRating,
                comments: commentsInput.value.trim()
            };

            const response = await fetch("/api/feedback", {

                method: "POST",

                credentials: "same-origin",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(payload)
            });

            const result = await readResponse(response);

            if (!response.ok) {

                throw new Error(
                    result && result.message
                        ? result.message
                        : "Failed to submit feedback."
                );
            }

            // Feedback has been saved successfully
            submissionSucceeded = true;

            // Show success immediately
            showMessage(
                "Your feedback has been submitted successfully!",
                "success"
            );

            // Reset the form
            form.reset();

            selectedRating = 0;
            updateStars(0);

            // Refresh feedback history separately
            try {

                await loadPageData();

            } catch (refreshError) {

                console.error(
                    "Feedback saved, but refresh failed:",
                    refreshError
                );

                showMessage(
                    "Your feedback was submitted successfully, " +
                    "but the feedback history could not be refreshed. " +
                    "Please reload the page.",
                    "success"
                );
            }

        } catch (error) {

            if (!submissionSucceeded) {

                showMessage(
                    error.message ||
                    "Something went wrong while submitting feedback.",
                    "error"
                );
            }

        } finally {

            isSubmitting = false;
            updateSubmitButton();
        }
    });


    // 15. Update button when complaint changes
    complaintSelect.addEventListener(
        "change",
        updateSubmitButton
    );


    // 16. Logout
    logoutBtn.addEventListener("click", async function () {

        try {

            await fetch("/api/users/logout", {
                method: "POST",
                credentials: "same-origin"
            });

        } catch (error) {

            console.error("Logout error:", error);

        } finally {

            window.location.href = "login.html";
        }
    });


    // 17. Initial page setup
    updateStars(0);

    try {

        const loggedIn = await checkSession();

        if (loggedIn) {

            try {

                await loadPageData();

            } catch (error) {

                showMessage(
                    error.message ||
                    "Unable to load feedback information.",
                    "error"
                );

                historyContainer.innerHTML = "";

                const empty = document.createElement("div");

                empty.className = "empty-state";

                empty.textContent =
                    "Unable to load feedback information.";

                historyContainer.appendChild(empty);
            }
        }

    } catch (error) {

        showMessage(
            "Unable to verify your login session. Please try again.",
            "error"
        );
    }

});

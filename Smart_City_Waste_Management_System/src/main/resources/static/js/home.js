
"use strict";

// ==========================================
// E-SAFAI - HOME PAGE
// PUBLIC CITIZEN TESTIMONIALS
// ==========================================

document.addEventListener("DOMContentLoaded", function () {

    // Public Feedback API
    const FEEDBACK_API = "/api/feedback/public";

    // HTML elements
    const feedbackGrid =
        document.getElementById("testimonialsGrid");

    const feedbackStatus =
        document.getElementById("testimonialsStatus");

    // Check required HTML elements
    if (!feedbackGrid || !feedbackStatus) {
        console.error(
            "Testimonials section elements not found."
        );
        return;
    }

    // ==========================================
    // 1. SHOW STATUS MESSAGE
    // ==========================================

    function showStatus(message) {

        feedbackStatus.textContent = message;
        feedbackStatus.hidden = false;
    }

    function hideStatus() {

        feedbackStatus.textContent = "";
        feedbackStatus.hidden = true;
    }

    // ==========================================
    // 2. CREATE STAR RATING
    // ==========================================

    function createStars(rating) {

        const stars = document.createElement("div");

        stars.className = "testimonial-stars";

        const value = Math.max(
            0,
            Math.min(5, Math.round(Number(rating) || 0))
        );

        // Unicode stars
        stars.textContent =
            "\u2605".repeat(value) +
            "\u2606".repeat(5 - value);

        stars.setAttribute(
            "aria-label",
            value + " out of 5 stars"
        );

        return stars;
    }

    // ==========================================
    // 3. CREATE FEEDBACK CARD
    // ==========================================

    function createFeedbackCard(feedback) {

        const card = document.createElement("article");
        card.className = "testimonial-card";

        // Rating stars
        const stars = createStars(feedback.rating);

        // Citizen comment
        const comment = document.createElement("p");
        comment.className = "testimonial-comment";
        comment.textContent = feedback.comments;

        // Footer label
        const label = document.createElement("div");
        label.className = "testimonial-label";
        label.textContent = "Citizen Feedback";

        // Add elements
        card.appendChild(stars);
        card.appendChild(comment);
        card.appendChild(label);

        return card;
    }

    // ==========================================
    // 4. DISPLAY APPROVED FEEDBACK
    // ==========================================

    function displayFeedback(feedbackList) {

        // Clear existing cards
        feedbackGrid.replaceChildren();

        // Keep feedback with valid comments
        const validFeedback = feedbackList.filter(
            feedback =>
                feedback &&
                typeof feedback.comments === "string" &&
                feedback.comments.trim() !== ""
        );

        // Display latest 6 feedback records
        const latestFeedback = validFeedback.slice(0, 6);

        if (latestFeedback.length === 0) {

            showStatus(
                "No citizen testimonials are available yet."
            );

            return;
        }

        // Hide loading message
        hideStatus();

        // Create testimonial cards
        latestFeedback.forEach(feedback => {

            const card = createFeedbackCard(feedback);

            feedbackGrid.appendChild(card);
        });
    }

    // ==========================================
    // 5. LOAD FEEDBACK FROM SPRING BOOT
    // ==========================================

    async function loadPublicFeedback() {

        showStatus("Loading citizen feedback...");

        try {

            const response = await fetch(FEEDBACK_API, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                },
                credentials: "same-origin"
            });

            if (!response.ok) {

                throw new Error(
                    "Failed to load feedback. HTTP " +
                    response.status
                );
            }

            const feedbackList = await response.json();

            if (!Array.isArray(feedbackList)) {

                throw new Error(
                    "Invalid feedback response."
                );
            }

            displayFeedback(feedbackList);

        } catch (error) {

            console.error(
                "E-Safai Public Feedback Error:",
                error
            );

            feedbackGrid.replaceChildren();

            showStatus(
                "Unable to load citizen testimonials right now."
            );
        }
    }

    // ==========================================
    // 6. INITIALIZE
    // ==========================================

    loadPublicFeedback();

});

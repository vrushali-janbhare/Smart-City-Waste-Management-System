
"use strict";

// ==========================================
// E-SAFAI - ADMIN MANAGE FEEDBACK
// ==========================================

(function () {

    const API = "/api/feedback";

    let records = [];
    let loading = false;

    const el = id => document.getElementById(id);

    // ==========================================
    // 1. ALERT MESSAGES
    // ==========================================

    function showAlert(message, type) {

        const box = el("feedbackAlert");

        if (!box) return;

        box.className = "alert alert-" + (type || "danger");
        box.textContent = message;
    }

    function hideAlert() {

        const box = el("feedbackAlert");

        if (box) {
            box.className = "alert d-none";
            box.textContent = "";
        }
    }

    // ==========================================
    // 2. API REQUEST
    // ==========================================

    async function request(path, options) {

        const response = await fetch(API + path, {
            credentials: "same-origin",
            headers: {
                "Accept": "application/json"
            },
            ...options
        });

        const contentType =
            response.headers.get("content-type") || "";

        const body = contentType.includes("application/json")
            ? await response.json()
            : await response.text();

        if (!response.ok) {

            const message =
                body && typeof body === "object"
                    ? (body.message || body.error)
                    : null;

            throw new Error(
                message ||
                (response.status === 401
                    ? "Please log in as an administrator."
                    : response.status === 403
                        ? "Administrator access is required."
                        : "Request failed (HTTP " +
                          response.status + ").")
            );
        }

        return body;
    }

    // ==========================================
    // 3. CREATE TABLE CELL
    // ==========================================

    function td(text, className) {

        const cell = document.createElement("td");

        cell.textContent = String(
            text == null ? "" : text
        );

        if (className) {
            cell.className = className;
        }

        return cell;
    }

    // ==========================================
    // 4. DISPLAY FEEDBACK TABLE
    // ==========================================

    function render() {

        const tbody = el("feedbackTableBody");

        if (!tbody) return;

        const search = el("feedbackSearch")
            .value.trim().toLowerCase();

        const rating =
            el("feedbackRatingFilter").value;

        const visibility =
            el("feedbackVisibilityFilter").value;

        // Search and filters
        const filtered = records.filter(item => {

            const matchesSearch =
                String(item.feedbackId).includes(search) ||
                String(item.comments || "")
                    .toLowerCase()
                    .includes(search);

            const matchesRating =
                rating === "ALL" ||
                String(item.rating) === rating;

            const matchesVisibility =
                visibility === "ALL" ||
                (visibility === "PUBLIC"
                    ? item.isPublic === true
                    : item.isPublic !== true);

            return matchesSearch &&
                   matchesRating &&
                   matchesVisibility;
        });

        tbody.replaceChildren();

        // Empty table message
        if (filtered.length === 0) {

            const row = document.createElement("tr");

            const cell = td(
                "No matching feedback found.",
                "text-center text-muted py-4"
            );

            cell.colSpan = 5;

            row.appendChild(cell);
            tbody.appendChild(row);
        }

        // Display records
        filtered.forEach(item => {

            const row = document.createElement("tr");

            // Feedback ID
            row.appendChild(td(item.feedbackId));

            // Rating stars
            const starCount = Math.max(
                0,
                Math.min(5, Number(item.rating) || 0)
            );

            const ratingCell = td(
                "★".repeat(starCount) +
                "☆".repeat(5 - starCount) +
                " (" + item.rating + ")",
                "feedback-stars"
            );

            row.appendChild(ratingCell);

            // Citizen comment
            row.appendChild(
                td(
                    item.comments || "No written comment",
                    "feedback-comment"
                )
            );

            // Public / Private status
            const visibilityCell =
                document.createElement("td");

            const badge =
                document.createElement("span");

            badge.className =
                item.isPublic === true
                    ? "badge bg-success"
                    : "badge bg-secondary";

            badge.textContent =
                item.isPublic === true
                    ? "Public"
                    : "Private";

            visibilityCell.appendChild(badge);

            row.appendChild(visibilityCell);

            // Action buttons
            const actionCell =
                document.createElement("td");

            actionCell.className = "feedback-action";

            if (
                item.isPublic !== true &&
                !(item.comments || "").trim()
            ) {

                actionCell.textContent =
                    "No comment to publish";

                actionCell.classList.add("text-muted");

            } else {

                const button =
                    document.createElement("button");

                button.type = "button";

                button.className =
                    item.isPublic === true
                        ? "btn btn-sm btn-outline-danger"
                        : "btn btn-sm btn-outline-success";

                button.textContent =
                    item.isPublic === true
                        ? "Remove from Public"
                        : "Approve for Public";

                button.addEventListener(
                    "click",
                    () => updateVisibility(item, button)
                );

                actionCell.appendChild(button);
            }

            row.appendChild(actionCell);

            tbody.appendChild(row);
        });

        // Public feedback count
        el("feedbackPublicCount").textContent =
            records.filter(
                item => item.isPublic === true
            ).length;

        // Search results count
        el("feedbackResultsCount").textContent =
            "Showing " + filtered.length +
            " of " + records.length +
            " feedback records";
    }

    // ==========================================
    // 5. APPROVE / REMOVE PUBLIC FEEDBACK
    // ==========================================

    async function updateVisibility(item, button) {

        const makePublic = item.isPublic !== true;

        const message = makePublic
            ? "Confirm the citizen has consented " +
              "to public display and the comment " +
              "contains no personal information. " +
              "Approve this feedback?"
            : "Remove this feedback from " +
              "the public homepage?";

        if (!window.confirm(message)) {
            return;
        }

        button.disabled = true;

        hideAlert();

        try {

            const updated = await request(
                "/admin/" +
                encodeURIComponent(item.feedbackId) +
                "/visibility?isPublic=" + makePublic,
                {
                    method: "PUT"
                }
            );

            item.isPublic = updated.isPublic === true;

            render();

            showAlert(
                item.isPublic
                    ? "Feedback approved for public display."
                    : "Feedback removed from public display.",
                "success"
            );

        } catch (error) {

            showAlert(error.message);

            button.disabled = false;
        }
    }

    // ==========================================
    // 6. LOAD FEEDBACK FROM MYSQL
    // ==========================================

    async function load() {

        if (loading) return;

        loading = true;

        hideAlert();

        const refresh = el("refreshFeedbackBtn");

        if (refresh) {
            refresh.disabled = true;
        }

        try {

            const [items, stats] = await Promise.all([
                request("/admin/all"),
                request("/admin/stats")
            ]);

            records = Array.isArray(items)
                ? items
                : [];

            // Total feedback
            el("feedbackTotalCount").textContent =
                stats.totalFeedback ?? records.length;

            // Average rating
            el("feedbackAverageRating").textContent =
                Number(
                    stats.averageRating || 0
                ).toFixed(1) + " / 5";

            // Display feedback
            render();

        } catch (error) {

            showAlert(error.message);

            const tbody = el("feedbackTableBody");

            if (tbody) {

                tbody.replaceChildren();

                const row =
                    document.createElement("tr");

                const cell = td(
                    "Could not load feedback.",
                    "text-center text-muted py-4"
                );

                cell.colSpan = 5;

                row.appendChild(cell);
                tbody.appendChild(row);
            }

        } finally {

            loading = false;

            if (refresh) {
                refresh.disabled = false;
            }
        }
    }

    // Make loading available to admin-dashboard.js
    window.loadAdminFeedback = load;

    // ==========================================
    // 7. EVENT LISTENERS
    // ==========================================

    document.addEventListener(
        "DOMContentLoaded",
        function () {

            // Search and filters
            [
                "feedbackSearch",
                "feedbackRatingFilter",
                "feedbackVisibilityFilter"
            ].forEach(id => {

                const control = el(id);

                if (control) {

                    control.addEventListener(
                        id === "feedbackSearch"
                            ? "input"
                            : "change",
                        render
                    );
                }
            });

            // Refresh button
            const refresh = el("refreshFeedbackBtn");

            if (refresh) {

                refresh.addEventListener(
                    "click",
                    load
                );
            }
        }
    );

})();

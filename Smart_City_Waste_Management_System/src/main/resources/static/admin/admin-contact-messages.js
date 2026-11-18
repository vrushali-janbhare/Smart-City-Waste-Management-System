
document.addEventListener("DOMContentLoaded", function () {
    "use strict";

    const API = "/api/contact-messages";
    const body = document.getElementById("contactMessagesTableBody");
    const search = document.getElementById("contactMessageSearch");
    const filter = document.getElementById("contactMessageStatusFilter");
    const modalElement = document.getElementById("contactMessageDetailsModal");
    if (!body || !modalElement) return;

    let messages = [];
    let selectedId = null;

    function text(id, value) {
        const element = document.getElementById(id);
        if (element) element.textContent = value == null || value === "" ? "-" : String(value);
    }

    function notice(message, success) {
        const element = document.getElementById("contactMessagesAlert");
        if (!element) return;
        element.className = "alert " + (success ? "alert-success" : "alert-danger");
        element.textContent = message;
    }

    async function request(url, options = {}) {
        const response = await fetch(url, {
            credentials: "same-origin",
            ...options
        });

        const contentType = response.headers.get("content-type") || "";
        const data = contentType.includes("application/json")
            ? await response.json()
            : await response.text();

        if (!response.ok) {
            const message = typeof data === "object" && data !== null
                ? (data.message || data.error || "Request failed")
                : (data || "Request failed");

            throw new Error(message + " (HTTP " + response.status + ")");
        }

        return data;
    }

    function formatDate(value) {
        if (!value) return "-";
        const date = new Date(value);
        return Number.isNaN(date.getTime())
            ? String(value)
            : date.toLocaleString("en-IN");
    }

    function button(label, classes, callback) {
        const element = document.createElement("button");
        element.type = "button";
        element.className = classes;
        element.textContent = label;
        element.addEventListener("click", callback);
        return element;
    }

    function showEmpty(message) {
        body.replaceChildren();

        const row = body.insertRow();
        const cell = row.insertCell();

        cell.colSpan = 7;
        cell.className = "text-center text-muted py-4";
        cell.textContent = message;
    }

    function updateCounts() {
        text("contactTotalCount", messages.length);

        ["NEW", "READ", "RESOLVED"].forEach(status => {
            const count = messages.filter(
                item => item.status === status
            ).length;

            text(
                "contact" + status.charAt(0)
                + status.slice(1).toLowerCase() + "Count",
                count
            );
        });
    }

    function render() {
        const query = (search.value || "").toLowerCase().trim();
        const status = filter.value;

        const filtered = messages.filter(item => {
            const matchesStatus =
                status === "ALL" || item.status === status;

            const haystack = [
                item.id,
                item.fullName,
                item.email,
                item.subject,
                item.enquiryType,
                item.message
            ].join(" ").toLowerCase();

            return matchesStatus && haystack.includes(query);
        });

        text(
            "contactMessagesResultCount",
            "Showing " + filtered.length
            + " of " + messages.length + " messages"
        );

        if (!filtered.length) {
            showEmpty("No matching contact messages found.");
            return;
        }

        body.replaceChildren();

        filtered.forEach(item => {
            const row = body.insertRow();

            [
                item.id,
                item.fullName,
                item.email,
                item.subject || "-",
                formatDate(item.createdAt)
            ].forEach(value => {
                row.insertCell().textContent =
                    value == null ? "-" : String(value);
            });

            const badge = document.createElement("span");

            badge.className = "badge " + ({
                NEW: "bg-warning text-dark",
                READ: "bg-info text-dark",
                RESOLVED: "bg-success"
            }[item.status] || "bg-secondary");

            badge.textContent = item.status || "UNKNOWN";
            row.insertCell().appendChild(badge);

            const actions = document.createElement("div");
            actions.className = "contact-message-actions";

            actions.appendChild(
                button(
                    "View",
                    "btn btn-sm btn-outline-primary",
                    () => openDetails(item.id)
                )
            );

            actions.appendChild(
                button(
                    "Delete",
                    "btn btn-sm btn-outline-danger",
                    () => deleteMessage(item.id)
                )
            );

            row.insertCell().appendChild(actions);
        });
    }

    async function loadMessages() {
        showEmpty("Loading contact messages...");

        try {
            const result = await request(API);

            if (!Array.isArray(result)) {
                throw new Error("Unexpected server response");
            }

            messages = result;

            messages.sort(
                (a, b) => (b.id || 0) - (a.id || 0)
            );

            updateCounts();
            render();

        } catch (error) {
            showEmpty("Unable to load contact messages.");
            notice(error.message, false);
        }
    }

    async function openDetails(id) {
        try {
            const item = await request(
                API + "/" + encodeURIComponent(id)
            );

            selectedId = item.id;

            text("contactDetailName", item.fullName);
            text("contactDetailEmail", item.email);
            text("contactDetailSubject", item.subject);
            text("contactDetailType", item.enquiryType);
            text("contactDetailDate", formatDate(item.createdAt));
            text("contactDetailMessage", item.message);

            document.getElementById("contactDetailStatus").value =
                item.status || "NEW";

            bootstrap.Modal.getOrCreateInstance(
                modalElement
            ).show();

        } catch (error) {
            notice(error.message, false);
        }
    }

    async function saveStatus() {
        if (selectedId == null) return;

        const buttonElement =
            document.getElementById("saveContactStatusBtn");

        buttonElement.disabled = true;

        try {
            const status =
                document.getElementById("contactDetailStatus").value;

            await request(
                API + "/" + encodeURIComponent(selectedId),
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ status })
                }
            );

            bootstrap.Modal.getOrCreateInstance(
                modalElement
            ).hide();

            notice("Message status updated successfully.", true);

            await loadMessages();

        } catch (error) {
            notice(error.message, false);
        } finally {
            buttonElement.disabled = false;
        }
    }

    async function deleteMessage(id) {
        if (!window.confirm(
            "Delete contact message #" + id
            + "? This cannot be undone."
        )) {
            return;
        }

        try {
            await request(
                API + "/" + encodeURIComponent(id),
                { method: "DELETE" }
            );

            notice("Contact message deleted successfully.", true);

            await loadMessages();

        } catch (error) {
            notice(error.message, false);
        }
    }

    search.addEventListener("input", render);
    filter.addEventListener("change", render);

    document.getElementById("refreshContactMessagesBtn")
        .addEventListener("click", loadMessages);

    document.getElementById("saveContactStatusBtn")
        .addEventListener("click", saveStatus);

    document.querySelector(
        '[data-section="contact-messages"]'
    )?.addEventListener("click", loadMessages);

    window.addEventListener("hashchange", () => {
        if (window.location.hash === "#contact-messages") {
            loadMessages();
        }
    });

    if (window.location.hash === "#contact-messages") {
        loadMessages();
    }
});


 // E-Safai Admin — Bulk Pickup Requests
 // Uses the existing session-authenticated Admin APIs.

document.addEventListener("DOMContentLoaded", function () {
    "use strict";

    const section = document.getElementById("bulk-pickups");
    if (!section) return;

    const endpoint = "/api/bulk-pickup-requests/admin";

    const tbody = document.getElementById("bulkPickupTableBody");
    const search = document.getElementById("bulkPickupSearch");
    const filter = document.getElementById("bulkPickupStatusFilter");
    const message = document.getElementById("bulkPickupMessage");
    const resultCount = document.getElementById("bulkPickupResultsCount");

    let requests = [];
    let loading = false;

    async function api(url, options = {}) {
        const response = await fetch(url, {
            credentials: "same-origin",
            ...options
        });

        if (!response.ok) {
            let detail = "Request failed (" + response.status + ")";

            try {
                const body = await response.json();
                detail = body.message || detail;
            } catch (_) {}

            throw new Error(detail);
        }

        return response.json();
    }

    function notice(text, success = true) {
        message.textContent = text;
        message.className = "alert " +
            (success ? "alert-success" : "alert-danger");
    }

    function clearNotice() {
        message.textContent = "";
        message.className = "alert d-none";
    }

    function set(id, value) {
        const el = document.getElementById(id);

        if (el) {
            el.textContent = value;
        }
    }

    function dateText(value) {
        if (!value) return "—";

        const parts = String(value).split("-");

        if (parts.length === 3) {
            const d = new Date(
                Number(parts[0]),
                Number(parts[1]) - 1,
                Number(parts[2])
            );

            if (!Number.isNaN(d.getTime())) {
                return d.toLocaleDateString("en-IN", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric"
                });
            }
        }

        return String(value);
    }

    function statusBadge(status) {
        const badge = document.createElement("span");

        const classes = {
            PENDING: "bg-warning text-dark",
            APPROVED: "bg-info text-dark",
            IN_PROGRESS: "bg-primary",
            COMPLETED: "bg-success",
            REJECTED: "bg-danger"
        };

        badge.className = "badge " +
            (classes[status] || "bg-secondary");

        badge.textContent = String(status || "UNKNOWN")
            .replace(/_/g, " ");

        return badge;
    }

    function empty(text, error = false) {
        tbody.replaceChildren();

        const row = tbody.insertRow();
        const cell = row.insertCell();

        cell.colSpan = 8;

        cell.className = "text-center py-4 " +
            (error ? "text-danger" : "text-muted");

        cell.textContent = text;
    }

    function updateStats() {
        set("bulkTotal", requests.length);

        for (const [status, id] of Object.entries({
            PENDING: "bulkPending",
            APPROVED: "bulkApproved",
            IN_PROGRESS: "bulkInProgress",
            COMPLETED: "bulkCompleted",
            REJECTED: "bulkRejected"
        })) {
            set(
                id,
                requests.filter(r =>
                    r.requestStatus === status
                ).length
            );
        }
    }

    function action(label, style, handler) {
        const button = document.createElement("button");

        button.type = "button";
        button.className = "btn btn-sm " + style;
        button.textContent = label;

        button.addEventListener("click", handler);

        return button;
    }

    function render() {
        const query = search.value.trim().toLowerCase();
        const status = filter.value;

        const visible = requests.filter(r => {
            const fields = [
                r.requestId,
                r.citizenName,
                r.areaName,
                r.categoryName,
                r.pickupAddress
            ];

            return (
                status === "ALL" ||
                r.requestStatus === status
            ) && fields.some(v =>
                String(v ?? "")
                    .toLowerCase()
                    .includes(query)
            );
        });

        resultCount.textContent =
            "Showing " + visible.length +
            " of " + requests.length + " requests";

        tbody.replaceChildren();

        if (!visible.length) {
            empty("No matching bulk pickup requests found.");
            return;
        }

        visible.forEach(r => {
            const row = tbody.insertRow();

            [
                r.requestId,
                r.citizenName || "—",
                r.areaName || "—",
                r.categoryName || "—",
                r.estimatedQuantityKg == null
                    ? "—"
                    : r.estimatedQuantityKg + " kg",
                dateText(r.requestedDate)
            ].forEach(value => {
                row.insertCell().textContent = value;
            });

            row.insertCell().appendChild(
                statusBadge(r.requestStatus)
            );

            const actions = document.createElement("div");
            actions.className = "bulk-actions";

            actions.appendChild(
                action(
                    "View",
                    "btn-outline-primary",
                    () => view(r.requestId)
                )
            );

            if (r.requestStatus === "PENDING") {
                actions.appendChild(
                    action(
                        "Approve",
                        "btn-outline-success",
                        () => changeStatus(r, "APPROVED")
                    )
                );

                actions.appendChild(
                    action(
                        "Reject",
                        "btn-outline-danger",
                        () => changeStatus(r, "REJECTED")
                    )
                );

            } else if (r.requestStatus === "APPROVED") {
                actions.appendChild(
                    action(
                        "Start Pickup",
                        "btn-outline-success",
                        () => changeStatus(r, "IN_PROGRESS")
                    )
                );

            } else if (r.requestStatus === "IN_PROGRESS") {
                actions.appendChild(
                    action(
                        "Complete",
                        "btn-outline-success",
                        () => changeStatus(r, "COMPLETED")
                    )
                );
            }

            row.insertCell().appendChild(actions);
        });
    }

    async function load() {
        if (loading) return;

        loading = true;
        clearNotice();

        empty("Loading bulk pickup requests...");

        try {
            const data = await api(endpoint + "/all");

            if (!Array.isArray(data)) {
                throw new Error(
                    "Unexpected response from server"
                );
            }

            requests = data;

            updateStats();
            render();

        } catch (error) {
            requests = [];

            updateStats();
            resultCount.textContent = "";

            empty(
                "Unable to load requests: " + error.message,
                true
            );

            notice(error.message, false);

        } finally {
            loading = false;
        }
    }

    async function view(id) {
        try {
            const r = await api(
                endpoint + "/" + encodeURIComponent(id)
            );

            const container = document.getElementById(
                "bulkPickupDetailsContent"
            );

            container.replaceChildren();

            const values = [
                ["Request ID", r.requestId],
                ["Citizen", r.citizenName],
                ["Citizen ID", r.userId],
                ["Area", r.areaName],
                ["Category", r.categoryName],
                [
                    "Estimated Quantity",
                    r.estimatedQuantityKg == null
                        ? "—"
                        : r.estimatedQuantityKg + " kg"
                ],
                [
                    "Preferred Pickup Date",
                    dateText(r.requestedDate)
                ],
                [
                    "Status",
                    String(r.requestStatus || "—")
                        .replace(/_/g, " ")
                ],
                ["Pickup Address", r.pickupAddress]
            ];

            values.forEach(([label, value]) => {
                const dt = document.createElement("dt");
                dt.className = "col-sm-4";
                dt.textContent = label;

                const dd = document.createElement("dd");
                dd.className = "col-sm-8";
                dd.textContent = value ?? "—";

                container.append(dt, dd);
            });

            if (typeof bootstrap === "undefined") {
                throw new Error(
                    "Bootstrap modal library is unavailable"
                );
            }

            bootstrap.Modal.getOrCreateInstance(
                document.getElementById(
                    "bulkPickupDetailsModal"
                )
            ).show();

        } catch (error) {
            notice(
                "Unable to view request: " + error.message,
                false
            );
        }
    }

    async function changeStatus(request, newStatus) {
        const verb = {
            APPROVED: "approve",
            REJECTED: "reject",
            IN_PROGRESS: "start pickup for",
            COMPLETED: "complete"
        }[newStatus];

        if (!window.confirm(
            "Are you sure you want to " +
            verb + " request #" +
            request.requestId + "?"
        )) {
            return;
        }

        try {
            await api(
                endpoint + "/" +
                encodeURIComponent(request.requestId) +
                "/status",
                {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        requestStatus: newStatus
                    })
                }
            );

            await load();

            if (requests.some(r =>
                r.requestId === request.requestId &&
                r.requestStatus === newStatus
            )) {
                notice(
                    "Request #" + request.requestId +
                    " updated to " +
                    newStatus.replace(/_/g, " ") + "."
                );
            }

        } catch (error) {
            notice(
                "Unable to update request: " +
                error.message,
                false
            );
        }
    }

    search.addEventListener("input", render);
    filter.addEventListener("change", render);

    document.getElementById(
        "refreshBulkPickupsBtn"
    ).addEventListener("click", load);

    document.querySelectorAll(
        '.sidebar-nav .nav-link[data-section="bulk-pickups"]'
    ).forEach(link =>
        link.addEventListener("click", load)
    );

    window.addEventListener("hashchange", () => {
        if (location.hash === "#bulk-pickups") {
            load();
        }
    });

    if (
        location.hash === "#bulk-pickups" ||
        section.classList.contains("active-section")
    ) {
        load();
    }
});

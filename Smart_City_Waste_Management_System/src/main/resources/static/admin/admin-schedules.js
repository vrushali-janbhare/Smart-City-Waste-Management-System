
"use strict";

// E-Safai Admin Collection Schedules
// Uses the authenticated Admin session and the schedule management API.

document.addEventListener("DOMContentLoaded", () => {
    const root = document.getElementById("schedules");
    if (!root) return;

    const API = "/api/admin/collection-schedules";
    const $ = id => document.getElementById(id);

    const body = $("schedulesTableBody");
    const form = $("scheduleForm");
    const modalElement = $("scheduleModal");

    let schedules = [];
    let editingId = null;
    let busy = false;

    const daysOrder = [
        "MONDAY", "TUESDAY", "WEDNESDAY",
        "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    ];

    function alertAt(id, message, type = "danger") {
        const el = $(id);
        if (!el) return;

        el.textContent = message || "";
        el.className = message
            ? `alert alert-${type}`
            : "alert d-none";
    }

    async function request(url, options = {}) {
        const response = await fetch(url, {
            ...options,
            credentials: "same-origin",
            headers: {
                "Accept": "application/json",
                ...(options.body
                    ? { "Content-Type": "application/json" }
                    : {}),
                ...(options.headers || {})
            }
        });

        const text = await response.text();
        let data;

        try {
            data = text ? JSON.parse(text) : null;
        } catch {
            data = text;
        }

        if (!response.ok) {
            const msg = typeof data === "object" && data !== null
                ? (data.message || data.error)
                : (typeof data === "string" &&
                   !data.startsWith("<") ? data : null);

            throw new Error(
                msg || `Request failed (${response.status})`
            );
        }

        return data;
    }

    function statusBadge(status) {
        const badge = document.createElement("span");

        const cls = {
            ACTIVE: "bg-success",
            INACTIVE: "bg-secondary",
            COMPLETED: "bg-primary"
        };

        badge.className = `badge ${cls[status] || "bg-secondary"}`;
        badge.textContent = status || "—";

        return badge;
    }

    function button(label, className, callback) {
        const b = document.createElement("button");

        b.type = "button";
        b.className = className;
        b.textContent = label;
        b.addEventListener("click", callback);

        return b;
    }

    function cell(row, value) {
        row.insertCell().textContent =
            value == null || value === ""
                ? "—"
                : String(value);
    }

    function render() {
        const q = $("scheduleSearch").value.trim().toLowerCase();
        const filter = $("scheduleFilter").value;

        const visible = schedules.filter(s => {
            const haystack = [
                s.scheduleId,
                s.scheduleName,
                s.areaName,
                s.vehicleNumber
            ].join(" ").toLowerCase();

            return haystack.includes(q) &&
                (filter === "ALL" || s.scheduleStatus === filter);
        });

        $("scheduleTotal").textContent = schedules.length;

        for (const status of ["ACTIVE", "INACTIVE", "COMPLETED"]) {
            const ids = {
                ACTIVE: "scheduleActive",
                INACTIVE: "scheduleInactive",
                COMPLETED: "scheduleCompleted"
            };

            $(ids[status]).textContent =
                schedules.filter(s => s.scheduleStatus === status).length;
        }

        $("scheduleResults").textContent =
            `Showing ${visible.length} of ${schedules.length} schedules`;

        body.replaceChildren();

        if (!visible.length) {
            const row = body.insertRow();
            const td = row.insertCell();

            td.colSpan = 8;
            td.className = "text-center text-muted py-4";
            td.textContent = schedules.length
                ? "No matching schedules."
                : "No schedules created yet.";

            return;
        }

        for (const s of visible) {
            const row = body.insertRow();

            cell(row, s.scheduleId);
            cell(row, s.scheduleName);
            cell(row, s.areaName);
            cell(row, s.vehicleNumber);

            cell(
                row,
                (s.collectionDays || [])
                    .slice()
                    .sort((a, b) =>
                        daysOrder.indexOf(a) - daysOrder.indexOf(b)
                    )
                    .map(d => d.slice(0, 3))
                    .join(", ")
            );

            cell(
                row,
                `${s.startTime || "—"} – ${s.endTime || "—"}`
            );

            row.insertCell().appendChild(
                statusBadge(s.scheduleStatus)
            );

            const actions = document.createElement("div");
            actions.className = "schedule-actions";

            actions.append(
                button(
                    "View",
                    "btn btn-sm btn-outline-primary",
                    () => view(s)
                ),
                button(
                    "Edit",
                    "btn btn-sm btn-outline-success",
                    () => openForm(s)
                ),
                button(
                    "Status",
                    "btn btn-sm btn-outline-secondary",
                    () => changeStatus(s)
                ),
                button(
                    "Delete",
                    "btn btn-sm btn-outline-danger",
                    () => remove(s)
                )
            );

            row.insertCell().appendChild(actions);
        }
    }

    async function load() {
        try {
            const result = await request(API);

            if (!Array.isArray(result)) {
                throw new Error("Unexpected schedule response.");
            }

            schedules = result;
            render();
            alertAt("scheduleMessage", "");

        } catch (e) {
            body.replaceChildren();

            const td = body.insertRow().insertCell();
            td.colSpan = 8;
            td.className = "text-center text-danger py-4";
            td.textContent = "Unable to load schedules.";

            alertAt("scheduleMessage", e.message);
        }
    }

    function options(select, items, idKey, nameFn, selected = []) {
        select.replaceChildren();

        if (!select.multiple) {
            const blank = document.createElement("option");

            blank.value = "";
            blank.textContent = "Select an option";
            select.appendChild(blank);
        }

        for (const item of items) {
            const id = item[idKey];
            if (id == null) continue;

            const opt = document.createElement("option");

            opt.value = String(id);
            opt.textContent = nameFn(item);
            opt.selected = selected.map(String).includes(String(id));

            select.appendChild(opt);
        }
    }

    async function openForm(s = null) {
        editingId = s ? s.scheduleId : null;

        form.reset();
        alertAt("scheduleFormMessage", "");

        $("scheduleModalTitle").textContent = s
            ? `Edit Schedule #${s.scheduleId}`
            : "Add Schedule";

        $("saveScheduleBtn").disabled = true;

        try {
            const [areas, vehicles, workers] = await Promise.all([
                request("/api/areas"),
                request("/api/vehicles"),
                request("/api/admin/complaints/workers")
            ]);

            if (![areas, vehicles, workers].every(Array.isArray)) {
                throw new Error(
                    "Area, vehicle or worker list is invalid."
                );
            }

            options(
                $("scheduleArea"),
                areas,
                "areaId",
                a => `${a.areaName || "Area"} (ID ${a.areaId})`,
                s ? [s.areaId] : []
            );

            options(
                $("scheduleVehicle"),
                vehicles,
                "vehicleId",
                v => `${v.vehicleNumber || "Vehicle"} (ID ${v.vehicleId})`,
                s ? [s.vehicleId] : []
            );

            options(
                $("scheduleWorkers"),
                workers,
                "workerId",
                w => `${w.fullName || w.user?.fullName || "Worker"} (${w.employeeCode || `ID ${w.workerId}`})`,
                s ? s.workerIds : []
            );

            if (s) {
                $("scheduleName").value = s.scheduleName || "";
                $("scheduleStartDate").value = s.startDate || "";
                $("scheduleEndDate").value = s.endDate || "";

                $("scheduleStartTime").value =
                    (s.startTime || "").slice(0, 5);

                $("scheduleEndTime").value =
                    (s.endTime || "").slice(0, 5);

                $("scheduleStatus").value =
                    s.scheduleStatus || "ACTIVE";
            }

            document.querySelectorAll(
                '#scheduleDays input[type="checkbox"]'
            ).forEach(cb => {
                cb.checked = !!s?.collectionDays?.includes(cb.value);
            });

            if (!areas.length || !vehicles.length || !workers.length) {
                alertAt(
                    "scheduleFormMessage",
                    "Please register at least one area, vehicle and worker before saving."
                );
            } else {
                $("saveScheduleBtn").disabled = false;
            }

            bootstrap.Modal.getOrCreateInstance(modalElement).show();

        } catch (e) {
            alertAt(
                "scheduleMessage",
                `Unable to open schedule form: ${e.message}`
            );
        }
    }

    function view(s) {
        const sorted = (s.collectionDays || [])
            .slice()
            .sort((a, b) =>
                daysOrder.indexOf(a) - daysOrder.indexOf(b)
            );

        window.alert(
            `Schedule #${s.scheduleId}\n` +
            `Name: ${s.scheduleName}\n` +
            `Area: ${s.areaName}\n` +
            `Vehicle: ${s.vehicleNumber}\n` +
            `Dates: ${s.startDate} to ${s.endDate || "Open-ended"}\n` +
            `Time: ${s.startTime} – ${s.endTime}\n` +
            `Days: ${sorted.join(", ")}\n` +
            `Worker IDs: ${(s.workerIds || []).join(", ")}\n` +
            `Status: ${s.scheduleStatus}`
        );
    }

    form.addEventListener("submit", async e => {
        e.preventDefault();

        if (busy) return;

        const payload = {
            scheduleName: $("scheduleName").value.trim(),
            areaId: Number($("scheduleArea").value),
            vehicleId: Number($("scheduleVehicle").value),

            startDate: $("scheduleStartDate").value,
            endDate: $("scheduleEndDate").value || null,

            startTime: $("scheduleStartTime").value,
            endTime: $("scheduleEndTime").value,

            scheduleStatus: $("scheduleStatus").value,

            collectionDays: [
                ...document.querySelectorAll(
                    '#scheduleDays input:checked'
                )
            ].map(el => el.value),

            workerIds: [
                ...$("scheduleWorkers").selectedOptions
            ].map(o => Number(o.value))
        };

        if (
            !payload.areaId ||
            !payload.vehicleId ||
            !payload.collectionDays.length ||
            !payload.workerIds.length
        ) {
            alertAt(
                "scheduleFormMessage",
                "Select an area, vehicle, at least one day and at least one worker."
            );
            return;
        }

        if (
            payload.endDate &&
            payload.endDate < payload.startDate
        ) {
            alertAt(
                "scheduleFormMessage",
                "End date cannot be before start date."
            );
            return;
        }

        if (payload.endTime <= payload.startTime) {
            alertAt(
                "scheduleFormMessage",
                "End time must be later than start time."
            );
            return;
        }

        busy = true;
        $("saveScheduleBtn").disabled = true;

        try {
            await request(
                editingId ? `${API}/${editingId}` : API,
                {
                    method: editingId ? "PUT" : "POST",
                    body: JSON.stringify(payload)
                }
            );

            bootstrap.Modal.getOrCreateInstance(modalElement).hide();

            await load();

            alertAt(
                "scheduleMessage",
                editingId
                    ? "Schedule updated successfully."
                    : "Schedule created successfully.",
                "success"
            );

        } catch (e) {
            alertAt("scheduleFormMessage", e.message);

        } finally {
            busy = false;
            $("saveScheduleBtn").disabled = false;
        }
    });

    async function changeStatus(s) {
        const next = window.prompt(
            `Change status for #${s.scheduleId}. Enter ACTIVE, INACTIVE or COMPLETED:`,
            s.scheduleStatus
        );

        if (next === null) return;

        const status = next.trim().toUpperCase();

        if (!["ACTIVE", "INACTIVE", "COMPLETED"].includes(status)) {
            alertAt("scheduleMessage", "Invalid status.");
            return;
        }

        if (status === s.scheduleStatus) return;

        try {
            await request(
                `${API}/${s.scheduleId}/status`,
                {
                    method: "PATCH",
                    body: JSON.stringify({
                        scheduleStatus: status
                    })
                }
            );

            await load();

            alertAt(
                "scheduleMessage",
                "Schedule status updated.",
                "success"
            );

        } catch (e) {
            alertAt("scheduleMessage", e.message);
        }
    }

    async function remove(s) {
        if (!window.confirm(
            `Delete schedule #${s.scheduleId} (${s.scheduleName})? This cannot be undone.`
        )) {
            return;
        }

        try {
            await request(
                `${API}/${s.scheduleId}`,
                { method: "DELETE" }
            );

            await load();

            alertAt(
                "scheduleMessage",
                "Schedule deleted.",
                "success"
            );

        } catch (e) {
            alertAt("scheduleMessage", e.message);
        }
    }

    $("addScheduleBtn").addEventListener(
        "click",
        () => openForm()
    );

    $("refreshSchedulesBtn").addEventListener(
        "click",
        load
    );

    $("scheduleSearch").addEventListener(
        "input",
        render
    );

    $("scheduleFilter").addEventListener(
        "change",
        render
    );

    document.querySelector(
        '.sidebar-nav [data-section="schedules"]'
    )?.addEventListener("click", load);

    window.addEventListener("hashchange", () => {
        if (location.hash === "#schedules") {
            load();
        }
    });

    if (location.hash === "#schedules") {
        load();
    }
});

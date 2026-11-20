"use strict";
/* Run immediately before page-specific scripts register DOMContentLoaded listeners.
   Reuses the ORIGINAL logout element so each page's existing logout handler remains intact. */
(function () {
    const oldHeader = document.querySelector("body > header.topbar");
    if (!oldHeader) return;
    const logout = oldHeader.querySelector("#logoutBtn");
    if (!logout) { console.error("Citizen sidebar: logout control missing"); return; }

    const menuItems = [
        ["user-dashboard.html", "▦", "Dashboard"],
        ["report-waste.html", "＋", "Report Waste"],
        ["my-complaints.html", "▤", "My Complaints"],
        ["track-complaint.html", "⌖", "Track Complaint"],
        ["bulk-pickup.html", "🚛", "Bulk Pickup"],
        ["feedback.html", "☆", "Feedback"]
    ];
    const current = window.location.pathname.split("/").pop() || "user-dashboard.html";
    const sidebar = document.createElement("aside");
    sidebar.className = "citizen-sidebar";
    sidebar.id = "citizenSidebar";
    sidebar.setAttribute("aria-label", "Citizen navigation");

    const logo = document.createElement("a");
    logo.className = "citizen-logo";
    logo.href = "user-dashboard.html";
    logo.innerHTML = '<span class="citizen-logo-icon">♻</span><span><strong>E-Safai</strong><small>Smart City Initiative</small></span>';
    sidebar.appendChild(logo);

    function heading(label, extraClass) {
        const el = document.createElement("div");
        el.className = "citizen-menu-heading" + (extraClass ? " " + extraClass : "");
        el.textContent = label;
        sidebar.appendChild(el);
    }
    function nav(items) {
        const container = document.createElement("nav");
        container.className = "citizen-menu";
        for (const [href, symbol, label] of items) {
            const link = document.createElement("a");
            link.className = "citizen-nav-link";
            link.href = href;
            if (current === href) { link.classList.add("active"); link.setAttribute("aria-current", "page"); }
            const icon = document.createElement("span");
            icon.className = "citizen-nav-icon";
            icon.setAttribute("aria-hidden", "true");
            icon.textContent = symbol;
            link.append(icon, document.createTextNode(label));
            container.appendChild(link);
        }
        sidebar.appendChild(container);
        return container;
    }
    heading("MAIN MENU");
    nav(menuItems);
    heading("ACCOUNT", "citizen-account-heading");
    const account = nav([["my-profile.html", "♙", "My Profile"]]);
    logout.className = "citizen-nav-link citizen-logout-link";
    if (logout.tagName === "A") logout.setAttribute("href", "#");
    const logoutIcon = document.createElement("span");
    logoutIcon.className = "citizen-nav-icon";
    logoutIcon.setAttribute("aria-hidden", "true");
    logoutIcon.textContent = "↪";
    logout.replaceChildren(logoutIcon, document.createTextNode("Logout"));
    account.appendChild(logout);

    const bottom = document.createElement("div");
    bottom.className = "citizen-sidebar-bottom";
    bottom.innerHTML = '<span class="plant">🌱</span><strong>Keep Your City Clean!</strong><p>Small actions make a big difference.</p>';
    sidebar.appendChild(bottom);

    const toggle = document.createElement("button");
    toggle.type = "button";
    toggle.className = "citizen-menu-toggle";
    toggle.setAttribute("aria-controls", "citizenSidebar");
    toggle.setAttribute("aria-expanded", "false");
    toggle.textContent = "☰  E-Safai Menu";
    const overlay = document.createElement("div");
    overlay.className = "citizen-menu-overlay";
    function closeMenu() { document.body.classList.remove("citizen-menu-open"); toggle.setAttribute("aria-expanded", "false"); }
    toggle.addEventListener("click", function () {
        const open = document.body.classList.toggle("citizen-menu-open");
        toggle.setAttribute("aria-expanded", String(open));
    });
    overlay.addEventListener("click", closeMenu);
    document.addEventListener("keydown", function (event) { if (event.key === "Escape") closeMenu(); });

    oldHeader.remove();
    document.body.classList.add("citizen-shell");
    document.body.prepend(overlay);
    document.body.prepend(toggle);
    document.body.prepend(sidebar);
})();


document.addEventListener("DOMContentLoaded", function () {

    // ==========================================
    // E-SAFAI - ADMIN MANAGE WASTE CATEGORIES
    // ==========================================

    const API_URL = "/api/waste-categories";

    let allCategories = [];

    const categoriesTableBody =
        document.getElementById("categoriesTableBody");

    const categorySearch =
        document.getElementById("categorySearch");


    // ==========================================
    // 1. COMMON HELPER FUNCTIONS
    // ==========================================

    async function categoryApiRequest(url, options = {}) {

        const response = await fetch(url, {
            ...options,
            credentials: "same-origin"
        });

        const contentType =
            response.headers.get("content-type") || "";

        let data = null;

        if (response.status !== 204) {
            if (contentType.includes("application/json")) {
                data = await response.json();
            } else {
                data = await response.text();
            }
        }

        if (!response.ok) {

            let message =
                "Request failed (" + response.status + ")";

            if (typeof data === "string" && data.trim()) {
                message = data;
            } else if (data && typeof data === "object") {
                message = data.message ||
                    data.error ||
                    message;
            }

            throw new Error(message);
        }

        return data;
    }


    function setCategoryText(id, value) {

        const element = document.getElementById(id);

        if (element) {
            element.textContent = value ?? "—";
        }
    }


    function setCategoryInput(id, value) {

        const element = document.getElementById(id);

        if (element) {
            element.value = value ?? "";
        }
    }


    function showCategoryMessage(message, success = true) {

        const element =
            document.getElementById("categoryActionMessage");

        if (!element) {
            return;
        }

        element.className = success
            ? "alert alert-success"
            : "alert alert-danger";

        element.textContent = message;
    }


    function showCategoryModal(id) {

        const element = document.getElementById(id);

        if (!element || typeof bootstrap === "undefined") {
            console.error("Bootstrap modal unavailable:", id);
            return;
        }

        bootstrap.Modal
            .getOrCreateInstance(element)
            .show();
    }


    function hideCategoryModal(id) {

        const element = document.getElementById(id);

        if (!element || typeof bootstrap === "undefined") {
            return;
        }

        bootstrap.Modal
            .getOrCreateInstance(element)
            .hide();
    }


    function showCategoryTableMessage(
        message,
        isError = false
    ) {

        if (!categoriesTableBody) {
            return;
        }

        categoriesTableBody.replaceChildren();

        const row = categoriesTableBody.insertRow();
        const cell = row.insertCell();

        cell.colSpan = 5;

        cell.className = isError
            ? "text-center text-danger py-4"
            : "text-center text-muted py-4";

        cell.textContent = message;
    }


    function createCategoryButton(
        label,
        className,
        callback
    ) {

        const button = document.createElement("button");

        button.type = "button";
        button.className = className;
        button.textContent = label;

        button.addEventListener("click", callback);

        return button;
    }


    function createYesNoBadge(value) {

        const badge = document.createElement("span");

        badge.className = value
            ? "badge bg-success"
            : "badge bg-secondary";

        badge.textContent = value ? "Yes" : "No";

        return badge;
    }


    // ==========================================
    // 2. LOAD ALL WASTE CATEGORIES
    // ==========================================

    async function loadCategories() {

        if (!categoriesTableBody) {
            return;
        }

        showCategoryTableMessage(
            "Loading waste categories..."
        );

        try {

            const response =
                await categoryApiRequest(API_URL);

            allCategories = Array.isArray(response)
                ? response
                : [];

            updateCategoryStatistics();

            displayCategories();

        } catch (error) {

            console.error(
                "Waste categories loading error:",
                error
            );

            showCategoryTableMessage(
                "Unable to load categories: " + error.message,
                true
            );
        }
    }


    // ==========================================
    // 3. UPDATE CATEGORY STATISTICS
    // ==========================================

    function updateCategoryStatistics() {

        setCategoryText(
            "totalWasteCategories",
            allCategories.length
        );

        setCategoryText(
            "totalRecyclableCategories",
            allCategories.filter(
                category => category.recyclable === true
            ).length
        );

        setCategoryText(
            "totalHazardousCategories",
            allCategories.filter(
                category => category.hazardous === true
            ).length
        );
    }


    // ==========================================
    // 4. DISPLAY CATEGORIES TABLE
    // ==========================================

    function displayCategories() {

        if (!categoriesTableBody) {
            return;
        }

        const searchText = (categorySearch?.value || "")
            .toLowerCase()
            .trim();

        const filteredCategories =
            allCategories.filter(category => {

                const searchable = [
                    category.categoryId,
                    category.categoryName
                ].join(" ").toLowerCase();

                return searchable.includes(searchText);
            });

        categoriesTableBody.replaceChildren();

        setCategoryText(
            "categoryResultsCount",
            "Showing " + filteredCategories.length +
            " of " + allCategories.length + " categories"
        );

        if (filteredCategories.length === 0) {

            showCategoryTableMessage(
                "No matching waste categories found."
            );

            return;
        }

        filteredCategories.forEach(category => {

            const row = categoriesTableBody.insertRow();

            row.insertCell().textContent =
                category.categoryId ?? "—";

            row.insertCell().textContent =
                category.categoryName || "—";

            row.insertCell().appendChild(
                createYesNoBadge(category.recyclable)
            );

            row.insertCell().appendChild(
                createYesNoBadge(category.hazardous)
            );

            const actionCell = row.insertCell();

            const actions = document.createElement("div");

            actions.className =
                "d-flex flex-wrap gap-2";

            actions.appendChild(
                createCategoryButton(
                    "View",
                    "btn btn-sm btn-outline-primary",
                    function () {
                        viewCategory(category.categoryId);
                    }
                )
            );

            actions.appendChild(
                createCategoryButton(
                    "Edit",
                    "btn btn-sm btn-outline-success",
                    function () {
                        editCategory(category.categoryId);
                    }
                )
            );

            actionCell.appendChild(actions);
        });
    }


    // ==========================================
    // 5. SEARCH CATEGORIES
    // ==========================================

    categorySearch?.addEventListener(
        "input",
        displayCategories
    );


    // ==========================================
    // 6. VIEW CATEGORY DETAILS
    // ==========================================

    async function viewCategory(categoryId) {

        try {

            const category = await categoryApiRequest(
                API_URL + "/" + categoryId
            );

            setCategoryText(
                "modalCategoryId",
                category.categoryId
            );

            setCategoryText(
                "modalCategoryName",
                category.categoryName
            );

            setCategoryText(
                "modalCategoryRecyclable",
                category.recyclable ? "Yes" : "No"
            );

            setCategoryText(
                "modalCategoryHazardous",
                category.hazardous ? "Yes" : "No"
            );

            showCategoryModal("categoryDetailsModal");

        } catch (error) {

            showCategoryMessage(
                "Unable to load category: " + error.message,
                false
            );
        }
    }


    // ==========================================
    // 7. ADD NEW WASTE CATEGORY
    // ==========================================

    const addCategoryForm =
        document.getElementById("addCategoryForm");

    addCategoryForm?.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const button =
                document.getElementById(
                    "saveAddCategoryBtn"
                );

            const message =
                document.getElementById(
                    "addCategoryMessage"
                );

            const categoryName =
                document.getElementById(
                    "addCategoryName"
                ).value.trim();

            const request = {
                categoryName: categoryName,
                recyclable:
                    document.getElementById(
                        "addCategoryRecyclable"
                    ).value === "true",
                hazardous:
                    document.getElementById(
                        "addCategoryHazardous"
                    ).value === "true"
            };

            if (message) {
                message.textContent = "";
            }

            if (!categoryName) {
                if (message) {
                    message.textContent =
                        "Category name is required.";
                }
                return;
            }

            if (button) {
                button.disabled = true;
            }

            try {

                await categoryApiRequest(API_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(request)
                });

                hideCategoryModal("addCategoryModal");

                addCategoryForm.reset();

                await loadCategories();

                showCategoryMessage(
                    "Waste category added successfully."
                );

            } catch (error) {

                if (message) {
                    message.textContent = error.message;
                }

            } finally {

                if (button) {
                    button.disabled = false;
                }
            }
        }
    );


    // ==========================================
    // 8. OPEN EDIT CATEGORY MODAL
    // ==========================================

    async function editCategory(categoryId) {

        try {

            const category = await categoryApiRequest(
                API_URL + "/" + categoryId
            );

            setCategoryInput(
                "editCategoryId",
                category.categoryId
            );

            setCategoryInput(
                "editCategoryName",
                category.categoryName
            );

            setCategoryInput(
                "editCategoryRecyclable",
                String(category.recyclable)
            );

            setCategoryInput(
                "editCategoryHazardous",
                String(category.hazardous)
            );

            setCategoryText(
                "editCategoryMessage",
                ""
            );

            showCategoryModal("editCategoryModal");

        } catch (error) {

            showCategoryMessage(
                "Unable to load category: " + error.message,
                false
            );
        }
    }


    // ==========================================
    // 9. UPDATE WASTE CATEGORY
    // ==========================================

    const editCategoryForm =
        document.getElementById("editCategoryForm");

    editCategoryForm?.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();

            const categoryId =
                document.getElementById(
                    "editCategoryId"
                ).value;

            const button =
                document.getElementById(
                    "saveEditCategoryBtn"
                );

            const message =
                document.getElementById(
                    "editCategoryMessage"
                );

            const categoryName =
                document.getElementById(
                    "editCategoryName"
                ).value.trim();

            if (!/^\d+$/.test(categoryId)) {
                if (message) {
                    message.textContent =
                        "Invalid category ID.";
                }
                return;
            }

            if (!categoryName) {
                if (message) {
                    message.textContent =
                        "Category name is required.";
                }
                return;
            }

            const request = {
                categoryName: categoryName,
                recyclable:
                    document.getElementById(
                        "editCategoryRecyclable"
                    ).value === "true",
                hazardous:
                    document.getElementById(
                        "editCategoryHazardous"
                    ).value === "true"
            };

            if (message) {
                message.textContent = "";
            }

            if (button) {
                button.disabled = true;
            }

            try {

                await categoryApiRequest(
                    API_URL + "/" + categoryId,
                    {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(request)
                    }
                );

                hideCategoryModal("editCategoryModal");

                await loadCategories();

                showCategoryMessage(
                    "Waste category updated successfully."
                );

            } catch (error) {

                if (message) {
                    message.textContent = error.message;
                }

            } finally {

                if (button) {
                    button.disabled = false;
                }
            }
        }
    );


    // ==========================================
    // 10. REFRESH CATEGORIES
    // ==========================================

    document.getElementById(
        "refreshCategoriesBtn"
    )?.addEventListener("click", loadCategories);


    // ==========================================
    // 11. LOAD WHEN CATEGORIES SECTION OPENS
    // ==========================================

    const categoriesNavigationLink =
        document.querySelector(
            '.sidebar-nav .nav-link[data-section="categories"]'
        );

    categoriesNavigationLink?.addEventListener(
        "click",
        loadCategories
    );

    window.addEventListener("hashchange", function () {

        if (window.location.hash === "#categories") {
            loadCategories();
        }
    });


    // ==========================================
    // 12. INITIAL PAGE LOAD
    // ==========================================

    if (window.location.hash === "#categories") {
        loadCategories();
    }

});

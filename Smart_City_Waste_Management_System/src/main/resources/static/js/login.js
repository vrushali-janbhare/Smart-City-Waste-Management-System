
document.addEventListener("DOMContentLoaded", function () {

    const loginForm = document.getElementById("loginForm");
    const message = document.getElementById("message");
    const loginButton = document.getElementById("loginButton");

    const password = document.getElementById("password");
    const togglePassword = document.getElementById("togglePassword");


    // Show and Hide Password
    togglePassword.addEventListener("click", function () {

        if (password.type === "password") {

            password.type = "text";
            togglePassword.textContent = "Hide";

        } else {

            password.type = "password";
            togglePassword.textContent = "Show";

        }

    });


    // Login Form Submission
    loginForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const enteredPassword = password.value;

        message.textContent = "";
        message.className = "message";


        // Validate Empty Fields
        if (email === "" || enteredPassword === "") {

            message.textContent = "Please enter email and password.";
            message.classList.add("error");

            return;

        }


        // Disable Button During Login
        loginButton.disabled = true;
        loginButton.textContent = "Logging in...";


        try {

            // Send Login Request to Spring Boot Backend
            const response = await fetch("/api/users/login", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    email: email,
                    password: enteredPassword
                })

            });


            const result = await response.json();


            // Successful Login
            if (response.ok) {

                message.textContent = "Login successful!";
                message.classList.add("success");

                // Redirect to User Dashboard
              // Redirect according to user role
setTimeout(function () {

    const role = result.roleName;

    if (role === "USER") {

        window.location.href = "user-dashboard.html";

    } 
    else if (role === "WORKER") {

        window.location.href = "worker-dashboard.html";

    } 
    else if (role === "ADMIN") {

        window.location.href = "/admin/admin-dashboard.html";

    } 
    else {

        message.textContent = "Invalid user role!";
        message.className = "message error";

    }

}, 1000);


            } else {

                // Login Failed
                message.textContent =
                    result.message || "Invalid email or password.";

                message.classList.add("error");

            }


        } catch (error) {

            // Server Connection Error
            message.textContent =
                "Unable to connect to server. Please try again.";

            message.classList.add("error");

            console.error("Login Error:", error);


        } finally {

            loginButton.disabled = false;
            loginButton.textContent = "Login";

        }

    });

});

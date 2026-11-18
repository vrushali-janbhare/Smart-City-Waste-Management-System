
document.addEventListener("DOMContentLoaded", function () {

    const signupForm = document.getElementById("signupForm");
    const message = document.getElementById("message");
    const signupButton = document.getElementById("signupButton");

    // Display success or error message
    function showMessage(text, type) {
        message.textContent = text;
        message.className = "message " + type;
    }

    // Signup form submission
    signupForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const signupData = {

            fullName: document.getElementById("fullName").value.trim(),
            email: document.getElementById("email").value.trim(),
            phoneNumber: document.getElementById("phoneNumber").value.trim(),
            address: document.getElementById("address").value.trim(),
            password: document.getElementById("password").value,
            confirmPassword: document.getElementById("confirmPassword").value

        };

        // Clear previous message
        message.textContent = "";
        message.className = "message";

        // Check empty fields
        if (
            !signupData.fullName ||
            !signupData.email ||
            !signupData.phoneNumber ||
            !signupData.address ||
            !signupData.password ||
            !signupData.confirmPassword
        ) {

            showMessage("Please fill in all fields.", "error");
            return;

        }

        // Check phone number
        if (!/^[0-9]{10}$/.test(signupData.phoneNumber)) {

            showMessage(
                "Please enter a valid 10-digit phone number.",
                "error"
            );
            return;

        }

        // Check password length
        if (signupData.password.length < 8) {

            showMessage(
                "Password must contain at least 8 characters.",
                "error"
            );
            return;

        }

        // Check password confirmation
        if (signupData.password !== signupData.confirmPassword) {

            showMessage("Passwords do not match.", "error");
            return;

        }

        // Disable button
        signupButton.disabled = true;
        signupButton.textContent = "Creating Account...";

        try {

            // Send data to Spring Boot backend
            const response = await fetch("/api/users/register", {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(signupData)

            });

            // Read backend response
            const result = await response.json();

            // Registration successful
            if (response.ok) {

                showMessage(
                    result.message || "Registration successful!",
                    "success"
                );

                signupForm.reset();

                // Redirect to login page
                setTimeout(function () {
                    window.location.href = "/login.html";
                }, 1500);

            } else {

                showMessage(
                    result.message ||
                    result.error ||
                    "Registration failed. Please try again.",
                    "error"
                );

            }

        } catch (error) {

            console.error("Signup Error:", error);

            showMessage(
                "Unable to connect to the server. Please try again.",
                "error"
            );

        } finally {

            signupButton.disabled = false;
            signupButton.textContent = "Create Account";

        }

    });

});

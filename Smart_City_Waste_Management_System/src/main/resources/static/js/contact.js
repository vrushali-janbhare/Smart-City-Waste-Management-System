
document.addEventListener("DOMContentLoaded", function () {

    const contactForm = document.getElementById("contactForm");

    contactForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const contactData = {
            fullName: document.getElementById("fullName").value.trim(),
            email: document.getElementById("email").value.trim(),
            subject: document.getElementById("subject").value.trim(),
            enquiryType: document.getElementById("enquiryType").value,
            message: document.getElementById("message").value.trim()
        };

        try {

            const response = await fetch("/api/contact-messages", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(contactData)
            });

            if (response.ok) {

                const result = await response.json();

                alert("Your message has been submitted successfully!");

                contactForm.reset();

                console.log("Saved Contact Message:", result);

            } else {

                alert("Unable to submit your message. Please try again.");

            }

        } catch (error) {

            console.error("Error:", error);

            alert("Server connection failed. Please try again later.");

        }

    });

});

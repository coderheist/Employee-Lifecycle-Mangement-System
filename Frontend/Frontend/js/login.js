const API_BASE_URL = "http://localhost:8080/api/auth";

/**
* Handles the form submission event, authenticates with the backend,
* and routes the user based on their server-side assigned role.
*/
async function handleLogin(event) {
    // 1. Prevent the HTML form from refreshing the page on submit
    event.preventDefault();
 
    // 2. Grab inputs from your HTML structure
    const usernameInput = document.getElementById("username").value;
    const passwordInput = document.getElementById("password").value;
 
    // Grab UI element references for dynamic feedback
    const errorBox = document.getElementById("errorBox");
    const errorMsg = document.getElementById("errorMsg");
    const btnText = document.getElementById("btnText");
    const btnSpinner = document.getElementById("btnSpinner");
    const loginBtn = document.getElementById("loginBtn");
 
    // Always hide previous error messages when a new attempt begins
    errorBox.classList.add("d-none");
 
    // ==========================================
    //   BASIC CLIENT-SIDE VALIDATION LAYER
    // ==========================================
    if (!usernameInput.trim()) {
        showErrorMessage("Username field cannot be left blank.", errorBox, errorMsg);
        return;
    }

    if (passwordInput.length < 4) {
        showErrorMessage("Password is too short (Minimum 4 characters required).", errorBox, errorMsg);
        return;
    }

    // 3. UI Feedback: Show loading spinner and disable login button
    btnText.classList.add("d-none");
    btnSpinner.classList.remove("d-none");
    loginBtn.disabled = true;
 
    // 4. Build JSON Payload matching Backend's LoginRequest DTO (Strictly user/pass)
    const loginPayload = {
        username: usernameInput.trim(),
        password: passwordInput
    };
 
    try {
        // 5. Send POST request to Spring Boot REST Endpoint
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginPayload),
            credentials: "include" // CRITICAL: Captures and saves the JSESSIONID cookie safely
        });
 
        // 6. Handle Backend Response
        if (response.ok) {
            const data = await response.json(); // returns { username, role, message }
 
            // Defensive Check: Ensure backend returned a valid role
            if (!data || !data.role) {
                showErrorMessage("Authentication succeeded, but no user role was assigned to this account.", errorBox, errorMsg);
                resetLoadingUI(btnText, btnSpinner, loginBtn);
                return;
            }

            // Save user data or status to session storage for dashboard navbar tracking
            sessionStorage.setItem("currentUser", data.username);
            sessionStorage.setItem("userRole", data.role);
 
            // 7. Role-Based Page Redirection Engine (Evaluates server string)
            switch (data.role) {
                case 'EMPLOYEE':
                    window.location.href = "../html/employee.html";
                    break;
                case 'PAYROLL_OFFICER':
                    window.location.href = "../html/payroll.html";
                    break;
                case 'APPRAISAL_OFFICER':
                    window.location.href = "../html/appraisal-dashboard.html";
                    break;
                case 'MANAGER':
                    window.location.href = "../html/manager.html";
                    break;
                case 'HR_RECRUITER':
                    window.location.href = "../html/dashboard-recruiter.html";
                    break;
                default:
                    showErrorMessage("Invalid role mapping route configured.", errorBox, errorMsg);
                    resetLoadingUI(btnText, btnSpinner, loginBtn);
            }
        } else {
            // ==========================================
            //   SERVER ERROR VALIDATION LAYER
            // ==========================================
            let fallbackError = "Invalid credentials or unauthorized account context.";
            
            if (response.status === 401) {
                fallbackError = "Incorrect username or password. Please try again.";
            } else if (response.status === 404) {
                fallbackError = "Account not found. Check your credentials.";
            } else if (response.status === 403) {
                fallbackError = "Access denied. Your account is not authorized to log in.";
            } else if (response.status >= 500) {
                fallbackError = "Internal server error. Please try again later.";
            }
 
            try {
                const errData = await response.json();
                if (errData) {
                    fallbackError = errData.error || errData.message || fallbackError;
                }
            } catch (e) { 
                /* Handle plain text or fallback default safely */
            }
 
            showErrorMessage(fallbackError, errorBox, errorMsg);
            resetLoadingUI(btnText, btnSpinner, loginBtn);
        }
 
    } catch (error) {
        console.error("Network interface error context:", error);
        showErrorMessage("Cannot connect to server. Ensure your backend application is running.", errorBox, errorMsg);
        resetLoadingUI(btnText, btnSpinner, loginBtn);
    }
}
 
/**
* Utility helper to show the error alert box
*/
function showErrorMessage(message, box, targetSpan) {
    targetSpan.textContent = message;
    box.classList.remove("d-none");
}
 
/**
* Utility helper to clear the loading spinner state if authorization fails
*/
function resetLoadingUI(text, spinner, button) {
    text.classList.remove("d-none");
    spinner.classList.add("d-none");
    button.disabled = false;
}
 
/**
* Password Visibility Toggle Helper Function
*/
function togglePassword() {
    const passwordInput = document.getElementById("password");
    const toggleIcon = document.getElementById("toggleIcon");
 
    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        toggleIcon.classList.remove("bi-eye");
        toggleIcon.classList.add("bi-eye-slash");
    } else {
        passwordInput.type = "password";
        toggleIcon.classList.remove("bi-eye-slash");
        toggleIcon.classList.add("bi-eye");
    }
}

// ==========================================
//   SAFE COMPONENT EVENT INITIALIZATION
// ==========================================
document.addEventListener("DOMContentLoaded", () => {
    // Attach the submission handler safely to the form
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", handleLogin);
    }

    // Attach the password toggle visibility logic safely to the button
    const toggleBtn = document.getElementById("toggleBtn");
    if (toggleBtn) {
        toggleBtn.addEventListener("click", togglePassword);
    }
});
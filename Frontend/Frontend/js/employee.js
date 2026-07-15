/**
 * Employee HRMS Portal Workspace Engine
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Fetch dashboard metrics immediately when the page loads
    fetchDashboardData();

    // DOM Element Selectors
    const btnMarkAttendance = document.getElementById('btnMarkAttendance');
    const btnSubmitTimesheet = document.getElementById('btnSubmitTimesheet');
    const leaveApplicationForm = document.getElementById('leaveApplicationForm');

    // Event Listeners for Shift Action Buttons
    if (btnMarkAttendance) {
        btnMarkAttendance.addEventListener('click', async () => {
            await handleShiftAction('http://localhost:8080/api/leave/mark-attendance', 'Attendance marked successfully!');
        });
    }

    if (btnSubmitTimesheet) {
        btnSubmitTimesheet.addEventListener('click', async () => {
            await handleShiftAction('http://localhost:8080/api/leave/submit-timesheet', 'Timesheet submitted successfully!');
        });
    }

    // Form submission triggers the async API function
    if (leaveApplicationForm) {
        leaveApplicationForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            await submitLeaveApplication();
        });
    }
});

/**
 * Dynamic Toast Alert Controller
 */
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;

    toast.innerText = message;
    toast.className = 'toast';
    if (type === 'success') toast.classList.add('success');
    if (type === 'error') toast.classList.add('error');
    
    toast.style.display = 'block';
    setTimeout(() => {
        toast.style.display = 'none';
    }, 4000);
}

/**
 * Gets overview status lists from backend REST API
 */
async function fetchDashboardData() {
    try {
        const response = await fetch('http://localhost:8080/api/leave', {
            method: 'GET',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) throw new Error(`Network failure tracking metrics: ${response.status}`);

        const homeData = await response.json();
        console.log("Server Data Received:", homeData); // Helpful debugging checkpoint

        // Decouple objects into separate rendering functions
        renderLeaveChips(homeData.leaveDto);
        renderLeaveHistoryTable(homeData.attendanceList);
        renderAttendanceLogTable(homeData.markAttendanceList);

        await checkTodayShiftStatus(); // Ensure buttons reflect current shift state

    } catch (error) {
        console.error('Error compiling view metrics:', error);
        showToast("Failed to fetch dashboard updates from server", "error");
    }
}

/**
 * Updates top metrics boxes with remaining balances
 */
function renderLeaveChips(leaveDTO) {
    if (!leaveDTO) return;

    // Adjusted properties to match LeaveDto.java names: sickLeave, casualLeave, earnedLeave
    document.getElementById('countSick').innerText = leaveDTO.sickLeave !== undefined ? leaveDTO.sickLeave : 0;
    document.getElementById('countCasual').innerText = leaveDTO.casualLeave !== undefined ? leaveDTO.casualLeave : 0;
    document.getElementById('countEarned').innerText = leaveDTO.earnedLeave !== undefined ? leaveDTO.earnedLeave : 0;
}

/**
 * Submit form data to the backend REST API
 */
async function submitLeaveApplication() {
    const leaveTypeElement = document.getElementById('leaveType');
    const fromDateElement = document.getElementById('fromDate');
    const toDateElement = document.getElementById('toDate');

    const leaveType = leaveTypeElement.value;
    const fromDate = fromDateElement.value;
    const toDate = toDateElement.value;

    if (!leaveType || !fromDate || !toDate) {
        showToast('Please fill all required fields', 'error');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/leave/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                leaveType: leaveType.toUpperCase(), 
                fromDate: fromDate,
                toDate: toDate
            }),
            credentials: 'include' // Ensures session cookie is sent for authentication
        });

        const statusMessage = await response.text();

        if (response.ok) {
            showToast(statusMessage || 'Leave application submitted successfully!', 'success');
            
            // Clear form elements cleanly
            leaveTypeElement.value = '';
            fromDateElement.value = '';
            toDateElement.value = '';
            
            // Re-fetch server values to automatically update tables and cards
            fetchDashboardData();
           
        } else {
            showToast("Please check the details",'error');
        }

    } catch (error) {
        console.error("Submission error:", error);
        showToast("Failed submitting leave application", 'error');
    }
}

/**
 * Reusable controller wrapper for Shift actions (Mark Attendance / Submit Timesheet)
 */
async function handleShiftAction(endpoint, fallbackSuccessMessage) {
    try {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include' // Ensures session cookie is sent for authentication
        });

        const responseText = await response.text();

        if (response.ok) {
            showToast(responseText || fallbackSuccessMessage, 'success');
            // Refresh dashboard components immediately to visually reflect state change
            fetchDashboardData();
        } else {
            showToast("You have already performed this action today. Please try again tomorrow.", 'error');
        }
    } catch (error) {
        console.error(`Error sending transaction request to ${endpoint}:`, error);
        showToast("Network execution failed. Please verify server connectivity.", 'error');
    }
}

/**
 * Populates tracked leave data table (Right Side)
 */
function renderLeaveHistoryTable(attendanceList) {
    const tableBody = document.getElementById('leaveHistoryTable');
    if (!tableBody) return;

    // Wipe out current contents
    tableBody.innerHTML = '';

    if (!attendanceList || attendanceList.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="6" style="text-align: center;">No tracked leaves found</td></tr>`;
        return;
    }

    attendanceList.forEach(item => {
        const totalDays = item.totalDays;
        const currentStatus = item.status || 'APPLIED';
        
        // Dynamic Class extraction per row item
        const rowBadgeClass = getStatusClass(currentStatus);

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.leaveId || 'N/A'}</td>
            <td>${item.leaveType || ''}</td>
            <td>${item.fromDate || ''}</td>
            <td>${item.toDate || ''}</td>
            <td>${totalDays || 0} ${totalDays === 1 ? 'Day' : 'Days'}</td>
            <td><span class="badge ${rowBadgeClass}">${currentStatus}</span></td>
        `;
        tableBody.appendChild(row);
    });
}

/**
 * Populates attendance check-in shift history logs (Left Side)
 */
function renderAttendanceLogTable(markAttendanceList) {
    const tableBody = document.getElementById('attendanceHistoryTable');
    if (!tableBody) return;

    // Wipe out historical data blocks
    tableBody.innerHTML = '';

    if (!markAttendanceList || markAttendanceList.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center;">No shift activities logged</td></tr>`;
        return;
    }

    markAttendanceList.forEach(log => {
        const currentStatus = log.attendanceStatus || 'PRESENT';
        
        // Dynamic Class extraction per row item
        const rowBadgeClass = getStatusClass(currentStatus);
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${log.presentDate || ''}</td>
            <td>${log.inTime|| '---'}</td>
            <td>${log.outTime || '---'}</td>
            <td>${log.totalHours || 0} hrs</td>
            <td><span class="badge ${rowBadgeClass}">${currentStatus}</span></td>
        `;
        tableBody.appendChild(row);
    });
}

function getStatusClass(status) {
    if (!status) return 'badge-pending'; 
    
    const normalizedStatus = status.toUpperCase().trim();

    switch (normalizedStatus) {
        case 'PENDING':
        case 'APPLIED':
            return 'badge-pending'; // Assigns Yellow styling map
        case 'PRESENT':
        case 'APPROVED':
            return 'badge-present'; // Assigns Green styling map
        case 'ABSENT':
        case 'REJECTED':
            return 'badge-absent';  // Assigns Red styling map
        default:
            return 'badge-pending'; // Fallback mapping case
    }
}

async function checkTodayShiftStatus() {
    const btnMarkAttendance = document.getElementById('btnMarkAttendance');
    const btnSubmitTimesheet = document.getElementById('btnSubmitTimesheet');

    try {
        const response = await fetch('http://localhost:8080/api/leave/today-status', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include' // Keep this here!
        });

        if (!response.ok) return;

        const textData = await response.text();
        if (!textData || textData.trim() === "") return; // Ensure it's not empty white space

        // Safe JSON parsing wrapper
        let todayLog;
        try {
            todayLog = JSON.parse(textData);
        } catch (e) {
            console.log("No JSON structure parsed from today-status endpoint.");
            return;
        }

        // Scenario 1: User has clocked in but hasn't clocked out yet
        if (todayLog && todayLog.inTime && !todayLog.outTime) {
            if (btnMarkAttendance) {
                btnMarkAttendance.disabled = true;
                btnMarkAttendance.innerText = "Already Clocked In";
                btnMarkAttendance.classList.add('btn-disabled'); 
            }
        }

        // Scenario 2: User has fully completed their shift cycle for today
        if (todayLog && todayLog.inTime && todayLog.outTime) {
            if (btnMarkAttendance) {
                btnMarkAttendance.disabled = true;
                btnMarkAttendance.innerText = "Shift Completed";
                btnMarkAttendance.classList.add('btn-disabled');
            }
            if (btnSubmitTimesheet) {
                btnSubmitTimesheet.disabled = true;
                btnSubmitTimesheet.innerText = "Timesheet Submitted";
                btnSubmitTimesheet.classList.add('btn-disabled');
            }
        }

    } catch (error) {
        console.warn("Could not determine modern daily shift lock parameters:", error);
    }
}
async function handleLogout() {
    try {
        const response = await fetch('http://localhost:8080/api/auth/logout', {
            method: 'POST', // Spring Security prefers POST requests for mutations like logout
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include' // CRITICAL: Sends the JSESSIONID cookie so the server knows which session to kill
        });
 
        if (response.ok) {
            // 1. Clear any frontend cached variables
            localStorage.removeItem("user_roles");
 
            // 2. Redirect back to the login page
            window.location.href = "../html/login.html";
        } else {
            console.error("Backend failed to clear session context.");
            // Fallback safety redirect
            window.location.href = "../html/login.html";
        }
    } catch (error) {
        console.error("Network error during logout operation:", error);
        // Force redirect to login even if the network fails
        window.location.href = "../html/login.html";
    }
}

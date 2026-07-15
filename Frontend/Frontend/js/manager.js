const API_BASE_URL = 'http://localhost:8080/api/manager';

document.addEventListener('DOMContentLoaded', () => {
    fetchLeaveRequests();
});

// --- TAB NAVIGATION LOGIC ---
function switchTab(tabName) {
    // Safely grab container panels
    const leaveSection = document.getElementById('leave-section');
    const payrollSection = document.getElementById('payroll-section');
    
    // Safely grab navigation tabs
    const tabLeave = document.getElementById('tab-leave');
    const tabPayroll = document.getElementById('tab-payroll');

    // Reset visual and layout display properties 
    if (leaveSection) leaveSection.style.display = 'none';
    if (payrollSection) payrollSection.style.display = 'none';
    if (tabLeave) tabLeave.classList.remove('active');
    if (tabPayroll) tabPayroll.classList.remove('active');

    // Mount active target items onto interface
    const targetSection = document.getElementById(`${tabName}-section`);
    const targetTab = document.getElementById(`tab-${tabName}`);

    if (targetSection) targetSection.style.display = 'block';
    if (targetTab) targetTab.classList.add('active');
}

// --- LEAVE LOGIC ---
async function fetchLeaveRequests() {
    const tbody = document.getElementById('leave-table-body');
    try {
        const response = await fetch(`${API_BASE_URL}/all-leaves`, { credentials: 'include' });
        if (!response.ok) throw new Error('Network execution fault accessing payload.');
        
        const leaves = await response.json();
        renderTableData(leaves);
    } catch (error) {
        console.error('Error handling fetch metrics routine:', error);
        showToast('Failed to extract updated leave records.');
        tbody.innerHTML = `<tr><td colspan="8" class="no-data" style="color: #dc2626;">Failed connection to HRMS services. Ensure your Spring Backend server is online.</td></tr>`;
    }
}

function renderTableData(leaves) {
    const tbody = document.getElementById('leave-table-body');
    tbody.innerHTML = '';

    if (!leaves || leaves.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" class="no-data">No active leave data files currently available.</td></tr>`;
        return;
    }

    leaves.forEach(item => {
        const tr = document.createElement('tr');
        const id = item.leaveId || item.id || 'N/A';
        const employeeId = item.employee?.employeeId || 'N/A';
        const type = item.leaveType || 'Regular';
        const start = item.fromDate || 'N/A';
        const end = item.toDate || 'N/A';
        const totalDays = item.totalDays || 'N/A';
        const status = item.status || 'applied';

        let statusClass = 'pill-applied';
        if(status.toLowerCase() === 'approved') statusClass = 'pill-approved';
        if(status.toLowerCase() === 'rejected') statusClass = 'pill-rejected';

        let actionsMarkup = `<span style="color: #9ca3af; font-style: italic;">Processed</span>`;
        if(status.toLowerCase() === 'applied' || status.toLowerCase() === 'pending') {
            actionsMarkup = `
            <div class="action-btn-group">
                <button class="btn-action btn-approve" onclick="processAction('approve', ${id})">
                    <i class="fa-solid fa-check"></i> Approve
                </button>
                <button class="btn-action btn-reject" onclick="processAction('reject', ${id})">
                    <i class="fa-solid fa-xmark"></i> Reject
                </button>
            </div>
            `;
        }

        tr.innerHTML = `
            <td><strong>#${id}</strong></td>
            <td>EMP-${employeeId}</td>
            <td><span class="pill" style="background:#e0e7ff; color:#4338ca;">${type}</span></td>
            <td>${start}</td>
            <td>${end}</td>
            <td><strong>${totalDays}</strong> days</td>
            <td><span class="pill ${statusClass}">${status}</span></td>
            <td align="right">${actionsMarkup}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function processAction(endpoint, leaveId) {
    try {
        const response = await fetch(`${API_BASE_URL}/${endpoint}/${leaveId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            credentials: 'include'
        });

        if (response.ok) {
            showToast(`Record requested #${leaveId} marked as ${endpoint}d successfully.`);
            fetchLeaveRequests();
        } else {
            throw new Error('Server application side fault executed rejection.');
        }
    } catch (error) {
        console.error(error);
        showToast(`Action execution error handling: ${endpoint}`);
    }
}

// --- PAYROLL LOGIC ---
async function loadTableData() {
    const monthInput = document.getElementById('tableFilterPeriod').value;
    const tbody = document.getElementById('payrollTableBody');

    if (!monthInput) {
        tbody.innerHTML = `<tr><td colspan="7" class="no-data">Please select a Pay Period to view records.</td></tr>`;
        return;
    }

    const url1 = `http://localhost:8080/api/payroll/dashboard?month=${monthInput}`;

    try {
        // Fetch payroll with credentials configured properly
        const response = await fetch(url1, { 
            method: 'GET',
            credentials: 'include' 
        });

        if (!response.ok) throw new Error('Failed to fetch payroll data');

        const data = await response.json();
        tbody.innerHTML = '';

        if (!data.payrolls || data.payrolls.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="no-data">No payroll records found for this period.</td></tr>`;
            return;
        }

        data.payrolls.forEach(p => {
            let empId = 'N/A';
            if (p.employeeId) {
                empId = p.employeeId;
            } else if (p.employee && p.employee.employeeId) {
                empId = p.employee.employeeId;
            } else if (p.employee && p.employee.id) {
                empId = p.employee.id;
            }

            const currentStatus = p.status || p.payrollStatus || 'Pending';
            const isPaid = currentStatus.toUpperCase() === 'PAID';
            let statusClass = isPaid ? 'pill-approved' : 'pill-pending';

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>#${p.payrollId || p.id || 'N/A'}</strong></td>
                <td>EMP-${empId}</td>
                <td>${p.payPeriod || 'N/A'}</td>
                <td>₹${(p.grossSalary || 0).toFixed(2)}</td>
                <td>₹${(p.totalDeductions || 0).toFixed(2)}</td>
                <td><strong>₹${(p.netSalary || 0).toFixed(2)}</strong></td>
                <td><span class="pill ${statusClass}">${currentStatus}</span></td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Error loading table data:", error);
        tbody.innerHTML = `<tr><td colspan="7" class="no-data" style="color: #dc2626;">Failed connection to HRMS services. Ensure your Spring Backend server is online.</td></tr>`;
    }
}

// --- UTILS & LOGOUT ---
function showToast(message) {
    const toast = document.getElementById('toast');
    toast.innerText = message;
    toast.style.display = 'block';
    setTimeout(() => { toast.style.display = 'none'; }, 4000);
}

async function handleLogout() {
    try {
        const response = await fetch('http://localhost:8080/api/auth/logout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include'
        });

        if (response.ok) {
            localStorage.removeItem("user_roles");
            window.location.href = "../html/login.html";
        } else {
            console.error("Backend failed to clear session context.");
            window.location.href = "../html/login.html";
        }
    } catch (error) {
        console.error("Network error during logout operation:", error);
        window.location.href = "../html/login.html";
    }
}